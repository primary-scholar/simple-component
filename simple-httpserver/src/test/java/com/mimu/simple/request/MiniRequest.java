package com.mimu.simple.request;

import org.springframework.util.StringUtils;

/**
 * author: mimu
 * date: 2018/10/28
 */
public class MiniRequest {
    private long pid;
    private long cid;
    private int apiVersion;
    private int u;
    private String v;
    private int platformId;

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public long getCid() {
        return cid;
    }

    public void setCid(long cid) {
        this.cid = cid;
    }

    public int getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(int apiVersion) {
        this.apiVersion = apiVersion;
    }

    public int getU() {
        return u;
    }

    public void setU(int u) {
        this.u = u;
    }

    public String getV() {
        if (StringUtils.isEmpty(v)) {
            return "";
        }
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }

    public int getPlatformId() {
        return platformId;
    }

    public void setPlatformId(int platformId) {
        this.platformId = platformId;
    }
}
