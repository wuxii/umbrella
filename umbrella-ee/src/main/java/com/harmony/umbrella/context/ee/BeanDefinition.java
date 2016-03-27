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

import static com.harmony.umbrella.context.ee.util.TextMatchCalculator.*;

import java.lang.annotation.Annotation;
import java.util.*;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Singleton;
import javax.ejb.Stateful;
import javax.ejb.Stateless;

import com.harmony.umbrella.util.AnnotationUtils;
import com.harmony.umbrella.util.Assert;
import com.harmony.umbrella.util.ClassUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * JavaEE {@linkplain Stateless}, {@linkplain Stateful}, {@linkplain Singleton} 为sessionBean
 * <p/>
 * 将标记了这些注解的bean的基础信息定义为{@linkplain BeanDefinition}
 *
 * @author wuxii@foxmail.com
 */
public class BeanDefinition {

    @SuppressWarnings("unchecked")
    public static final List<Class<? extends Annotation>> SESSION_ANNOTATION = Collections.unmodifiableList(Arrays.asList(Stateless.class, Stateful.class, Singleton.class));

    /**
     * beanClass 会话bean的类, 如果class没有标注session bean/local的注解并且是接口, 默认认为是remote接口
     */
    public final Class<?> beanClass;

    public final Annotation sessionAnnotation;

    public BeanDefinition(Class<?> beanClass) {
        this(beanClass, getSessionBeanAnnotation(beanClass));
    }

    public BeanDefinition(Class<?> beanClass, Annotation ann) {
        this.beanClass = beanClass;
        this.sessionAnnotation = ann;
    }

    /**
     * bean 类型
     *
     * @return 被描述的类
     */
    public Class<?> getBeanClass() {
        return beanClass;
    }

    public Class<?> getRemoteClass() {
        return isRemoteClass() ? beanClass : findMatchingClass(getAllRemoteClasses());
    }

    public Class<?> getLocalClass() {
        return isLocalClass() ? beanClass : findMatchingClass(getAllLocalClasses());
    }

    /**
     * 查找与beanClass最匹配的class, 如果输入的classes为空集合则返回null
     */
    @SuppressWarnings("rawtypes")
    private Class findMatchingClass(Collection<Class> classes) {
        if (classes.isEmpty()) {
            return null;
        }
        String beanClassName = beanClass.getSimpleName();
        Iterator<Class> iterator = classes.iterator();
        double ratio = 0, currentRatio = 0;
        Class<?> result = null, temp = null;
        while (iterator.hasNext()) {
            temp = iterator.next();
            currentRatio = matchingRate(beanClassName, temp.getSimpleName());
            if (ratio <= currentRatio) {
                result = temp;
                ratio = currentRatio;
            }
        }
        return result;
    }

    /**
     * beanClass 标记了{@linkplain Stateless}
     */
    public boolean isStateless() {
        return isThatOf(Stateless.class);
    }

    /**
     * beanClass 标记了{@linkplain Stateful}
     */
    public boolean isStateful() {
        return isThatOf(Stateful.class);
    }

    /**
     * beanClass 标记了{@linkplain Singleton}
     */
    public boolean isSingleton() {
        return isThatOf(Singleton.class);
    }

    /**
     * 测试beanClass上注解的是否是annClass
     *
     * @param annClass
     * @return
     */
    public boolean isThatOf(Class<? extends Annotation> annClass) {
        return sessionAnnotation != null && sessionAnnotation.getClass() == annClass;
    }

    // 注解信息

    /**
     * 获取beanClass上的SessionBean的注解, 判断注解上的mappedName, 为空则默认为类名的SampleName
     * <p/>
     * 如果为标注SessionBean注解返回null
     */
    public String getMappedName() {
        String mappedName = getAnnotationValue("mappedName");
        if (StringUtils.isBlank(mappedName) && isSessionClass()) {
            return beanClass.getSimpleName();
        }
        return mappedName;
    }

    /**
     * @see Stateless#name()
     */
    public String getName() {
        return getAnnotationValue("name");
    }

    /**
     * @see Stateless#description()
     */
    public String getDescription() {
        return getAnnotationValue("description");
    }

    /**
     * 注有{@linkplain Stateless}, {@linkplain Stateful}, {@linkplain Singleton} 三类注解中的一个则为sessionBean
     */
    public boolean isSessionClass() {
        return sessionAnnotation != null;
    }

    /**
     * beanClass是interface并且接口标记了{@linkplain Remote}注解
     * <p/>
     * <b>默认将不是local的interface定义为remote接口</b>
     */
    public boolean isRemoteClass() {
        return isRemoteClass(beanClass) || (beanClass.isInterface() && !isLocalClass(beanClass));
    }

    /**
     * beanClass是interface并且beanClass上标记了{@linkplain Local}注解
     */
    public boolean isLocalClass() {
        return isLocalClass(beanClass);
    }

    /**
     * 获取beanClass所有符合{@linkplain #isRemoteClass()}条件的数据
     */
    @SuppressWarnings("rawtypes")
    public Collection<Class> getAllRemoteClasses() {
        Set<Class> remoteClasses = new LinkedHashSet<Class>();
        // 如果本身是remote接口
        if (isRemoteClass()) {
            remoteClasses.add(beanClass);
        }
        if (isSessionClass()) {
            // 注解上配置的remote接口
            Remote ann = beanClass.getAnnotation(Remote.class);
            if (ann != null) {
                for (Class clazz : ann.value()) {
                    remoteClasses.add(clazz);
                }
            }
        }
        // class 的所有接口
        for (Class clazz : ClassUtils.getAllInterfaces(beanClass)) {
            // 当前类的所有接口, 如果接口上标注了remote注解则表示是一个remote class
            if (isRemoteClass(clazz)) {
                remoteClasses.add(clazz);
            }
        }
        return remoteClasses;
    }

    /**
     * 获取beanClass中所有的local class
     */
    @SuppressWarnings("rawtypes")
    public Collection<Class> getAllLocalClasses() {
        Set<Class> localClasses = new LinkedHashSet<Class>();
        if (isLocalClass()) {
            localClasses.add(beanClass);
        }
        if (isSessionClass()) {
            Local ann = beanClass.getAnnotation(Local.class);
            if (ann != null) {
                for (Class clazz : ann.value()) {
                    localClasses.add(clazz);
                }
            }
        }
        // 所有接口
        for (Class clazz : ClassUtils.getAllInterfaces(beanClass)) {
            if (isLocalClass(clazz)) {
                localClasses.add(clazz);
            }
        }
        return localClasses;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        BeanDefinition that = (BeanDefinition) o;

        if (sessionAnnotation != null ? !sessionAnnotation.equals(that.sessionAnnotation) : that.sessionAnnotation != null)
            return false;
        if (beanClass != null ? !beanClass.equals(that.beanClass) : that.beanClass != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = beanClass != null ? beanClass.hashCode() : 0;
        result = 31 * result + (sessionAnnotation != null ? sessionAnnotation.hashCode() : 0);
        return result;
    }

    private String getAnnotationValue(String name) {
        return sessionAnnotation == null ? null : getAnnotationValue(sessionAnnotation, name);
    }

    // utils method
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static boolean isRemoteClass(Class clazz) {
        return clazz.isInterface() && clazz.getAnnotation(Remote.class) != null;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static boolean isLocalClass(Class clazz) {
        return clazz.isInterface() && clazz.getAnnotation(Local.class) != null;
    }

    private static final String getAnnotationValue(Annotation ann, String methodName) {
        Assert.notNull(ann, "annotation is null");
        Assert.notNull(methodName, "annotation value name is null");
        return (String) AnnotationUtils.getAnnotationValue(ann, methodName);
    }

    private static Annotation getSessionBeanAnnotation(Class<?> clazz) {
        for (Class<? extends Annotation> sc : SESSION_ANNOTATION) {
            Annotation ann = clazz.getAnnotation(sc);
            if (ann != null) {
                return ann;
            }
        }
        return null;
    }
}
