package com.infrastructure.sso.exception;

/**
 * @author SaKondri
 */
public class RefreshTokenException extends RuntimeException{
    public RefreshTokenException(){
        super("Refresh token operation failed");
    }

}
