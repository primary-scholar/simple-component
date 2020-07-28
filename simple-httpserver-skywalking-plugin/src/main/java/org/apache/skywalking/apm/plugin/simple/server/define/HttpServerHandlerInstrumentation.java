package org.apache.skywalking.apm.plugin.simple.server.define;


import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.ClassInstanceMethodsEnhancePluginDefine;
import org.apache.skywalking.apm.agent.core.plugin.match.ClassMatch;

import static net.bytebuddy.matcher.ElementMatchers.named;
import static org.apache.skywalking.apm.agent.core.plugin.match.NameMatch.byName;

public class HttpServerHandlerInstrumentation extends ClassInstanceMethodsEnhancePluginDefine {

    private static final String ENHANCE_CLASS = "com.mimu.simple.httpserver.core.handler.ActionHandler";
    private static final String ENHANCE_METHOD = "invoke";
    private static final String INTERCEPTOR_CLASS = "org.apache.skywalking.apm.plugin.simple.server.HttpServerHandlerInterceptor";

    public ConstructorInterceptPoint[] getConstructorsInterceptPoints() {
        return null;
    }

    public InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return new InstanceMethodsInterceptPoint[]{
                new InstanceMethodsInterceptPoint() {
                    public ElementMatcher<MethodDescription> getMethodsMatcher() {
                        return named(ENHANCE_METHOD);
                    }

                    public String getMethodsInterceptor() {
                        return INTERCEPTOR_CLASS;
                    }

                    public boolean isOverrideArgs() {
                        return false;
                    }
                }
        };
    }

    protected ClassMatch enhanceClass() {
        return byName(ENHANCE_CLASS);
    }
}
