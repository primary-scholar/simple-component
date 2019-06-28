package com.mimu.simple.httpclient;

import com.mimu.simple.httpserver.core.FileItem;
import com.mimu.simple.httpserver.util.ConvertUtil;
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
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.*;

/**
 * author: mimu
 * date: 2018/10/28
 */
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

    public static String get(String url) {
        return get(url, null);
    }

    public static Future<String> futureGet(String url) {
        return futureGet(url, null);
    }

    public static String get(String url, int connectionTimeOut, int readTimeOut) {
        return get(url, null, connectionTimeOut, readTimeOut);
    }

    public static Future<String> futureGet(String url, int connectionTimeOut, int readTimeOut) {
        return futureGet(url, null, connectionTimeOut, readTimeOut);
    }

    public static String get(String url, Object object) {
        return get(url, object, connectionTimeOut, readTimeOut);
    }

    public static Future<String> futureGet(String url, Object object) {
        return futureGet(url, object, connectionTimeOut, readTimeOut);
    }

    public static String get(String url, Object object, int connectionTimeOut, int readTimeOut) {
        try {
            return get(url, ConvertUtil.convert2Map(object), connectionTimeOut, readTimeOut);
        } catch (Exception e) {
            logger.error("get method error url={}", url, e);
        }
        return null;
    }

    public static Future<String> futureGet(String url, Object object, int connectionTimeOut, int readTimeOut) {
        try {
            return futureGet(url, ConvertUtil.convert2Map(object), connectionTimeOut, readTimeOut);
        } catch (Exception e) {
            logger.error("get method error url={}", url, e);
            return CompletableFuture.supplyAsync(() -> null);
        }
    }

    public static String get(String url, Map<String, Object> para) {
        return get(url, para, connectionTimeOut, readTimeOut);
    }

    public static Future<String> futureGet(String url, Map<String, Object> para) {
        return futureGet(url, para, connectionTimeOut, readTimeOut);
    }

    public static String get(String url, Map<String, Object> para, int connectionTimeOut, int readTimeOut) {
        return getMethod(url, para, connectionTimeOut, readTimeOut);
    }

    public static Future<String> futureGet(String url, Map<String, Object> para, int connectionTimeOut, int readTimeOut) {
        return CompletableFuture.supplyAsync(() -> getMethod(url, para, connectionTimeOut, readTimeOut));
    }

    private static String getMethod(String url, Map<String, Object> para, int connectionTimeOut, int readTimeOut) {
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
            logger.info("get method url={},para={},cost={}ms", url, para, System.currentTimeMillis() - startTime);
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                result = EntityUtils.toString(responseEntity);
            }
            EntityUtils.consume(responseEntity);
        } catch (URISyntaxException | IOException e) {
            logger.error("get method url={},para={}", url, para, e);
        }
        return result;
    }

    public static String post(String url, Map<String, Object> para) {
        return post(url, para, connectionTimeOut, readTimeOut);
    }

    public static Future<String> futurePost(String url, Map<String, Object> para) {
        return futurePost(url, para, connectionTimeOut, readTimeOut);
    }

    public static String post(String url, Object object) {
        return post(url, object, connectionTimeOut, readTimeOut);
    }

    public static Future<String> futurePost(String url, Object object) {
        return futurePost(url, object, connectionTimeOut, readTimeOut);
    }

    public static String post(String url, Object object, int connectionTimeOut, int readTimeOut) {
        try {
            return post(url, ConvertUtil.convert2Map(object), connectionTimeOut, readTimeOut);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Future<String> futurePost(String url, Object object, int connectionTimeOut, int readTimeOut) {
        try {
            return futurePost(url, ConvertUtil.convert2Map(object), connectionTimeOut, readTimeOut);
        } catch (Exception e) {
            logger.error("futurePost method error url={}", url, e);
            return CompletableFuture.supplyAsync(() -> null);
        }
    }

    public static String post(String url, Map<String, Object> para, int connectionTimeOut, int readTimeOut) {
        return postMethod(url, para, connectionTimeOut, readTimeOut);
    }

    public static Future<String> futurePost(String url, Map<String, Object> para, int connectionTimeOut, int readTimeOut) {
        return CompletableFuture.supplyAsync(() -> postMethod(url, para, connectionTimeOut, readTimeOut));
    }

    private static String postMethod(String url, Map<String, Object> para, int connectionTimeOut, int readTimeOut) {
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
            logger.info("post method url={},para={},cost={}ms", url, para, System.currentTimeMillis() - startTime);
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                result = EntityUtils.toString(responseEntity);
            }
            EntityUtils.consume(responseEntity);
        } catch (IOException | URISyntaxException e) {
            logger.error("post method url={},para={}", url, para, e);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
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
            Object element;
            if (list.size() > 0 && (element = list.get(0)) instanceof FileItem) {
                FileItem item = (FileItem) element;
                builder.addBinaryBody(key, item.getBytes(), ContentType.create(item.getFileName()), item.getFileName());
            }
        } else if (value instanceof Map) {
            Map map = (Map) value;
            Iterator<Map.Entry> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Object nextValue = iterator.next();
                Object iteratorValue = ((Map.Entry) nextValue).getValue();
                if (iteratorValue instanceof List) {
                    List listValue = (List) iteratorValue;
                    for (Object element : listValue) {
                        if (element instanceof FileItem) {
                            FileItem item = (FileItem) element;
                            builder.addBinaryBody(((Map.Entry) nextValue).getKey().toString(), item.getBytes(), ContentType.create(item.getFileName()), item.getFileName());
                        }
                    }
                }

            }
        }
    }

    private static void initHttpClient() {
        if (httpClient == null) {
            synchronized (lock) {
                if (httpClient == null) {
                    PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
                    connectionManager.setMaxTotal(500);
                    connectionManager.setDefaultMaxPerRoute(50);
                    httpClient = HttpClients.custom().setConnectionManager(connectionManager).setRetryHandler(new StandardHttpRequestRetryHandler(1, false)).build();
                    /**
                     * 服务端 关闭连接 客户端无感知，HttpClient为了缓解这一问题，
                     * 在某个连接使用前会检测这个连接是否过时，如果过时则连接失效，但是这种做法会为每个请求增加一定额外开销，
                     * 因此有一个定时任务专门回收长时间不活动而被判定为失效的连接，可以某种程度上解决这个问题
                     */
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            /**
                             * 关闭失效链接，并已出链接池
                             */
                            connectionManager.closeExpiredConnections();
                            /**
                             * 关闭不活跃链接
                             */
                            connectionManager.closeIdleConnections(20, TimeUnit.SECONDS);
                        }
                    }, 0, 1000 * 10);
                }
            }
        }
    }

    /**
     * @param connectionTime 链接建立时间
     * @param readTime       请求翻出后等待对方相应时间
     * @return
     */
    private static RequestConfig getRequestConfig(int connectionTime, int readTime) {
        if (connectionTimeOut > 0) {
            connectionTimeOut = connectionTime;
        }
        if (readTimeOut > 0) {
            readTimeOut = readTime;
        }
        /**
         * connectionRequestTimeOut 从链接池获取链接的 超时时间
         */
        int connectionRequestTimeOut = 3000;
        return RequestConfig.custom().setConnectTimeout(connectionTimeOut).setConnectionRequestTimeout(connectionRequestTimeOut)
                .setSocketTimeout(readTimeOut).build();
    }
}
