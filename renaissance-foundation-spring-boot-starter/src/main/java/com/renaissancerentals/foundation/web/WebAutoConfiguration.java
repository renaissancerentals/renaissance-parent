package com.renaissancerentals.foundation.web;

import com.contentmunch.foundation.logging.RequestResponseLoggingFilter;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration(proxyBeanMethods = false)
public class WebAutoConfiguration implements WebMvcConfigurer {

    /** Swaps the shared logging filter for one that does not buffer file downloads (see its javadoc). */
    @Bean
    static BeanPostProcessor binaryResponseLoggingFilterSwap() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) {
                if (bean instanceof FilterRegistrationBean<?> registration
                        && registration.getFilter() instanceof RequestResponseLoggingFilter
                        && !(registration.getFilter() instanceof BinaryResponseLoggingFilter)) {
                    @SuppressWarnings("unchecked")
                    var typed = (FilterRegistrationBean<RequestResponseLoggingFilter>) registration;
                    typed.setFilter(new BinaryResponseLoggingFilter());
                }
                return bean;
            }
        };
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/index.html")
                .addResourceLocations("classpath:/static/")
                .setCacheControl(CacheControl.noStore());
    }
}
