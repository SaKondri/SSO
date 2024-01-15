package com.infrastructure.sso.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author SaKondri
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenDetails {
    private Integer exp;
    private String sub;
    private String preferred_username;
    private String email;
}
