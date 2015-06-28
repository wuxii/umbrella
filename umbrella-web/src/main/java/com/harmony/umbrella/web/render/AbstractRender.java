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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractRender implements Render {

    protected static final Logger LOG = LoggerFactory.getLogger(Render.class);

    @Override
    public void render(byte[] buf, OutputStream out) throws IOException {
        try {
            out.write(buf);
            out.flush();
        } catch (IOException e) {
            LOG.error("", e);
            throw e;
        }
    }

    @Override
    public boolean render(String text, PrintWriter writer) {
        writer.write(text);
        writer.flush();
        return true;
    }

}
