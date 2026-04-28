package com.fix.fixnow.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

        registry.addViewController("/").setViewName("redirect:/login");

        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/register").setViewName("register");

        registry.addViewController("/customer/dashboard").setViewName("customerDashboard");
        registry.addViewController("/technician/dashboard").setViewName("technicianDashboard");
        registry.addViewController("/admin/dashboard").setViewName("adminDashboard");

        registry.addViewController("/customer/request/new").setViewName("createRequest");
        registry.addViewController("/customer/review/new").setViewName("addReview");
        registry.addViewController("/customer/request/details").setViewName("requestDetails");
    }
}