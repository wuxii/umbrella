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
package com.harmony.umbrella.web.render;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

/**
 * 文本渲染
 * 
 * @author wuxii@foxmail.com
 */
public interface HttpTextRender extends Render {

    /**
     * 渲染json
     * 
     * @param text
     *            待渲染的文本
     * @param response
     *            http response
     * @throws IOException
     *             if an input or output exception occurred
     */
    void renderJson(String text, HttpServletResponse response) throws IOException;

    /**
     * 渲染xml
     * 
     * @param text
     *            待渲染的文本
     * @param response
     *            http response
     * @throws IOException
     *             if an input or output exception occurred
     */
    void renderXml(String text, HttpServletResponse response) throws IOException;

    /**
     * 渲染html
     * 
     * @param text
     *            待渲染的文本
     * @param response
     *            http response
     * @throws IOException
     *             if an input or output exception occurred
     */
    void renderHtml(String text, HttpServletResponse response) throws IOException;

    /**
     * 渲染text
     * 
     * @param text
     *            待渲染的文本
     * @param response
     *            http response
     * @throws IOException
     *             if an input or output exception occurred
     */
    void renderText(String text, HttpServletResponse response) throws IOException;

}
