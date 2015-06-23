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
package com.harmony.umbrella.monitor.graph;

import static com.harmony.umbrella.monitor.util.MonitorUtils.*;

import java.lang.reflect.Method;

import com.harmony.umbrella.monitor.MethodMonitor.MethodGraph;

/**
 * @author wuxii@foxmail.com
 */
public class DefaultMethodGraph extends AbstractGraph implements MethodGraph {

    protected Object target;
    protected final Method method;

    public DefaultMethodGraph(Method method) {
        this(null, method, null);
    }

    public DefaultMethodGraph(Object target, Method method, Object[] args) {
        super(methodIdentifie(method));
        this.target = target;
        this.method = method;
        this.setArguments(args);
    }

    public Object getTarget() {
        return target;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public void setArguments(Object[] args) {
        this.arguments.clear();
        if (args != null) {
            for (int i = 0, max = args.length; i < max; i++) {
                arguments.put(i + 1 + "", args[i]);
            }
        }
    }

}