package com.imedia.config.application;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class BeanConfig {
    @Bean
    public Environment environment(ApplicationContext context) {
        String[] allBeanNames = context.getBeanDefinitionNames();
        return context.getEnvironment();
    }
}
