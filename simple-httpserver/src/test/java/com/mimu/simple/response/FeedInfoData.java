package com.mimu.simple.response;

import com.mimu.simple.core.FileItem;

import java.util.List;
import java.util.Map;

/**
 * author: mimu
 * date: 2018/10/28
 */
public class FeedInfoData {
    private int feedId;
    private String name;
    private Map<String, List<FileItem>> fileItem;

    public int getFeedId() {
        return feedId;
    }

    public void setFeedId(int feedId) {
        this.feedId = feedId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, List<FileItem>> getFileItem() {
        return fileItem;
    }

    public void setFileItem(Map<String, List<FileItem>> fileItem) {
        this.fileItem = fileItem;
    }
}
