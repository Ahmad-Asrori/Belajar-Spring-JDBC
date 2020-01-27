package com.asrori.configuration;

import com.asrori.domain.Akun;
import com.asrori.modelingjdbcjavaobject.AkunByIdQuery;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.object.MappingSqlQuery;

import javax.sql.DataSource;

@Configuration
@ComponentScan("com.asrori")
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
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(){
        return new NamedParameterJdbcTemplate(jdbcTemplate());
    }

    @Bean
    public SimpleJdbcCall simpleJdbcCall(){
        return new SimpleJdbcCall(jdbcTemplate());
    }

    @Bean
    public MappingSqlQuery<Akun> akunByIdQuery(){
        AkunByIdQuery akunByIdQuery = new AkunByIdQuery(dataSource());
        return akunByIdQuery;
    }
}
