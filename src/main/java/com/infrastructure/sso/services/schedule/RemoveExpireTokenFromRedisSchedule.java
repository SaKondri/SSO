package com.infrastructure.sso.services.schedule;

import com.infrastructure.sso.dto.Token;
import com.infrastructure.sso.repository.cache.TokenRepository;
import com.infrastructure.sso.security.jwt.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

/**
 * @author SaKondri
 */

@Component
public class RemoveExpireTokenFromRedisSchedule {

    @Autowired
    private TokenRepository tokenRepository;


    @Autowired
    private JwtUtils jwtUtils;

    //@Scheduled(cron = "0 */5 * ? * *")
    @Scheduled(fixedRate = 10000)
    public void RemoveExpireToken(){
       List<Token> tokens = (List<Token>) tokenRepository.findAll();
       tokens.forEach(token -> {
           try {
               boolean isValid =  jwtUtils.isValidRefreshTokenTime(token.getRefreshToken());
               if(!isValid){
                   tokenRepository.deleteById(token.getAccess_token());
               }
           } catch (Exception e) {
           }

       });
    }
}
