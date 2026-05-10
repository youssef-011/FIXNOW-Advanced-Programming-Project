package com.fix.fixnow.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Legacy path: no controller serves this view with a model; send users to dashboard.
        registry.addRedirectViewController("/customer/request/details", "/customer/dashboard");
    }
}
