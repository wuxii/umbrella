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
package com.harmony.umbrella.ws.visitor;

import com.harmony.umbrella.ws.Context;
import com.harmony.umbrella.ws.ContextVisitor;
import com.harmony.umbrella.ws.WebServiceAbortException;

/**
 * 周期访问空实现
 * 
 * @author wuxii@foxmail.com
 */
public abstract class AbstractContextVisitor implements ContextVisitor {

    @Override
    public boolean visitBefore(Context context) throws WebServiceAbortException {
        return true;
    }

    @Override
    public void visitAbort(WebServiceAbortException ex, Context context) {
    }

    @Override
    public void visitCompletion(Object result, Context context) {
    }

    @Override
    public void visitThrowing(Throwable throwable, Context context) {
    }

    @Override
    public void visitFinally(Object result, Throwable throwable, Context context) {
    }

}
