package com.asrori;

import com.asrori.configuration.BeanConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class KoneksiPooling {
    public static void main(String[] args) throws SQLException {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(BeanConfiguration.class);

        DataSource dataSource = applicationContext.getBean("dataSource", DataSource.class);
        Connection connection = dataSource.getConnection();
        System.out.println("koneksi ditutup : " + connection.isClosed());
        connection.close();
        System.out.println("koneksi ditutup : " + connection.isClosed());

        applicationContext.close();
    }
}
