package com.example.insurance.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
    private final Environment env;
    @Autowired
    public DataSourceConfig(Environment env) {
        this.env = env;
    }

    @Bean
    public DataSource DBConnect()
    {
        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("com.mysql.cj.jdbc.Driver");
        dataSourceBuilder.url(env.getProperty("ds.url"));
        dataSourceBuilder.username(env.getProperty("ds.username"));
        dataSourceBuilder.password(env.getProperty("ds.password"));
        return dataSourceBuilder.build();
    }

}
