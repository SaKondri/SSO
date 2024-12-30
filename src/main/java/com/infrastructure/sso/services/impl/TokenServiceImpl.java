package com.infrastructure.sso.services.impl;

import com.infrastructure.sso.dto.Token;
import com.infrastructure.sso.dto.TokenDetails;
import com.infrastructure.sso.repository.cache.TokenRepository;
import com.infrastructure.sso.security.jwt.JwtUtils;
import com.infrastructure.sso.services.interfaces.TokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

/**
 * @author SaKondri
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;

    private final JwtUtils jwtUtils;

    @Override
    public void removeWhenHasToken(Token token) throws NoSuchAlgorithmException, InvalidKeySpecException {
       TokenDetails parseToken = jwtUtils.getTokenDetails(token.getAccess_token());
        log.debug("remove token" , token);
       if(parseToken != null){
           Token cachedToken = tokenRepository.findByUsername(parseToken.getPreferred_username());
           if(cachedToken != null){
               removeToken(cachedToken);
           }
       }
    }

    @Override
    public void removeToken(Token token) {
        tokenRepository.deleteById(token.getAccess_token());
    }

    @Override
    public Token save(Token token){
        log.debug("i want save" +token.getUsername());
        log.debug("i want save {}" , token);
      return tokenRepository.save(token);
    }

    @Override
    public Token findById(String token){
        Token result = tokenRepository.findById(token).get();
        return result;
    }

    @Override
    public Token findByRefresh_token(String refresh_token){
        Token result = tokenRepository.findByRefreshToken(refresh_token);
        return result;
    }

    @Override
    public void deleteAll() {
        tokenRepository.deleteAll();
    }

    @Override
    public Token findByUsername(String username) {
       return tokenRepository.findByUsername(username);
    }

    @Override
    public void deleteById(String token){
        tokenRepository.deleteById(token);
    }

    @Override
    public String getTokenWithoutPrefixBearer(String authorizationHeader){
       return jwtUtils.getTokenWithoutPrefixBearer(authorizationHeader);
    }

    @Override
    public List<Token> findAll() {
        return (List<Token>) tokenRepository.findAll();
    }


    @Override
    public void deleteUsername(String username) {
        tokenRepository.deleteByUsername(username);
    }
}
