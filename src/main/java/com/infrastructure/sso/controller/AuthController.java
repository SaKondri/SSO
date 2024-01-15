package com.infrastructure.sso.controller;


import com.infrastructure.sso.dto.Token;
import com.infrastructure.sso.dto.TokenDetails;
import com.infrastructure.sso.dto.req.LoginRequest;
import com.infrastructure.sso.dto.req.RefreshToken;
import com.infrastructure.sso.security.jwt.JwtUtils;
import com.infrastructure.sso.services.interfaces.SSO_Service;
import com.infrastructure.sso.services.interfaces.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

/**
 * @author SaKondri
 */

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SSO_Service sso_service;

    private final TokenService tokenService;

    private final JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<Token> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String user = loginRequest.getUsername();
        String pass = loginRequest.getPassword();
        Token token  = sso_service.login(user,pass);
        token.setUsername(loginRequest.getUsername());
        if(token != null){
            tokenService.removeWhenHasToken(token);
        }
        tokenService.save(token);
        return ResponseEntity.ok(token);
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("/refreshtoken")
    public ResponseEntity<Token> refreshToken(@RequestBody RefreshToken request) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String refreshToken = request.getRefreshToken();
        Token newToken =  sso_service.refreshToken(refreshToken);
        Token cachedToken = tokenService.findByRefresh_token(request.getRefreshToken());
        if(cachedToken != null){
            tokenService.deleteById(cachedToken.getAccess_token());
            newToken.setUsername(cachedToken.getUsername());
        }
        tokenService.save(newToken);
        return ResponseEntity.ok(newToken);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/signOut")
    public void signOut(@RequestHeader (name="Authorization") String authorizationHeader) throws NoSuchAlgorithmException, InvalidKeySpecException {

        String token = tokenService.getTokenWithoutPrefixBearer(authorizationHeader);
        tokenService.deleteById(token);
    }

}
