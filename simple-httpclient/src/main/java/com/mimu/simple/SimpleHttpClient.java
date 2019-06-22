package com.mimu.simple;

import com.mimu.simple.core.FileItem;
import com.mimu.simple.util.ConvertUtil;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SimpleHttpClient {
    private static final Logger logger = LoggerFactory.getLogger(SimpleHttpClient.class);
    private static volatile CloseableHttpClient httpClient;
    private static int connectionTimeOut = 3000;
    private static int readTimeOut = 3000;
    private static final Object lock = new Object();

    private SimpleHttpClient() {
    }

    static {
        initHttpClient();
    }

    public static CloseableHttpClient initHttpClient() {
        if (httpClient == null) {
            synchronized (lock) {
                if (httpClient == null) {
                    PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
                    poolingHttpClientConnectionManager.setMaxTotal(500);
                    poolingHttpClientConnectionManager.setDefaultMaxPerRoute(50);
                    httpClient = HttpClients.custom().setConnectionManager(poolingHttpClientConnectionManager).build();
                    if (logger.isDebugEnabled()) {
                        logger.debug("initHttpClient={}", httpClient.hashCode());
                    }
                }
            }
        }
        return httpClient;
    }

    public static RequestConfig getRequestConfig(int connectionTime, int readTime) {
        if (connectionTimeOut > 0) {
            connectionTimeOut = connectionTime;
        }
        if (readTimeOut > 0) {
            readTimeOut = readTime;
        }
        return RequestConfig.custom().setConnectTimeout(connectionTimeOut)
                .setSocketTimeout(readTimeOut).build();
    }

    public static String get(String url) {
        return get(url, null);
    }

    public static String get(String url, int connectionTimeOut, int readTimeOut) {
        return get(url, null, connectionTimeOut, readTimeOut);
    }

    public static String get(String url, Object object) {
        return get(url, object, connectionTimeOut, readTimeOut);
    }

    public static String get(String url, Object object, int connectionTimeOut, int readTimeOut) {
        try {
            return get(url, ConvertUtil.convert2Map(object), connectionTimeOut, readTimeOut);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String get(String url, Map<String, Object> para) {
        return get(url, para, connectionTimeOut, readTimeOut);
    }

    public static String get(String url, Map<String, Object> para, int connectionTimeOut, int readTimeOut) {
        String result = "";
        URIBuilder builder;
        try {
            builder = new URIBuilder(url);
            if (para != null) {
                List<NameValuePair> pairArrayList = new ArrayList<>();
                for (String paraKey : para.keySet()) {
                    Object value = para.get(paraKey);
                    if (value instanceof String) {
                        value = value.toString();
                    } else if (value instanceof Number) {
                        value = String.valueOf(value);
                    }
                    NameValuePair nameValuePair = new BasicNameValuePair(paraKey, value.toString());
                    pairArrayList.add(nameValuePair);
                }
                builder.addParameters(pairArrayList);
            }
            HttpGet httpGet = new HttpGet(builder.toString());
            httpGet.setConfig(getRequestConfig(connectionTimeOut, readTimeOut));
            long startTime = System.currentTimeMillis();
            CloseableHttpResponse response = httpClient.execute(httpGet);
            logger.info("HttpClientUtil get method url={},para={},cost={}ms", System.currentTimeMillis() - startTime);
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                result = EntityUtils.toString(responseEntity);
            }
            EntityUtils.consume(responseEntity);
        } catch (URISyntaxException | IOException e) {
            logger.error("HttpClientUtil get method url={},para={}", url, para, e);
        }
        return result;
    }

    public static String post(String url, Map<String, Object> para) {
        return post(url, para, connectionTimeOut, readTimeOut);
    }

    public static String post(String url, Object object) {
        return post(url, object, connectionTimeOut, readTimeOut);
    }

    public static String post(String url, Object object, int connectionTimeOut, int readTimeOut) {
        try {
            return post(url, ConvertUtil.convert2Map(object), connectionTimeOut, readTimeOut);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String post(String url, Map<String, Object> para, int connectionTimeOut, int readTimeOut) {
        String result = "";
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            HttpPost httpPost = new HttpPost(uriBuilder.toString());
            if (para != null) {
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                for (String paraKey : para.keySet()) {
                    Object value = para.get(paraKey);
                    populateParam(builder, paraKey, value);
                }
                HttpEntity entity = builder.build();
                httpPost.setEntity(entity);
            }
            httpPost.setConfig(getRequestConfig(connectionTimeOut, readTimeOut));
            long startTime = System.currentTimeMillis();
            CloseableHttpResponse response = httpClient.execute(httpPost);
            logger.info("HttpClientUtil post method url={},para={},cost={}ms", url, para, System.currentTimeMillis() - startTime);
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                result = EntityUtils.toString(responseEntity);
            }
            EntityUtils.consume(responseEntity);
        } catch (IOException | URISyntaxException e) {
            logger.error("HttpClientUtil post method url={},para={}", url, para, e);
        }
        return result;
    }

    private static void populateParam(MultipartEntityBuilder builder, String key, Object value) {
        ContentType defaultType = ContentType.create("text/plain", Consts.UTF_8);
        if (value instanceof Byte) {
            byte[] bytes = (byte[]) value;
            builder.addBinaryBody(key, bytes, ContentType.DEFAULT_BINARY, key);
        } else if (value instanceof FileItem) {
            FileItem item = (FileItem) value;
            builder.addBinaryBody(key, item.getBytes(), ContentType.create(item.getFileName()), item.getFileName());
        } else if (value instanceof String) {
            builder.addTextBody(key, value.toString(), defaultType);
        } else if (value instanceof Number) {
            value = String.valueOf(value);
            builder.addTextBody(key, value.toString(), defaultType);
        } else if (value instanceof List) {
            List list = (List) value;
            if (list.size() > 0) {
                Object element = list.get(0);
                if (element instanceof FileItem) {
                    FileItem item = (FileItem) element;
                    builder.addBinaryBody(key, item.getBytes(), ContentType.create(item.getFileName()), item.getFileName());
                }
            }
        } else if (value instanceof Map) {
            Map map = (Map) value;
            Iterator<Map.Entry> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Object nextValue = iterator.next();
                Object iteratorValue = ((Map.Entry) nextValue).getValue();
                if (iteratorValue instanceof List) {
                    List listValue = (List) iteratorValue;
                    for (int i = 0; i < listValue.size(); i++) {
                        Object element = listValue.get(i);
                        if (element instanceof FileItem) {
                            FileItem item = (FileItem) element;
                            builder.addBinaryBody(((Map.Entry) nextValue).getKey().toString(), item.getBytes(), ContentType.create(item.getFileName()), item.getFileName());
                        }
                    }
                }

            }
        }
    }
}
