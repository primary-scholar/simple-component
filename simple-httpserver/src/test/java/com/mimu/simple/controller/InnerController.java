package com.mimu.simple.controller;

import com.alibaba.fastjson.JSONObject;
import com.mimu.simple.common.core.FileItem;
import com.mimu.simple.httpserver.core.SimpleHttpRequest;
import com.mimu.simple.httpserver.core.SimpleHttpResponse;
import com.mimu.simple.request.FeedRequest;
import com.mimu.simple.request.UserRequest;
import com.mimu.simple.response.FeedInfoData;
import com.mimu.simple.response.UserInfoData;
import com.mimu.simple.service.UserService;
import com.mimu.simple.httpserver.util.ConvertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * author: mimu
 * date: 2018/10/28
 */
@RestController
public class InnerController {
    private static final Logger logger = LoggerFactory.getLogger(InnerController.class);
    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/test/info.go")
    public void getInfo(SimpleHttpRequest request, SimpleHttpResponse response) {
        try {
            UserRequest userRequest = ConvertUtil.convert(request, UserRequest.class);
            UserInfoData userInfoData = userService.getUserInfoData(userRequest);
            response.response(userInfoData);
            logger.info("InnerController getInfo result={}", JSONObject.toJSONString(userInfoData));
        } catch (Exception e) {
            JSONObject result = new JSONObject();
            result.put("code", 500);
            result.put("msg", "something is wrong");
            response.response(result);
            logger.error("InnerController getInfo error", e);
        }

    }

    @RequestMapping(value = "/test/info1.go")
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
            logger.info("InnerController getFeedInfo result={}", result);
        } catch (Exception e) {
            JSONObject result = new JSONObject();
            result.put("code", 500);
            result.put("msg", "something is wrong");
            response.response(result);
            logger.error("InnerController getFeedInfo error", e);
        }
    }

    @RequestMapping(value = "/test/info2.go")
    public void testInfo(SimpleHttpRequest request, SimpleHttpResponse response){
        try {
            UserRequest userRequest = ConvertUtil.convert(request,UserRequest.class);
            Thread.sleep(10000);
            JSONObject result = new JSONObject();
            result.put("code", 200);
            result.put("msg", "sleep over");
            response.response(result);
            logger.info("InnerController testInfo result={}",result);
        } catch (Exception e) {
            JSONObject result = new JSONObject();
            result.put("code", 500);
            result.put("msg", "something is wrong");
            response.response(result);
            logger.error("InnerController testInfo error", e);
        }
    }
}
