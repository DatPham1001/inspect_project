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
import org.springframework.context.annotation.Primary;
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
        entityManagerFactoryRef = "primaryEntityManagerFactory",
        transactionManagerRef = "primaryTransactionManager",
        basePackages = {"com.imedia.oracle.repository"})
@DependsOn({"environment"})
public class PrimaryDataSourceConfiguration {

    private final Environment env;

    @Autowired
    public PrimaryDataSourceConfiguration(Environment env) {
        this.env = env;
    }


    @Bean(name = "primaryDataSourceProperties")
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties primaryDataSourceProperties() {
        return new DataSourceProperties();
    }


    @Bean(name = "primaryDataSource")
    @Primary
    @ConfigurationProperties("spring.datasource.hikari")
    public DataSource primaryDataSource(@Qualifier("primaryDataSourceProperties") DataSourceProperties primaryDataSourceProperties) {
        return primaryDataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }


    @Bean(name = "primaryEntityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory(
            EntityManagerFactoryBuilder primaryEntityManagerFactoryBuilder, @Qualifier("primaryDataSource") DataSource primaryDataSource) {

        Map<String, String> primaryJpaProperties = new HashMap<>();
        primaryJpaProperties.put("hibernate.dialect", env.getProperty("spring.jpa.properties.hibernate.dialect"));
        primaryJpaProperties.put("hibernate.hbm2ddl.auto", "none");
        primaryJpaProperties.put("hikari.connectionTimeout", env.getProperty("spring.datasource.hikari.connectionTimeout"));
        primaryJpaProperties.put("hikari.idleTimeout", env.getProperty("spring.datasource.hikari.idleTimeout"));
        primaryJpaProperties.put("hikari.maxLifetim", env.getProperty("spring.datasource.hikari.maxLifetime"));
        primaryJpaProperties.put("hikari.poolName", env.getProperty("spring.datasource.hikari.poolName"));
        primaryJpaProperties.put("hikari.connectionTestQuery", env.getProperty("spring.datasource.hikari.connectionTestQuery"));
        primaryJpaProperties.put("hikari.minimumIdle", env.getProperty("spring.datasource.hikari.minimumIdle"));
        primaryJpaProperties.put("hikari.maximumPoolSize", env.getProperty("spring.datasource.hikari.maximumPoolSize"));
        primaryJpaProperties.put("hikari.validationTimeout", env.getProperty("spring.datasource.hikari.validationTimeout"));
        primaryJpaProperties.put("hikari.leakDetectionThreshold", env.getProperty("spring.datasource.hikari.leakDetectionThreshold"));

        return primaryEntityManagerFactoryBuilder
                .dataSource(primaryDataSource)
                .packages("com.imedia.oracle.entity")
                .persistenceUnit("primaryDataSource")
                .properties(primaryJpaProperties)
                .build();
    }


    @Bean(name = "primaryTransactionManager")
    @Primary
    public PlatformTransactionManager primaryTransactionManager(
            @Qualifier("primaryEntityManagerFactory") EntityManagerFactory primaryEntityManagerFactory) {

        return new JpaTransactionManager(primaryEntityManagerFactory);
    }
}
