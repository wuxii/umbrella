package com.harmony.umbrella.autoconfigure.web;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.harmony.umbrella.web.method.support.BundleModelMethodArgumentResolver;
import com.harmony.umbrella.web.method.support.BundleParamMethodArgumentResolver;
import com.harmony.umbrella.web.method.support.BundleQueryMethodArgumentResolver;
import com.harmony.umbrella.web.method.support.BundleViewMethodProcessor;
import com.harmony.umbrella.web.servlet.handler.ModelFragmentInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author wuxii@foxmail.com
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = "harmony.web", value = "enabled", matchIfMissing = true)
@EnableConfigurationProperties(WebProperties.class)
@Import(WebAutoConfiguration.WebBundleConfiguration.class)
public class WebAutoConfiguration {

    // @Bean
    // @ConditionalOnProperty(name = "harmony.web.current-context.enabled", havingValue = "true", matchIfMissing = true)
    // FilterRegistrationBean currentContextFilter() {
    // // filter
    // CurrentContextFilter filter = new CurrentContextFilter();
    // CurrentContextProperties currentContextProps = webContextProperties.getCurrentContext();
    // filter.setIpHeader(webContextProperties.getIpHeader());
    // Collection<String> urlPatterns = null;
    //
    // if (currentContextProps != null) {
    // filter.setExcludedPatterns(currentContextProps.getGlobalIncludeFields());
    // urlPatterns = currentContextProps.getUrlPatterns();
    // }
    //
    // FilterRegistrationBean registrationBean = new FilterRegistrationBean();
    // registrationBean.setFilter(filter);
    // registrationBean.setUrlPatterns(urlPatterns != null ? urlPatterns : Arrays.asList("/"));
    // return registrationBean;
    // }

    @ConditionalOnClass({
            BundleParamMethodArgumentResolver.class, BundleModelMethodArgumentResolver.class,
            BundleQueryMethodArgumentResolver.class, BundleViewMethodProcessor.class,
            ModelFragmentInterceptor.class
    })
    public static class WebBundleConfiguration {

        private final WebProperties webProperties;

        public WebBundleConfiguration(WebProperties webProperties) {
            this.webProperties = webProperties;
        }

        @Bean
        BundleViewMethodProcessor bundleViewMethodProcessor() {
            WebProperties.View view = webProperties.getView();
            BundleViewMethodProcessor bundleViewMethodProcessor = new BundleViewMethodProcessor();
            bundleViewMethodProcessor.setKeyStyle(view.getKeyStyle());
            bundleViewMethodProcessor.setDateFormat(view.getDateFormat());
            bundleViewMethodProcessor.setDefaultSerializerFeatures(view.getDefaultSerializerFeatures());
            bundleViewMethodProcessor.getGlobalExcludeFields().addAll(view.getGlobalExcludeFields());
            bundleViewMethodProcessor.getGlobalIncludeFields().addAll(view.getGlobalIncludeFields());
            return bundleViewMethodProcessor;
        }

        @Bean
        WebMvcConfigurer webMvcConfigurer(BundleViewMethodProcessor bundleViewMethodProcessor) {
            return new WebMvcConfigurer() {

                @Override
                public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
                    converters.add(new FastJsonHttpMessageConverter());
                }

                @Override
                public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
                    argumentResolvers.add(new BundleParamMethodArgumentResolver());
                    argumentResolvers.add(new BundleModelMethodArgumentResolver());
                    argumentResolvers.add(new BundleQueryMethodArgumentResolver());
                    argumentResolvers.add(bundleViewMethodProcessor);
                }

                @Override
                public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
                    returnValueHandlers.add(bundleViewMethodProcessor);
                }

                @Override
                public void addInterceptors(InterceptorRegistry registry) {
                    registry.addInterceptor(new ModelFragmentInterceptor());
                }

            };
        }

    }

}
