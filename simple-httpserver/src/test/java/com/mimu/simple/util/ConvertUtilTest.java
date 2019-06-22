package com.mimu.simple.util;

import com.mimu.simple.request.FeedRequest;
import org.junit.Test;

/**
 * author: mimu
 * date: 2018/10/28
 */
public class ConvertUtilTest {

    @Test
    public void convert2Map() {
        FeedRequest request = new FeedRequest();
        request.setFeedId(1234455555);
        request.setName("啦啦啦");
        try {
            System.out.println(ConvertUtil.convert2Map(request));
            System.out.print(ConvertUtil.convert2Map(null));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}