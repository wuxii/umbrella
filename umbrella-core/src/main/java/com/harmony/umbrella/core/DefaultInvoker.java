package com.harmony.umbrella.core;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * 默认反射执行工具
 * 
 * @author wuxii@foxmail.com
 */
public class DefaultInvoker implements Invoker, Serializable {

    private static final long serialVersionUID = 5408528044216944899L;

    /**
     * 反射调用前是否进行检验
     */
    private boolean validate = false;

    @Override
    public Object invoke(Object obj, Method method, Object[] args) throws InvokeException {
        if (validate) {
            Class<?>[] pattern = method.getParameterTypes();
            if (args.length != pattern.length) {
                throw new InvokeException("parameter length mismatch");
            }
            if (!isAssignable(pattern, toTypeArray(args))) {
                throw new InvokeException("parameter type mismatch");
            }
        }
        try {
            return method.invoke(obj, args);
        } catch (Exception e) {
            throw new InvokeException(e.getMessage(), e);
        }
    }

    public boolean isValidate() {
        return validate;
    }

    /**
     * 设置是否调用前检验
     */
    public void setValidate(boolean validate) {
        this.validate = validate;
    }

    protected Class<?>[] toTypeArray(Object[] args) {
        if (args == null || args.length == 0) {
            return new Class<?>[0];
        }
        Class<?>[] parameterTypes = new Class[args.length];
        if (args != null && args.length > 0) {
            for (int i = 0, max = args.length; i < max; i++) {
                if (args[i] != null) {
                    parameterTypes[i] = args[i].getClass();
                } else {
                    parameterTypes[i] = Object.class;
                }
            }
        }
        return parameterTypes;
    }

    protected boolean isAssignable(Class<?>[] pattern, Class<?>[] inputTypes) {
        if (pattern.length != inputTypes.length)
            return false;
        for (int i = 0, max = pattern.length; i < max; i++) {
            if (!inputTypes[i].isAssignableFrom(pattern[i]))
                return false;
        }
        return true;
    }
}
