package com.mimu.simple.service;

import com.mimu.simple.request.FeedRequest;
import com.mimu.simple.request.UserRequest;
import com.mimu.simple.response.FeedInfoData;
import com.mimu.simple.response.UserInfoData;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * author: mimu
 * date: 2018/10/28
 */
@Service
public class UserService {

    public UserInfoData getUserInfoData(UserRequest request) {
        UserInfoData userInfoData = new UserInfoData();
        userInfoData.setUserId(request.getPid());
        userInfoData.setUserName(request.getPid() + ":" + request.getName());
        return userInfoData;
    }

    public FeedInfoData getFeedInfoData(FeedRequest request) {
        FeedInfoData userInfoData = new FeedInfoData();
        userInfoData.setFeedId(request.getFeedId());
        userInfoData.setName(request.getName());
        if (CollectionUtils.isEmpty(request.getFiles())) {
            userInfoData.setFileItem(null);
        } else {
            userInfoData.setFileItem(request.getFiles());
        }
        return userInfoData;
    }
}
