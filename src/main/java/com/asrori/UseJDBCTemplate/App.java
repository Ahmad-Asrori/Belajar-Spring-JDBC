package com.asrori.UseJDBCTemplate;

import com.asrori.UseJDBCTemplate.dao.Akun;
import com.asrori.UseJDBCTemplate.dao.AkunDao;
import com.asrori.UseJDBCTemplate.dao.implementation.AkunDaoImpl;
import com.asrori.configuration.BeanConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(BeanConfiguration.class);

        AkunDao akunDao = applicationContext.getBean(AkunDao.class);
        Akun akun = akunDao.find(100);

        System.out.println(akun.getId());
        System.out.println(akun.getNama());
        System.out.println(akun.getSaldo());
        System.out.println(akun.getWaktuAkses());
        System.out.println(akun.isTerkunci());
    }
}
