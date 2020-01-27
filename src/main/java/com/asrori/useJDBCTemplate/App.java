package com.asrori.useJDBCTemplate;

import com.asrori.domain.Akun;
import com.asrori.domain.repository.AkunRepository;
import com.asrori.configuration.BeanConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(BeanConfiguration.class);

        AkunRepository akunRepository = applicationContext.getBean(AkunRepository.class);

        Akun akun = akunRepository.find("budi");
        System.out.println(akun.getId());
        System.out.println(akun.getNama());
        System.out.println(akun.getSaldo());
        System.out.println(akun.getWaktuAkses());
        System.out.println(akun.isTerkunci());

        applicationContext.close();
    }
}
