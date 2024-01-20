package com.infrastructure.sso.security.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infrastructure.sso.dto.Token;
import com.infrastructure.sso.dto.TokenDetails;
import com.infrastructure.sso.services.interfaces.TokenService;
import io.jsonwebtoken.*;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.apache.commons.codec.binary.Base64;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.List;

@Component
@PropertySource(
        ignoreResourceNotFound = false,
        value = "classpath:jwt-config.properties")

public class JwtUtils {

    @Autowired(required = false)
    private TokenService tokenService;

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${keyCloakPublicKey}")
    private String keyClockPublicKey;

    public Claims decodeToken(String token) throws NoSuchAlgorithmException, InvalidKeySpecException {

        byte[] publicBytes = java.util.Base64.getDecoder().decode(keyClockPublicKey.getBytes());
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey pubKey = keyFactory.generatePublic(keySpec);
       Claims claims =  Jwts.parser().setSigningKey(pubKey).parseClaimsJws(token).getBody();
       return claims;
    }

    public TokenDetails getTokenDetails(String string) throws NoSuchAlgorithmException, InvalidKeySpecException {
        Claims claims = decodeToken(string);
        String email = (String) claims.get("email");
        String preferred_username = (String) claims.get("preferred_username");
        Integer exp = (Integer) claims.get("exp");
        String sub = (String) claims.get("sub");
       TokenDetails result = TokenDetails.builder()
                .email(email)
                .preferred_username(preferred_username)
                .exp(exp)
                .sub(sub)
               .build();
       return result;
    }


    public boolean validateJwtToken(String authToken) throws NoSuchAlgorithmException, InvalidKeySpecException {
        try {
            Claims claims = decodeToken(authToken);
            String username = (String) claims.get("preferred_username");
            Token cachedToken = tokenService.findByUsername(username.toLowerCase());
            if(cachedToken == null ){
                return false;
            }
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
            return false;
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
            return false;
        }
    }


    public Token getToken(String stringToken){
        Token token = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            token = objectMapper.readValue(stringToken, Token.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return token;
    }


    public String getTokenWithoutPrefixBearer(String token){
        return token.replace("Bearer " , "");
    }

}
