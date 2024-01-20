package com.infrastructure.sso;

import com.infrastructure.sso.security.jwt.JwtUtils;

import com.infrastructure.sso.services.interfaces.SSO_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;

import org.springframework.security.web.session.HttpSessionEventPublisher;

import javax.crypto.Cipher;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;

import java.util.Base64;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;


/**
 * @author SaKondri
 */

@SpringBootApplication

public class SsoApplication {

    @Bean
    public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean<HttpSessionEventPublisher>(new HttpSessionEventPublisher());
    }

    public static void main(String[] args) {
        SpringApplication.run(SsoApplication.class, args);
    }


    @Bean
    public CommandLineRunner init(
            @Value("${keyCloakPublicKey}") String key,
            @Autowired JwtUtils JwtUtils,
            @Autowired SSO_Service sso_service
            ){
        return args -> {
          // jwtUtils.validateJwtToken("eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJjaVcycUVISDJkb2pQQVh1M2w5dW1Tb3A1S2I2emJ1QmJDVnhQTVFpcGhNIn0.eyJleHAiOjE3MDQ4MzEzNTAsImlhdCI6MTcwNDgzMTA1MCwianRpIjoiMTBiMmU0MWYtODY2Yi00YjJmLTg2OTctZmQ5NTM4ODNkNDQ4IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy93ZWJBcHBsaWNhdGlvbiIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiJjOWE3YmJlYi1iYjcwLTQ5ZmUtOTBmMC03NDU3ZDg5ZTM1NzAiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJteUFwcCIsInNlc3Npb25fc3RhdGUiOiI4MDdlODI4MS0xMzZlLTQxNGQtYmNhMy1iODBkZDJmYjAxNGMiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbIi8qIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyIzMyIsIjQ0IiwiZGVmYXVsdC1yb2xlcy13ZWJhcHBsaWNhdGlvbiIsIm9mZmxpbmVfYWNjZXNzIiwiY3JlYXRlVXNlciIsInVtYV9hdXRob3JpemF0aW9uIiwic2hvd1VzZXJzIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiIiLCJzaWQiOiI4MDdlODI4MS0xMzZlLTQxNGQtYmNhMy1iODBkZDJmYjAxNGMiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJzYWVlZGFtaW5pIiwiZW1haWwiOiJteWVtYWlsQGdtYWlsLmNvbSJ9.NmbnMX1YVMAnqfAiHhUMl2vE4wWEJaIfpq3930hhHKRUkZqnw2DYW9WBP7oYI__SBkigsEYn_gJLCADfvyx-uWrvaRzOApKI8Xec4CFMiffXIWFaJAopgFA4awDHuPw9K6WV8KGDrEQa3dljXFHsuyaxsAGiaKPTYHMg3jIunKfhK3-JwtgbZ6mpZIm5oBd7fgPa3IhZ7LLWVKbCWl-yazS0u_hSQiGpSgfN4xlx7eZZC0J_3REDMOtUhmj-qMZzDjFhdSPnfeevXWyxjeCi_23V3dICnmHR7V_pypVLGYGPO22hBlyVEQ5JhRQAjJV-JNWzsQbyRr2aB4Ato2-l-w");
        };
    }

}
