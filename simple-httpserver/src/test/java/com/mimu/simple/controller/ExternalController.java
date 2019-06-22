package com.mimu.simple.controller;

import com.alibaba.fastjson.JSONObject;
import com.mimu.simple.core.FileItem;
import com.mimu.simple.core.SimpleHttpRequest;
import com.mimu.simple.core.SimpleHttpResponse;
import com.mimu.simple.core.annotation.SimpleController;
import com.mimu.simple.core.annotation.SimpleRequestUrl;
import com.mimu.simple.request.FeedRequest;
import com.mimu.simple.request.UserRequest;
import com.mimu.simple.service.UserService;
import com.mimu.simple.util.ConvertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * author: mimu
 * date: 2018/10/28
 */
@SimpleController
public class ExternalController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalController.class);
    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @SimpleRequestUrl(value = "/test/external/info.go")
    public void getInfo(SimpleHttpRequest request, SimpleHttpResponse response) {
        try {
            UserRequest userRequest = ConvertUtil.convert(request, UserRequest.class);
            String url = "http://127.0.0.1:8080/test/inner/info.go";
            Map<String, Object> para = new HashMap<>();
            para.put("pid", userRequest.getPid());
            para.put("name", userRequest.getName());
            /*String result = HttpClientUtil.get(url, userRequest);
            response.response(result);
            LOGGER.info("externalController getInfo result={}", result);*/
        } catch (Exception e) {
            JSONObject result = new JSONObject();
            result.put("code", 500);
            result.put("msg", "something is wrong");
            response.response(result);
            LOGGER.error("externalController getInfo error", e);
        }
    }

    @SimpleRequestUrl(url = "/test/external/feed/info.go")
    public void getFeedInfo(SimpleHttpRequest request, SimpleHttpResponse response) {
        try {
            FeedRequest feedRequest = ConvertUtil.convert(request, FeedRequest.class);
            String url = "http://127.0.0.1:8080/test/inner/feed/info.go";
            Map<String, Object> para = new HashMap<>();
            para.put("pid", feedRequest.getPid());
            para.put("feedId", feedRequest.getFeedId());
            para.put("name", feedRequest.getName());
            Map<String, List<FileItem>> requestFiles = feedRequest.getFiles();
            if (requestFiles != null) {
                Iterator<Map.Entry<String, List<FileItem>>> iterator = requestFiles.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, List<FileItem>> next = iterator.next();
                    para.put(next.getKey(), next.getValue().get(0));
                }
            }
            /*String result = HttpClientUtil.post(url, para);
            response.response(result);
            LOGGER.info("externalController getFeedInfo result={}", result);*/
        } catch (Exception e) {
            JSONObject result = new JSONObject();
            result.put("code", 500);
            result.put("msg", "something is wrong");
            response.response(result);
            LOGGER.error("externalController getFeedInfo error", e);
        }
    }
}
