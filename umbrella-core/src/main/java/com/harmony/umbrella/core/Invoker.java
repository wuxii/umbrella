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
package com.harmony.umbrella.core;

import java.lang.reflect.Method;

/**
 * 反射执行目标类的方法
 * 
 * @author wuxii@foxmail.com
 */
public interface Invoker {

    /**
     * 执行目标方法
     * 
     * @param target
     *            目标实例
     * @param method
     *            调用的方法
     * @param args
     *            方法参数
     * @return
     * @throws InvokeException
     */
    Object invoke(Object target, Method method, Object[] args) throws InvokeException;

}