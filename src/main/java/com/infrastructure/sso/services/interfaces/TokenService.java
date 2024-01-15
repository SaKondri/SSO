package com.infrastructure.sso.services.interfaces;

import com.infrastructure.sso.dto.Token;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

/**
 * @author SaKondri
 */
public interface TokenService {

    public void removeWhenHasToken(Token token) throws NoSuchAlgorithmException, InvalidKeySpecException;

    void removeToken(Token token);

    Token save(Token token);

    Token findById(String token);

    Token findByRefresh_token(String refresh_token);

    void deleteAll();

    Token findByUsername(String username);

    void deleteById(String token);

    void deleteUsername(String username);

    String getTokenWithoutPrefixBearer(String authorizationHeader);

    List<Token> findAll();
}
