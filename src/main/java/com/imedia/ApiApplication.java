package com.imedia;

import lombok.extern.log4j.Log4j2;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
@EnableScheduling
@SpringBootApplication
@Log4j2
public class ApiApplication {
    private static ApplicationContext applicationContext;

    public static void main(String[] args) {
        applicationContext = SpringApplication.run(ApiApplication.class, args);
        log.info("======WEBSHOP API STARTED======");
    }

    @Bean
    public MapperFacade mapperFacade() {
        DefaultMapperFactory defaultFactory = new DefaultMapperFactory.Builder().build();
        return defaultFactory.getMapperFacade();
    }

    public static void displayAllBeans() {
        String[] allBeanNames = applicationContext.getBeanDefinitionNames();
        for (String beanName : allBeanNames) {
            System.out.println(beanName);
        }
    }
}