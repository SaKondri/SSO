package com.infrastructure.sso;

import com.infrastructure.sso.security.jwt.JwtUtils;
import com.infrastructure.sso.security.jwt.filter.SignOutCheck;
import com.infrastructure.sso.services.interfaces.SSO_Service;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import java.util.Arrays;
import java.util.Properties;
import java.util.stream.StreamSupport;

@SpringBootApplication
@EnableScheduling
public class SsoApplication {

    @Bean
    public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean(new HttpSessionEventPublisher());
    }

    public static void main(String[] args) {
        SpringApplication.run(SsoApplication.class, args);
    }


    @Bean
    public CommandLineRunner init(
            @Value("${keyCloakPublicKey}") String key,
            @Autowired JwtUtils JwtUtils,
            @Autowired SSO_Service sso_service,
            @Autowired Environment env
            ){
        return args -> {
            boolean valid = JwtUtils.isValidRefreshTokenTime("eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJiZGM1NzFhOS05MDcxLTRjZGUtYTU2Yy0yYjNmMmU0YjUzZWMifQ.eyJleHAiOjE3MjIyNTE5MTEsImlhdCI6MTcyMjI1MDExMSwianRpIjoiYzgwZTIxMzUtM2I5Zi00YTE0LTg2YjMtNjBmZThiYjVkMTAzIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy93ZWJBcHBsaWNhdGlvbiIsImF1ZCI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODA4MC9yZWFsbXMvd2ViQXBwbGljYXRpb24iLCJzdWIiOiI0NmU3NjBjZi02OThhLTRhNTEtOTAzYS1lOGQ3NTJjNDA1YTMiLCJ0eXAiOiJSZWZyZXNoIiwiYXpwIjoibXlBcHAiLCJzZXNzaW9uX3N0YXRlIjoiZWU0NjM4ZTQtZmY0MC00MDA1LTkzY2ItZmFiZTY0NDgwYmY5Iiwic2NvcGUiOiJwcm9maWxlIGVtYWlsIiwic2lkIjoiZWU0NjM4ZTQtZmY0MC00MDA1LTkzY2ItZmFiZTY0NDgwYmY5In0.jSPP0TeijdRt2_bE_tSVOB41UkgK6Y_RmY2FRHwnVJE");
           //JwtUtils.decodeRefreshToken("eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJiZGM1NzFhOS05MDcxLTRjZGUtYTU2Yy0yYjNmMmU0YjUzZWMifQ.eyJleHAiOjE3MjIyNDIyODgsImlhdCI6MTcyMjI0MDQ4OCwianRpIjoiZTU3ZDJlYWYtMDI0Mi00Mzg1LWEwYjgtNjljMGNkZTBjODA5IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy93ZWJBcHBsaWNhdGlvbiIsImF1ZCI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODA4MC9yZWFsbXMvd2ViQXBwbGljYXRpb24iLCJzdWIiOiI0NmU3NjBjZi02OThhLTRhNTEtOTAzYS1lOGQ3NTJjNDA1YTMiLCJ0eXAiOiJSZWZyZXNoIiwiYXpwIjoibXlBcHAiLCJzZXNzaW9uX3N0YXRlIjoiNjc2ZjU4ZmYtZDExMi00NDU1LThhNDAtNGNkNTRlZjJkNTQ5Iiwic2NvcGUiOiJwcm9maWxlIGVtYWlsIiwic2lkIjoiNjc2ZjU4ZmYtZDExMi00NDU1LThhNDAtNGNkNTRlZjJkNTQ5In0.qYZgjsLQDe0r7kp6euvh9tX7_2YvwDdPsAnGJUiSapI");
            System.out.println(valid);
        };
    }

//    @Bean
//    public FilterRegistrationBean<SignOutCheck> loggingFilter(){
//        FilterRegistrationBean<SignOutCheck> registrationBean = new FilterRegistrationBean<>();
//
//        registrationBean.setFilter(new SignOutCheck());
//        registrationBean.addUrlPatterns("/*");
//       // registrationBean.setOrder(2);
//        return registrationBean;
//    }

}
