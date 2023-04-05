package com.imedia.config.application;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "secondaryEntityManagerFactory",
        transactionManagerRef = "secondaryTransactionManager",
        basePackages = {"com.imedia.oracle.reportrepository"})
@DependsOn({"environment"})
public class SecondaryDataSourceConfiguration {

    private final Environment env;

    @Autowired
    public SecondaryDataSourceConfiguration(Environment env) {
        this.env = env;
    }

    @Bean(name = "secondaryDataSourceProperties")
    @ConfigurationProperties("spring.datasource-secondary")
    public DataSourceProperties secondaryDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "secondaryDataSource")
    @ConfigurationProperties("spring.datasource-secondary.hikari")
    public DataSource secondaryDataSource(@Qualifier("secondaryDataSourceProperties") DataSourceProperties secondaryDataSourceProperties) {
        return secondaryDataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean(name = "secondaryEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean secondaryEntityManagerFactory(
            EntityManagerFactoryBuilder secondaryEntityManagerFactoryBuilder, @Qualifier("secondaryDataSource") DataSource secondaryDataSource) {

        Map<String, String> secondaryJpaProperties = new HashMap<>();
        secondaryJpaProperties.put("hibernate.dialect", env.getProperty("spring.jpa.properties.hibernate.dialect"));
        secondaryJpaProperties.put("hibernate.hbm2ddl.auto", "none");
        secondaryJpaProperties.put("hikari.connectionTimeout", env.getProperty("spring.datasource.hikari.connectionTimeout"));
        secondaryJpaProperties.put("hikari.idleTimeout", env.getProperty("spring.datasource.hikari.idleTimeout"));
        secondaryJpaProperties.put("hikari.maxLifetim", env.getProperty("spring.datasource.hikari.maxLifetime"));
        secondaryJpaProperties.put("hikari.poolName", env.getProperty("spring.datasource.hikari.poolName"));
        secondaryJpaProperties.put("hikari.connectionTestQuery", env.getProperty("spring.datasource.hikari.connectionTestQuery"));
        secondaryJpaProperties.put("hikari.minimumIdle", env.getProperty("spring.datasource.hikari.minimumIdle"));
        secondaryJpaProperties.put("hikari.maximumPoolSize", env.getProperty("spring.datasource.hikari.maximumPoolSize"));
        secondaryJpaProperties.put("hikari.validationTimeout", env.getProperty("spring.datasource.hikari.validationTimeout"));
        secondaryJpaProperties.put("hikari.leakDetectionThreshold", env.getProperty("spring.datasource.hikari.leakDetectionThreshold"));

        return secondaryEntityManagerFactoryBuilder
                .dataSource(secondaryDataSource)
                .packages("com.imedia.oracle.reportentity")
                .persistenceUnit("secondaryDataSource")
                .properties(secondaryJpaProperties)
                .build();
    }

    @Bean(name = "secondaryTransactionManager")
    public PlatformTransactionManager secondaryTransactionManager(
            @Qualifier("secondaryEntityManagerFactory") EntityManagerFactory secondaryEntityManagerFactory) {

        return new JpaTransactionManager(secondaryEntityManagerFactory);
    }


}
