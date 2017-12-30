/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.harmony.umbrella.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * 关于异常的工具类.
 * <p/>
 * 参考了guava的Throwables。
 *
 * @author calvin
 */
public abstract class Exceptions {

    /**
     * 将CheckedException转换为UncheckedException.
     */
    public static RuntimeException unchecked(Throwable ex) {
        if (ex instanceof RuntimeException) {
            return (RuntimeException) ex;
        } else {
            return new UndeclaredThrowableException(ex);
        }
    }

    /**
     * 将ErrorStack转化为String.
     */
    public static String getStackTraceAsString(Throwable ex) {
        if (ex == null) {
            return null;
        }
        StringWriter writer = new StringWriter();
        ex.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }

    /**
     * 获取异常的Root Cause.
     */
    public static Throwable getRootCause(Throwable ex) {
        Throwable cause;
        while ((cause = ex.getCause()) != null) {
            ex = cause;
        }
        return ex;
    }

}
