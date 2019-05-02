package com.harmony.umbrella.log.interceptor;

import com.harmony.umbrella.log.annotation.Binding;
import com.harmony.umbrella.log.annotation.Logging;
import com.harmony.umbrella.log.annotation.Module;
import com.harmony.umbrella.log.annotation.Scope;
import com.harmony.umbrella.util.StringUtils;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wuxii
 */
public abstract class AbstractLogInterceptor implements MethodInterceptor, LogInterceptor<MethodInvocation> {

    private LogInterceptorFilter logInterceptorFilter;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        return this.interceptor(invocation);
    }

    @Override
    public Object interceptor(MethodInvocation invocation) throws Throwable {
        LoggingOperation loggingOperation;
        if ((logInterceptorFilter != null && !logInterceptorFilter.accept(invocation))
                || (loggingOperation = getLoggingOperation(invocation)) == null) {
            return invocation.proceed();
        }

        LogInterceptorContext logInterceptorContext = new LogInterceptorContext(invocation);
        invokeWithLogging(loggingOperation, logInterceptorContext);

        if (logInterceptorContext.getError() != null) {
            throw logInterceptorContext.getError();
        }
        return logInterceptorContext.getResult();
    }

    protected abstract void invokeWithLogging(LoggingOperation loggingOperation, LogInterceptorContext logInterceptorContext);

    protected LoggingOperation getLoggingOperation(MethodInvocation invocation) {
        Method method = invocation.getMethod();
        Logging loggingAnn = AnnotationUtils.findAnnotation(method, Logging.class);
        if (loggingAnn == null) {
            return null;
        }

        String module = loggingAnn.module();
        if (StringUtils.isBlank(module)) {
            Module moduleAnn = AnnotationUtils.findAnnotation(method, Module.class);
            if (moduleAnn != null) {
                module = moduleAnn.value();
            }
        }

        ExpressionOperation keyExpression = StringUtils.isNotBlank(loggingAnn.key())
                ? new ExpressionOperation(loggingAnn.key(), loggingAnn.keyScope())
                : null;

        Map<String, ExpressionOperation> bindings = buildBindings(loggingAnn.bindings());

        return LoggingOperation
                .builder()
                .keyExpressionOperation(keyExpression)
                .module(module)
                .action(loggingAnn.action())
                .message(loggingAnn.message())
                .level(loggingAnn.level())
                .bindings(bindings)
                .build();
    }

    private Map<String, ExpressionOperation> buildBindings(Binding[] bindings) {
        Map<String, ExpressionOperation> result = new HashMap<>();
        for (Binding binding : bindings) {
            String key = binding.key();
            String exp = binding.expression();
            Scope scope = binding.scope();
            result.put(key, new ExpressionOperation(exp, scope));
        }
        return result;
    }

    public void setLogInterceptorFilter(LogInterceptorFilter logInterceptorFilter) {
        this.logInterceptorFilter = logInterceptorFilter;
    }

}
