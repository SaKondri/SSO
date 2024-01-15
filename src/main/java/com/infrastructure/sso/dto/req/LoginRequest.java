package com.infrastructure.sso.dto.req;

import javax.validation.constraints.NotBlank;

/**
 * @author SaKondri
 */

public class LoginRequest {


    public LoginRequest(){

    }

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }


    @NotBlank
    private String username;

    @NotBlank
    private String password;

    public String getUsername() {
        return username.toLowerCase();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}