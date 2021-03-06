package com.harmony.umbrella.log;

import com.harmony.umbrella.log.annotation.Binding;
import com.harmony.umbrella.log.annotation.Logging;
import com.harmony.umbrella.log.annotation.Scope;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;

/**
 * @author wuxii
 */
@Slf4j
public class LogMessageTest {

    public static void main(String[] args) {
        new LogMessage()
                .start()
                .level(Level.INFO)
                .key("1")
                .module("测试模块")
                .action("测试日志打印")
                .userId(1L)
                .username("wuxii")
                .message("用户[{}]对日志进行打印操作, 打印的日志信息为[{}]", "wuxii", "Hello World!")//
                .currentThreadFrame()
                .currentThread()
                .finish()
                .log(log);
        log.info("Hello {}!", "world");
    }

    @Test
    public void test() throws Throwable {
        Method[] methods = AImpl.class.getDeclaredMethods();
        for (Method method : methods) {
            // not null
            System.out.println(AnnotationUtils.findAnnotation(method, Logging.class));
            // null
            System.out.println(AnnotationUtils.getAnnotation(method, Logging.class));
        }
    }

    public interface A {

        @Logging(
                message = "这是一个测试的日志消息${#p0}. 绑定值测试${binding1}",
                bindings = {
                        @Binding(key = "binding1", expression = "#p1", scope = Scope.OUT)
                }
        )
        String greeting(String name);
    }

    public static class AImpl implements A {

        @Override
        public String greeting(String name) {
            return "Hi " + name;
        }
    }


}
