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
public class UserManagementDto {
    private Long id;
    private String name;
    private String password;
    private String image;
    private String email;
    private String username;
    private String stringRoles;

}
