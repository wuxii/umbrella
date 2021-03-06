package com.harmony.umbrella.lock.interceptor;

import java.lang.reflect.Method;

/**
 * @author wuxii
 */
class LockExpressionRootObject {

	private final Method method;

	private final Object[] args;

	private final Object target;

	private final Class<?> targetClass;

	public LockExpressionRootObject(Method method, Object[] args, Object target, Class<?> targetClass) {
		this.method = method;
		this.args = args;
		this.target = target;
		this.targetClass = targetClass;
	}

	public Method getMethod() {
		return method;
	}

	public Object[] getArgs() {
		return args;
	}

	public Object getTarget() {
		return target;
	}

	public Class<?> getTargetClass() {
		return targetClass;
	}

}
