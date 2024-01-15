package com.infrastructure.sso.dto;

/**
 * @author SaKondri
 */
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;

/**
 * @author SaKondri
 */

@RedisHash("Token")
public class Token implements Serializable {


    @Id
    @Indexed
    private String access_token;

    private Long expires_in;

    private Long refresh_expires_in;

    @Indexed
    @JsonProperty("refresh_token")
    private String refreshToken;

    private String token_type;

    @JsonProperty("not-before-policy")
    private Long not_before_policy;

    private String session_state;

    private String scope;


    @Indexed
    private String username;

    private String email;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public Long getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(Long expires_in) {
        this.expires_in = expires_in;
    }

    public Long getRefresh_expires_in() {
        return refresh_expires_in;
    }

    public void setRefresh_expires_in(Long refresh_expires_in) {
        this.refresh_expires_in = refresh_expires_in;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public Long getNot_before_policy() {
        return not_before_policy;
    }

    public void setNot_before_policy(Long not_before_policy) {
        this.not_before_policy = not_before_policy;
    }

    public String getSession_state() {
        return session_state;
    }

    public void setSession_state(String session_state) {
        this.session_state = session_state;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getEmail() {
        return email;
    }


    public String getUsername() {
        return username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {

        this.username = username.toLowerCase();
    }
}
