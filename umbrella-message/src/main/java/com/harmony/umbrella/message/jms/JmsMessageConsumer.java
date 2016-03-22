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
package com.harmony.umbrella.message.jms;

import com.harmony.umbrella.message.Message;
import com.harmony.umbrella.message.MessageConsumer;
import com.harmony.umbrella.message.MessageException;
import com.harmony.umbrella.message.MessageResolverChain;

/**
 * @author wuxii@foxmail.com
 */
public interface JmsMessageConsumer extends MessageConsumer {

    int DEFAULT_RECEIVE_TIMEOUT = 1000 * 30;

    Message consome(JmsConsomeConfig config) throws MessageException;

    void consume(MessageResolverChain chain, JmsConsomeConfig config) throws MessageException;

}
