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
package com.harmony.umbrella.web.render;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import com.harmony.umbrella.web.Render;

/**
 * 前台二进制输出
 * 
 * @author wuxii@foxmail.com
 */
public interface HttpBinaryRender extends Render {

    /**
     * 输出文件
     * 
     * @param file
     *            待输出的文件
     * @param response
     *            http response
     * @throws IOException
     *             if an input or output exception occurred
     */
    void renderFile(File file, HttpServletResponse response) throws IOException;

}
