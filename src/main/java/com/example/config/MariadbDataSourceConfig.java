package com.example.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "mariadbEntityManagerFactory",
        transactionManagerRef = "mariadbTransactionManager",
        basePackages = {"com.example.repository.mariadb"})
public class MariadbDataSourceConfig {

    @Bean(name = "mariadbProperties")
    @ConfigurationProperties("app.datasource.mariadb")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "mariadbDatasource")
    @Primary
    @ConfigurationProperties(prefix = "app.datasource.mariadb")
    public DataSource datasource(@Qualifier("mariadbProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }

    @Bean(name = "mariadbEntityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean
            (@Qualifier("dataJpaVendor") JpaVendorAdapter adapter,
             @Qualifier("mariadbDatasource") DataSource dataSource) {
        Properties properties = new Properties();
        properties.put("spring.jpa.hibernate.ddl-auto", "create");
        properties.put("spring.jpa.show-sql",true);
        properties.put("spring.jpa.properties.hibernate.format_sql",true);
        properties.put("spring.jpa.properties.hibernate.dialect", "org.hibernate.dialect.MariaDB103Dialect");
        LocalContainerEntityManagerFactoryBean lem = new LocalContainerEntityManagerFactoryBean();
        lem.setDataSource(dataSource);
        lem.setJpaProperties(properties);
        lem.setPackagesToScan("com.example");
        lem.setPersistenceUnitName("mysql");
        lem.setJpaVendorAdapter(adapter);
        return lem;
    }

    @Bean("dataJpaVendor")
    @Primary
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new
                HibernateJpaVendorAdapter();
        //hibernateJpaVendorAdapter.setShowSql(true);
        //hibernateJpaVendorAdapter.setGenerateDdl(true); //Auto creating scheme when true
        hibernateJpaVendorAdapter.setDatabase(Database.MYSQL);//Database type
        return hibernateJpaVendorAdapter;
    }

    @Bean(name = "mariadbTransactionManager")
    @ConfigurationProperties("spring.jpa")
    public PlatformTransactionManager transactionManager(
            @Qualifier("mariadbEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
