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
package com.harmony.modules.message;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wuxii
 */
public class AbstractMessageListener implements MessageListener {

    protected Logger log = LoggerFactory.getLogger(AbstractMessageListener.class);
    private List<MessageResolver> messageResolvers = new ArrayList<MessageResolver>();

    // private boolean initialized = false;

    public AbstractMessageListener() {
    }

    public AbstractMessageListener(List<MessageResolver> messageResolvers) {
        this.messageResolvers = messageResolvers;
    }

    @Override
    public void init() {
    }

    @Override
    public void onMessage(Message message) {
        for (MessageResolver mr : messageResolvers) {
            if (mr.support(message)) {
                log.debug("{}处理消息{}", mr, message);
                mr.handle(message);
            }
        }
    }

    @Override
    public void destory() {
    }

    public boolean addMessageResolver(MessageResolver messageResolver) {
        return messageResolvers.add(messageResolver);
    }

    public boolean removeMessageResolver(MessageResolver messageResolver) {
        return messageResolvers.remove(messageResolver);
    }

    public boolean containsMessageResolver(MessageResolver messageResolver) {
        return messageResolvers.contains(messageResolver);
    }
}
