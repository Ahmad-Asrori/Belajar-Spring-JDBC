package com.asrori.configuration;

import com.asrori.UseJDBCTemplate.dao.AkunDao;
import com.asrori.UseJDBCTemplate.dao.implementation.AkunDaoImpl;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class BeanConfiguration {

    /*
    @Bean
    public DataSource dataSource(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5432/bank");
        dataSource.setUsername("postgres");
        dataSource.setPassword("asrori08");
        return dataSource;
    }
    */

    // menggunakan connection pooling dari library apache commons DBCP
    @Bean
    public DataSource dataSource(){
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5432/bank");
        dataSource.setUsername("postgres");
        dataSource.setPassword("asrori08");
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(){
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource());
        return jdbcTemplate;
    }

    @Bean
    public AkunDao akunDao(){
        AkunDaoImpl akunDao = new AkunDaoImpl();
        akunDao.setJdbcTemplate(jdbcTemplate());
        return akunDao;
    }

}
