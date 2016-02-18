/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.harmony.umbrella.log;

/**
 * 
 * @author wuxii@foxmail.com
 */
public class Logs {

    private static LogAdapter adapter;

    public static void init() {

    }

    /**
     * 从调用栈中找到上层类名的log
     */
    public static Log get() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        return adapter.getLogger(sts[2].getClassName());
    }

    public static Log getLog(String className) {
        return adapter.getLogger(className);
    }

    public static Log getLog(Class<?> clazz) {
        return adapter.getLogger(clazz.getName());
    }

}
