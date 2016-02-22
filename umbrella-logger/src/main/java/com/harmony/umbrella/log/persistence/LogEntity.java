/*
 * Copyright 2012-2016 the original author or authors.
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
package com.harmony.umbrella.log.persistence;

import java.io.Serializable;

/**
 * @author wuxii@foxmail.com
 */
public class LogEntity {

    private Long logId;

    private Serializable bizId;
    private String bizModule;

    private String module;
    private String action;
    private String level;
    private String message;

    private String operator;
    private Serializable operatorId;

    private String stack;
    private String thread;

    private String formatType;

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public Serializable getBizId() {
        return bizId;
    }

    public void setBizId(Serializable bizId) {
        this.bizId = bizId;
    }

    public String getBizModule() {
        return bizModule;
    }

    public void setBizModule(String bizModule) {
        this.bizModule = bizModule;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Serializable getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Serializable operatorId) {
        this.operatorId = operatorId;
    }

    public String getStack() {
        return stack;
    }

    public void setStack(String stack) {
        this.stack = stack;
    }

    public String getThread() {
        return thread;
    }

    public void setThread(String thread) {
        this.thread = thread;
    }

    public String getFormatType() {
        return formatType;
    }

    public void setFormatType(String formatType) {
        this.formatType = formatType;
    }

}
