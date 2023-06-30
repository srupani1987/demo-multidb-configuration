package com.example.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "mysqlEntityManagerFactory",
        transactionManagerRef = "mysqlTransactionManager",
        basePackages = {"com.example.repository.mysql"})
public class MySqlDataSourceConfig {

    @Bean(name = "mysqlProperties")
    @ConfigurationProperties("app.datasource.mysql")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "mysqlDatasource")
    @ConfigurationProperties(prefix = "app.datasource.mysql")
    public DataSource datasource(@Qualifier("mysqlProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }

    @Bean(name = "mysqlEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean
            (@Qualifier("dataJpaVendor") JpaVendorAdapter adapter,
             @Qualifier("mysqlDatasource") DataSource dataSource) {
        Properties properties = new Properties();
        properties.put("spring.jpa.database","mysql");
        properties.put("spring.jpa.hibernate.ddl-auto","update");
        properties.put("spring.jpa.show-sql",true);
        properties.put("spring.jpa.properties.hibernate.format_sql",true);
        properties.put("spring.jpa.properties.hibernate.dialect","org.hibernate.dialect.MySQLDialect");
        LocalContainerEntityManagerFactoryBean lem=new LocalContainerEntityManagerFactoryBean();
        lem.setDataSource(dataSource);
        lem.setJpaProperties(properties);
        lem.setPackagesToScan("com.example");
        lem.setPersistenceUnitName("mysql");
        lem.setJpaVendorAdapter(adapter);
        return lem;
    }

    @Bean(name = "mysqlTransactionManager")
    @ConfigurationProperties("spring.jpa")
    public PlatformTransactionManager transactionManager(
            @Qualifier("mysqlEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
