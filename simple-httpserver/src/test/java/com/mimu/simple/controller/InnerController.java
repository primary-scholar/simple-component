package com.mimu.simple.controller;

import com.alibaba.fastjson.JSONObject;
import com.mimu.simple.core.FileItem;
import com.mimu.simple.core.SimpleHttpRequest;
import com.mimu.simple.core.SimpleHttpResponse;
import com.mimu.simple.core.annotation.SimpleController;
import com.mimu.simple.core.annotation.SimpleRequestUrl;
import com.mimu.simple.request.FeedRequest;
import com.mimu.simple.request.UserRequest;
import com.mimu.simple.response.FeedInfoData;
import com.mimu.simple.response.UserInfoData;
import com.mimu.simple.service.UserService;
import com.mimu.simple.util.ConvertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * author: mimu
 * date: 2018/10/28
 */
@SimpleController
public class InnerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(InnerController.class);
    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @SimpleRequestUrl(value = "/test/inner/info.go")
    public void getInfo(SimpleHttpRequest request, SimpleHttpResponse response) {
        try {
            UserRequest userRequest = ConvertUtil.convert(request, UserRequest.class);
            UserInfoData userInfoData = userService.getUserInfoData(userRequest);
            response.response(userInfoData);
            LOGGER.info("InnerController getInfo result={}", JSONObject.toJSONString(userInfoData));
        } catch (Exception e) {
            JSONObject result = new JSONObject();
            result.put("code", 500);
            result.put("msg", "something is wrong");
            response.response(result);
            LOGGER.error("InnerController getInfo error", e);
        }

    }

    @SimpleRequestUrl(value = "/test/inner/feed/info.go")
    public void getFeedInfo(SimpleHttpRequest request, SimpleHttpResponse response) {
        try {
            FeedRequest feedRequest = ConvertUtil.convert(request, FeedRequest.class);
            FeedInfoData feedInfoData = userService.getFeedInfoData(feedRequest);
            JSONObject result = new JSONObject();
            result.put("feedId", feedInfoData.getFeedId());
            result.put("name", feedInfoData.getName());
            int fileLength = 0;
            if (feedInfoData.getFileItem() == null) {
                result.put("fileNum", 0);
            } else {
                Iterator<Map.Entry<String, List<FileItem>>> iterator = feedInfoData.getFileItem().entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, List<FileItem>> next = iterator.next();
                    fileLength += next.getValue().get(0).getBytes().length;
                }
                result.put("fileNum", feedInfoData.getFileItem().size());
                result.put("fileLength", fileLength);
            }
            response.response(result);
            LOGGER.info("InnerController getFeedInfo result={}", result);
        } catch (Exception e) {
            JSONObject result = new JSONObject();
            result.put("code", 500);
            result.put("msg", "something is wrong");
            response.response(result);
            LOGGER.error("InnerController getFeedInfo error", e);
        }
    }

    @SimpleRequestUrl(url = "/test/inner/test.go")
    public void testInfo(SimpleHttpRequest request, SimpleHttpResponse response){
        try {
            UserRequest userRequest = ConvertUtil.convert(request,UserRequest.class);
            Thread.sleep(10000);
            JSONObject result = new JSONObject();
            result.put("code", 200);
            result.put("msg", "sleep over");
            response.response(result);
            LOGGER.info("InnerController testInfo result={}",result);
        } catch (Exception e) {
            JSONObject result = new JSONObject();
            result.put("code", 500);
            result.put("msg", "something is wrong");
            response.response(result);
            LOGGER.error("InnerController testInfo error", e);
        }
    }
}
