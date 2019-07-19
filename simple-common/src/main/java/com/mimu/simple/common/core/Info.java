package com.mimu.simple.common.core;

/**
 * author: mimu
 * date: 2019/6/24
 */
public class Info {
    private int code;
    private String desc;

    public Info() {
    }

    public Info(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
