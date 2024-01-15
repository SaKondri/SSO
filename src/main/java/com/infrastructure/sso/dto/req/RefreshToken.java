package com.infrastructure.sso.dto.req;

import javax.validation.constraints.NotBlank;

/**
 * @author SaKondri
 */

public class RefreshToken {
    @NotBlank
    private String refreshToken;

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}

