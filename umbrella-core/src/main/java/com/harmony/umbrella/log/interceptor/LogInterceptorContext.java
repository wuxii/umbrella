package com.harmony.umbrella.log.interceptor;

import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * 方法执行的日志拦截上下文
 *
 * @author wuxii
 */
public final class LogInterceptorContext {

    private MethodInvocation invocation;
    private Object result;
    private Throwable error;
    private long requestTime;
    private long responseTime;
    private LogTraceContext traceContext;

    protected LogInterceptorContext(MethodInvocation invocation) {
        this.invocation = invocation;
    }

    public void proceed() {
        traceContext = LogTraceContext.push();
        try {
            requestTime = System.currentTimeMillis();
            result = invocation.proceed();
        } catch (Throwable e) {
            error = e;
        } finally {
            responseTime = System.currentTimeMillis();
            LogTraceContext.pop();
        }
    }

    public Object[] getArgs() {
        return invocation.getArguments();
    }

    public Object getTarget() {
        return invocation.getThis();
    }

    public Class<?> getTargetClass() {
        return invocation.getThis().getClass();
    }

    public Method getMethod() {
        return invocation.getMethod();
    }

    public long getRequestTime() {
        return requestTime;
    }

    public long getResponseTime() {
        return responseTime;
    }

    public Object getResult() {
        return result;
    }

    public Throwable getError() {
        return error;
    }

    public LogTraceContext getTraceContext() {
        return traceContext;
    }

}
