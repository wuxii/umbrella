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
package com.harmony.umbrella.scheduling.support;

import java.io.IOException;
import java.util.Calendar;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;

import com.harmony.umbrella.core.BeanLoader;
import com.harmony.umbrella.core.ClassBeanLoader;
import com.harmony.umbrella.scheduling.Job;
import com.harmony.umbrella.scheduling.Scheduler;
import com.harmony.umbrella.scheduling.SchedulerException;
import com.harmony.umbrella.scheduling.Trigger;
import com.harmony.umbrella.util.PropUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * 基于配置文件的定时任务管理
 * @author wuxii@foxmail.com
 */
@Stateless
@Remote(Scheduler.class)
public class PropertiesFileEJBScheduler extends AbstractEJBScheduler {

    public static final String jobPropertiesFileLocation = "META-INF/scheduler/jobs.properties";

    public static final String triggerPropertiesFileLocation = "META-INF/scheduler/triggers.properties";

    private String jobPropertiesFile = jobPropertiesFileLocation;

    private String triggerPropertiesFile = triggerPropertiesFileLocation;

    @Resource
    private TimerService timerService;
    
    private BeanLoader beanLoader = new ClassBeanLoader();

    @PostConstruct
    private void postConstruct() {
        try {
            init();
        } catch (SchedulerException e) {
            throw new IllegalArgumentException(e.getMessage(), e.getCause());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void init() throws SchedulerException {
        try {
            Properties jobProps = PropUtils.loadProperties(jobPropertiesFile);
            for (String name : jobProps.stringPropertyNames()) {
                String jobClassName = jobProps.getProperty(name);
                if (StringUtils.isEmpty(jobClassName)) {
                    throw new IllegalArgumentException("job[" + name + "] class cannot be null");
                }
                try {
                    Class<?> jobClass = Class.forName(jobClassName);
                    if (!jobClass.isInterface() && Job.class.isAssignableFrom(jobClass)) {
                        EJBJobInfo jobInfo = new EJBJobInfo();
                        jobInfoMap.put(name, jobInfo);
                        jobInfo.jobName = name;
                        jobInfo.jobClass = (Class<? extends Job>) jobClass;
                        jobInfo.status = Status.READY;
                        jobInfo.regiestTime = Calendar.getInstance();
                        continue;
                    }
                    throw new IllegalArgumentException("job class [" + jobClass + "] not subclass of " + Job.class);
                } catch (ClassNotFoundException e) {
                    throw new IllegalArgumentException("job[" + name + "] class not find", e);
                }
            }
        } catch (IOException e) {
            throw new SchedulerException(e.getMessage(), e.getCause());
        }
    }

    @Override
    protected TimerService getTimerService() {
        return timerService;
    }

    @Override
    protected BeanLoader getBeanLoader() {
        return beanLoader;
    }

    @Override
    @Timeout
    protected void monitorTask(Timer timer) {
        handle(timer);
    }

    @Override
    protected Trigger getJobTrigger(String jobName) {
        try {
            Properties props = PropUtils.loadProperties(triggerPropertiesFile);
            String triggerExpression = props.getProperty(jobName);
            return triggerExpression != null ? new ExpressionTrigger(triggerExpression) : null;
        } catch (IOException e) {
        }
        return null;
    }

}