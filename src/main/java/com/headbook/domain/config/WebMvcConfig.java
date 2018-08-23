package com.headbook.domain.config;

import com.headbook.domain.interceptor.UserAddInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final UserAddInterceptor userAddInterceptor;

    @Autowired
    public WebMvcConfig(UserAddInterceptor userAddInterceptor) {
        this.userAddInterceptor = userAddInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userAddInterceptor);
    }
}
