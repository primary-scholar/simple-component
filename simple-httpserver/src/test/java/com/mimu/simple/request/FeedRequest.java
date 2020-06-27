package com.mimu.simple.request;


import com.mimu.simple.httpserver.core.model.FileItem;

import java.util.List;
import java.util.Map;

/**
 * author: mimu
 * date: 2018/10/28
 */
public class FeedRequest extends MiniRequest {
    private int feedId;
    private String name;
    private Map<String, List<FileItem>> files;

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

    public Map<String, List<FileItem>> getFiles() {
        return files;
    }

    public void setFiles(Map<String, List<FileItem>> files) {
        this.files = files;
    }
}
