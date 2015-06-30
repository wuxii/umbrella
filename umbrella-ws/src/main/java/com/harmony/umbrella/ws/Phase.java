package com.harmony.umbrella.ws;
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


/**
 * 服务交互的执行周期
 * 
 * @author wuxii@foxmail.com
 */
public enum Phase {

    /**
     * 执行前
     */
    PRE_INVOKE {
        @Override
        public String render() {
            return "post-unmarshal";
        }
    },
    /**
     * 有{@linkplain JaxWsAbortException} 取消执行
     */
    ABORT {
        @Override
        public String render() {
            return null;
        }
    },
    /**
     * 执行成功后
     */
    POST_INVOKE {
        @Override
        public String render() {
            return "pre-marshal";
        }
    },
    /**
     * 执行异常
     */
    THROWING {
        @Override
        public String render() {
            return null;
        }
    },
    /**
     * 执行的finally块中
     */
    FINALLY {
        @Override
        public String render() {
            return null;
        }
    };

    /**
     * for CXF {@linkplain org.apache.cxf.phase.Phase}
     * 
     * @return
     */
    public abstract String render();

    public static Phase value(String phase) {
        for (Phase p : values()) {
            if (p.render() != null && p.render().equals(phase)) {
                return p;
            }
        }
        return null;
    }

}