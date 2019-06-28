package com.mimu.simple.httpserver.enums;

/**
 * author: mimu
 * date: 2018/10/22
 */
public enum ProtocolEnum {

    FILE("file"),
    JAR("jar"),
    HTTP("http"),
    HTTPS("https");

    private String protocol;

    ProtocolEnum(String protocol) {
        this.protocol = protocol;
    }

    public String protocol() {
        return protocol;
    }
}
