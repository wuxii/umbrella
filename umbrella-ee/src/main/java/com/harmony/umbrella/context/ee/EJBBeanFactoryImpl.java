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
package com.harmony.umbrella.context.ee;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Properties;

import javax.ejb.EJB;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.harmony.umbrella.context.ee.resolver.InternalContextResolver;
import com.harmony.umbrella.core.BeanFactory;
import com.harmony.umbrella.core.BeansException;
import com.harmony.umbrella.core.NoSuchBeanFoundException;
import com.harmony.umbrella.util.ClassUtils.ClassFilterFeature;
import com.harmony.umbrella.util.ReflectionUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class EJBBeanFactoryImpl implements EJBBeanFactory {

    private final Properties contextProperties = new Properties();
    private ContextResolver contextResolver;

    public static EJBBeanFactory create(Properties properties) {
        return new EJBBeanFactoryImpl(InternalContextResolver.create(properties), properties);
    }

    public EJBBeanFactoryImpl(ContextResolver contextResolver, Properties contextProperties) {
        this.contextResolver = contextResolver;
        this.contextProperties.putAll(contextProperties);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T lookup(String jndi) throws BeansException {
        Object bean = contextResolver.tryLookup(jndi, getContext());
        if (bean != null) {
            return (T) bean;
        }
        throw new NoSuchBeanFoundException(jndi);
    }

    @Override
    public <T> T lookup(Class<T> clazz) throws BeansException {
        return lookup(new BeanDefinition(clazz));
    }

    @Override
    public <T> T lookup(Class<T> clazz, EJB ejbAnnotation) throws BeansException {
        return lookup(new BeanDefinition(clazz), ejbAnnotation);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T lookup(BeanDefinition beanDefinition) throws BeansException {
        Object bean = contextResolver.guessBean(beanDefinition, getContext());
        if (bean == null) {
            bean = deepLookup(beanDefinition, null);
        }
        if (bean == null) {
            throw new NoSuchBeanFoundException(beanDefinition.getBeanClass().getName());
        }
        return (T) bean;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T lookup(BeanDefinition beanDefinition, EJB ejbAnnotation) throws BeansException {
        Object bean = contextResolver.guessBean(beanDefinition, ejbAnnotation, getContext());
        if (bean == null) {
            bean = deepLookup(beanDefinition, ejbAnnotation);
        }
        if (bean == null) {
            throw new NoSuchBeanFoundException(beanDefinition.getBeanClass().getName());
        }
        return (T) bean;
    }

    @Override
    public void setContextProperties(Properties properties) {
        contextProperties.clear();
        contextProperties.putAll(properties);
    }

    private Object deepLookup(BeanDefinition beanDefinition, EJB ejbAnnotation) {
        if (ejbAnnotation != null && StringUtils.isNotBlank(ejbAnnotation.lookup())) {
            Object bean = contextResolver.tryLookup(ejbAnnotation.lookup(), getContext());
            if (contextResolver.isDeclareBean(beanDefinition, bean)) {
                return bean;
            }
            throw new NoSuchBeanFoundException(ejbAnnotation.lookup());
        }

        SessionBean sessionBean = contextResolver.search(beanDefinition, getContext());
        if (sessionBean != null) {
            return sessionBean.getBean();
        }

        throw new NoSuchBeanFoundException(beanDefinition.getBeanClass().getName());
    }

    // base bean factory method

    @Override
    public <T> T getBean(String beanName) throws BeansException {
        return getBean(beanName, BeanFactory.PROTOTYPE);
    }

    @Override
    public <T> T getBean(Class<T> beanClass) throws BeansException {
        return getBean(beanClass, BeanFactory.PROTOTYPE);
    }

    @Override
    public <T> T getBean(String beanName, String scope) throws BeansException {
        return lookup(beanName);
    }

    @Override
    public <T> T getBean(Class<T> beanClass, String scope) throws BeansException {
        T bean = null;
        try {
            bean = lookup(beanClass);
        } catch (BeansException e) {
            bean = tryNewOneBean(beanClass);
            if (bean == null) {
                throw e;
            }
        }
        return bean;
    }

    public <T> T tryNewOneBean(Class<T> beanClass) {
        if (ClassFilterFeature.NEWABLE.accept(beanClass)) {
            T bean = ReflectionUtils.instantiateClass(beanClass);
            Field[] fields = beanClass.getDeclaredFields();
            for (Field field : fields) {
                int modifiers = field.getModifiers();
                if (!Modifier.isStatic(modifiers)) {
                    EJB ejbAnnotation = field.getAnnotation(EJB.class);
                    Object v = ejbAnnotation == null ? lookup(field.getType()) : lookup(field.getType(), ejbAnnotation);
                    ReflectionUtils.setFieldValue(field, bean, v);
                }
            }
            return bean;
        }
        return null;
    }

    private Context getContext() {
        try {
            return new InitialContext(contextProperties);
        } catch (NamingException e) {
            throw new IllegalStateException(e);
        }
    }

}
