/*
 * Copyright 2002-2015 the original author or authors.
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
package com.harmony.umbrella.ws.cxf.interceptor;

import javax.xml.ws.Endpoint;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.harmony.umbrella.ws.jaxws.JaxWsProxyBuilder;
import com.harmony.umbrella.ws.services.HelloService;
import com.harmony.umbrella.ws.services.HelloWebService;

/**
 * @author wuxii@foxmail.com
 */
public class BeanInjectInterceptorTest {

    private static final String ADDRESS = "http://localhost:9999/hi";

    @BeforeClass
    public static void beforeClass() {
        // Endpoint.publish(ADDRESS, new HelloWebService());
    }

    public static void main(String[] args) {
        Endpoint.publish(ADDRESS, new HelloWebService());
    }

    @Test
    @Ignore
    public void test() {
        HelloService helloService = JaxWsProxyBuilder.create()//
                .setAddress(ADDRESS)//
                .addInInterceptor(new MessageInInterceptor())//
                .addOutInterceptor(new MessageOutInterceptor())//
                .build(HelloService.class);
        helloService.sayHi("wuxii");
    }
}