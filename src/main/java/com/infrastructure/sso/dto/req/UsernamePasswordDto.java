package com.infrastructure.sso.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author SaKondri
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsernamePasswordDto {
    private String username;
    private String password;
}
