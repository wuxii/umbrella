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
package com.harmony.umbrella.message;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.message.Message;
import com.harmony.umbrella.message.MessageListener;
import com.harmony.umbrella.message.MessageResolver;

/**
 * 消息监听抽象类
 *
 * @author wuxii
 */
public abstract class AbstractMessageListener implements MessageListener {

    protected final static Logger LOG = LoggerFactory.getLogger(AbstractMessageListener.class);

    protected List<MessageResolver> messageResolvers = new ArrayList<MessageResolver>();

    public AbstractMessageListener() {
    }

    public AbstractMessageListener(List<MessageResolver> messageResolvers) {
        this.messageResolvers = messageResolvers;
    }

    /**
     * 接受到的消息, 使用{@linkplain MessageResolver#support(Message)}判定当前有哪些是符合条件的
     * {@linkplain MessageResolver}. 再经由
     * {@linkplain MessageResolver#handle(Message)}处理该消息.
     * <p/>
     * <p>消息是可以被多个{@linkplain MessageResolver}按顺序处理的
     *
     * @see MessageResolver
     */
    @Override
    public void onMessage(Message message) {
        for (MessageResolver mr : messageResolvers) {
            if (mr.support(message)) {
                LOG.debug("{}处理消息{}", mr, message);
                mr.handle(message);
            }
        }
    }

    /**
     * 动态增加{@linkplain MessageResolver}
     *
     * @param messageResolver
     * @return
     */
    public boolean addMessageResolver(MessageResolver messageResolver) {
        return messageResolvers.add(messageResolver);
    }

    /**
     * 动态删除{@linkplain MessageResolver}
     *
     * @param messageResolver
     * @return
     */
    public boolean removeMessageResolver(MessageResolver messageResolver) {
        return messageResolvers.remove(messageResolver);
    }

    /**
     * 检测是否包含{@linkplain MessageResolver}. 重写{@linkplain Object#hashCode()}
     * {@linkplain Object#equals(Object)}方法
     *
     * @param messageResolver
     * @return
     */
    public boolean containsMessageResolver(MessageResolver messageResolver) {
        return messageResolvers.contains(messageResolver);
    }

    public List<MessageResolver> getMessageResolvers() {
        return messageResolvers;
    }

    public void setMessageResolvers(List<MessageResolver> messageResolvers) {
        this.messageResolvers = messageResolvers;
    }

}