package com.mimu.simple.httpserver.core;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.*;

/**
 * author: mimu
 * date: 2018/10/21
 */
public class SimpleHttpRequest {
    private static final Logger logger = LoggerFactory.getLogger(SimpleHttpRequest.class);
    private Channel channel;
    private FullHttpRequest request;
    private String url;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> cookies = new HashMap<>();
    private Map<String, List<String>> parameters = new HashMap<>();
    private Map<String, List<FileItem>> files = new HashMap<>();


    public SimpleHttpRequest(Channel channel, FullHttpRequest request) {
        this.channel = channel;
        this.request = request;
        initFullUri();
    }

    public FullHttpRequest getRequest() {
        return request;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public String getUrl() {
        return url;
    }

    public String getString(String key) {
        return getPara(key);
    }

    public int getInt(String key) {
        return NumberUtils.toInt(getPara(key), 0);
    }

    public long getLong(String key) {
        return NumberUtils.toLong(getPara(key), 0);
    }

    public Map<String, List<FileItem>> getFiles() {
        return files;
    }

    public final String getRemoteAddress() {
        InetSocketAddress address = (InetSocketAddress) channel.remoteAddress();
        if (address == null) {
            return null;
        }
        InetAddress inetAddress = address.getAddress();
        if (inetAddress == null) {
            return null;
        }
        return inetAddress.getHostAddress();
    }

    /**
     * 获取 远程ip地址，需要nginx的支持，并开启 X-FORWARDED-FOR 选项
     *
     * @return
     */
    public final String getRemoteIpAddress() {
        String xff = null;
        for (String head : headers.keySet()) {
            if ("X-FORWARDED-FOR".equalsIgnoreCase(head)) {
                xff = headers.get(head);
            }
        }
        if (StringUtils.isNotEmpty(xff)) {
            int dotIndex = xff.indexOf(",");
            return dotIndex > 0 ? xff.substring(0, dotIndex) : xff;
        } else {
            return getRemoteAddress();
        }
    }

    private String getPara(String key) {
        if (parameters.containsKey(key)) {
            List<String> value = parameters.get(key);
            if (value == null) {
                return null;
            } else {
                return value.get(0);
            }
        } else {
            return null;
        }
    }


    public void parseRequest() {
        initHeaders();
        initCookies();
        initParameters();
    }

    public String initFullUri() {
        String fullUri = request.uri();
        try {
            fullUri = URLDecoder.decode(fullUri, "utf-8");
            QueryStringDecoder decoder = new QueryStringDecoder(fullUri);
            url = decoder.path();
            return fullUri;
        } catch (UnsupportedEncodingException e) {
            logger.error("initFullUri error", e);
        }
        return null;
    }

    private void initHeaders() {
        HttpHeaders httpHeaders = request.headers();
        for (Map.Entry<String, String> entry : httpHeaders.entries()) {
            headers.put(entry.getKey(), entry.getValue());
        }
    }

    private void initCookies() {
        if (headers.containsKey("Cookies")) {
            String cookieString = headers.get("Cookies");
            for (Cookie cookie : ServerCookieDecoder.STRICT.decode(cookieString)) {
                cookies.put(cookie.name(), cookie.value());
            }
        }
    }

    private void initParameters() {
        if (request.method().equals(HttpMethod.GET)) {
            QueryStringDecoder decoder = new QueryStringDecoder(Objects.requireNonNull(initFullUri()));
            Map<String, List<String>> requestParameters = decoder.parameters();
            for (Map.Entry<String, List<String>> entry : requestParameters.entrySet()) {
                parameters.put(entry.getKey(), entry.getValue());
            }
        } else if (request.method().equals(HttpMethod.POST)) {
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(request);
            for (InterfaceHttpData data : decoder.getBodyHttpDatas()) {
                try {
                    if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.FileUpload) {
                        FileUpload fileUpload = (FileUpload) data;
                        if (fileUpload.isCompleted()) {
                            List<FileItem> fileItemList = files.get(fileUpload.getName());
                            if (fileItemList == null) {
                                fileItemList = new ArrayList<>();
                            }
                            fileItemList.add(new FileItem(fileUpload.get(), fileUpload.getContentType(), fileUpload.getFilename()));
                            files.put(fileUpload.getName(), fileItemList);
                        }
                    } else if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                        Attribute attribute = (Attribute) data;
                        if (attribute.isCompleted()) {
                            List<String> para = parameters.get(attribute.getName());
                            if (para == null) {
                                para = new ArrayList<>();
                            }
                            para.add(attribute.getValue());
                            parameters.put(attribute.getName(), para);
                        }
                    }
                } catch (IOException e) {
                    logger.error("SimpleHttpRequest initParameters error", e);
                }
                data.release();
            }
        }

    }

}
