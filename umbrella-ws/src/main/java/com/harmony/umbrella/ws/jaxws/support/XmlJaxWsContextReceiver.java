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
package com.harmony.umbrella.ws.jaxws.support;

import java.util.ArrayList;
import java.util.List;

import com.harmony.umbrella.ws.Context;
import com.harmony.umbrella.ws.Metadata;
import com.harmony.umbrella.ws.MetadataLoader;
import com.harmony.umbrella.ws.ContextVisitor;
import com.harmony.umbrella.ws.jaxws.JaxWsExecutor;
import com.harmony.umbrella.ws.support.SimpleContext;

/**
 * for spring config
 * 
 * @author wuxii@foxmail.com
 */
public class XmlJaxWsContextReceiver implements JaxWsContextReceiver {

    private JaxWsExecutor executor;

    private MetadataLoader metaLoader;

    private boolean reload = true;

    private List<ContextVisitor> visitors = new ArrayList<ContextVisitor>();

    public XmlJaxWsContextReceiver() {
    }

    public XmlJaxWsContextReceiver(JaxWsExecutor executor) {
        this.executor = executor;
    }

    @Override
    public void receive(Context context) {
        executor.execute(reload(context), visitors.toArray(new ContextVisitor[visitors.size()]));
    }

    @Override
    public void open() throws Exception {
    }

    @Override
    public void close() throws Exception {
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    protected Context reload(Context context) {
        if (!reload && context.getAddress() != null)
            return context;
        if (metaLoader != null) {
            Metadata metadata = metaLoader.loadMetadata(context.getServiceInterface());
            if (metadata != null) {
                SimpleContext copyContext = new SimpleContext(context.getServiceInterface(), context.getMethodName());
                copyContext.setParameters(context.getParameters());
                copyContext.putAll(context.getContextMap());
                copyContext.setAddress(metadata.getAddress());
                copyContext.setUsername(metadata.getUsername());
                copyContext.setPassword(metadata.getPassword());
                copyContext.setConnectionTimeout(metadata.getConnectionTimeout());
                copyContext.setReceiveTimeout(metadata.getReceiveTimeout());
                return copyContext;
            }
        }
        return context;
    }

    public JaxWsExecutor getJaxWsExecutor() {
        return executor;
    }

    public void getJaxWsExecutor(JaxWsExecutor executor) {
        this.executor = executor;
    }

    public MetadataLoader getMetadataLoader() {
        return metaLoader;
    }

    public void setMetadataLoader(MetadataLoader metaLoader) {
        this.metaLoader = metaLoader;
    }

    public boolean isReload() {
        return reload;
    }

    public void setReload(boolean reload) {
        this.reload = reload;
    }

    public List<ContextVisitor> getPhaseVisitors() {
        return visitors;
    }

    public void setPhaseVisitors(List<ContextVisitor> visitors) {
        this.visitors = visitors;
    }

}
