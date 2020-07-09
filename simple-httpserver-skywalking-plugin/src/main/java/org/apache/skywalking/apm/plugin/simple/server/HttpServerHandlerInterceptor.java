package org.apache.skywalking.apm.plugin.simple.server;

import com.mimu.simple.httpserver.core.request.SimpleHttpRequest;
import com.mimu.simple.httpserver.core.response.SimpleHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import org.apache.skywalking.apm.agent.core.context.CarrierItem;
import org.apache.skywalking.apm.agent.core.context.ContextCarrier;
import org.apache.skywalking.apm.agent.core.context.ContextManager;
import org.apache.skywalking.apm.agent.core.context.tag.Tags;
import org.apache.skywalking.apm.agent.core.context.trace.AbstractSpan;
import org.apache.skywalking.apm.agent.core.context.trace.SpanLayer;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.MethodInterceptResult;

import java.lang.reflect.Method;

public class HttpServerHandlerInterceptor implements InstanceMethodsAroundInterceptor {

    public void beforeMethod(EnhancedInstance enhancedInstance, Method method, Object[] objects, Class<?>[] classes, MethodInterceptResult methodInterceptResult) throws Throwable {
        SimpleHttpRequest request = (SimpleHttpRequest) objects[0];
        ContextCarrier contextCarrier = new ContextCarrier();
        CarrierItem next = contextCarrier.items();
        while (next.hasNext()) {
            next = next.next();
            next.setHeadValue(request.getHeaders().get(next.getHeadKey()));
        }
        AbstractSpan span = ContextManager.createEntrySpan(request.getUrl(), contextCarrier);
        Tags.URL.set(span, request.getUrl());
        Tags.HTTP.METHOD.set(span, request.getMethod().name());
        span.setComponent(SimpleHtttpServerConstants.serverComponent);
        SpanLayer.asHttp(span);
    }

    public Object afterMethod(EnhancedInstance enhancedInstance, Method method, Object[] objects, Class<?>[] classes, Object o) throws Throwable {
        SimpleHttpResponse response = (SimpleHttpResponse) objects[1];
        FullHttpResponse fullHttpResponse = response.getResponse();
        AbstractSpan span = ContextManager.activeSpan();
        if (fullHttpResponse.status().code() >= 400) {
            span.errorOccurred();
            Tags.STATUS_CODE.set(span, Integer.toString(fullHttpResponse.status().code()));
        }
        ContextManager.stopSpan();
        return o;
    }

    public void handleMethodException(EnhancedInstance enhancedInstance, Method method, Object[] objects, Class<?>[] classes, Throwable throwable) {
        AbstractSpan span = ContextManager.activeSpan();
        span.log(throwable);
        span.errorOccurred();
    }
}
