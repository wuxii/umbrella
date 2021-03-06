package com.harmony.umbrella.lock.annotation;

import org.springframework.context.annotation.AdviceMode;
import org.springframework.core.Ordered;

import java.lang.annotation.*;

/**
 * @author wuxii
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
// @Import(LockConfigurationSelector.class)
public @interface EnableDistributedLock {

    boolean proxyTargetClass() default false;

    AdviceMode mode() default AdviceMode.PROXY;

    int order() default Ordered.LOWEST_PRECEDENCE;

}
