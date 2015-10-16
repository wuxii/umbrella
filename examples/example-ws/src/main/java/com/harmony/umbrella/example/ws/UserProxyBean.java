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
package com.harmony.umbrella.example.ws;

import java.util.ArrayList;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.xml.stream.XMLInputFactory;

import com.harmony.umbrella.ws.cxf.interceptor.MessageInInterceptor;
import com.harmony.umbrella.ws.cxf.interceptor.MessageOutInterceptor;
import com.harmony.umbrella.ws.jaxws.JaxWsProxyBuilder;

/**
 * @author wuxii@foxmail.com
 */
@WebService(serviceName = "UserProxy")
@Stateless(mappedName = "UserProxy")
public class UserProxyBean {

    public void test() {
        UserService service = JaxWsProxyBuilder//
                .create()//
                .setAddress("http://localhost:9001/user")//
                .addInInterceptor(new MessageInInterceptor())//
                .addOutInterceptor(new MessageOutInterceptor())//
                .build(UserService.class);

        System.setProperty("javax.xml.stream.XMLInputFactory", "com.harmony.umbrella.xml.UmbrellaXMLInputFactory");

        XMLInputFactory factory = XMLInputFactory.newFactory();

        System.out.println(factory);

        /*factory = XMLInputFactory.newFactory("com.harmony.umbrella.xml.UmbrellaXMLInputFactory", Thread.currentThread().getContextClassLoader());

        System.out.println(factory);*/

        service.saveOrUpdateUser(new ArrayList<User>());
    }

    public static void main(String[] args) {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        System.out.println(factory);
    }

}
