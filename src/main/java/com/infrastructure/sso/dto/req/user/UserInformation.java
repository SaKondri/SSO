package com.infrastructure.sso.dto.req.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author SaKondri
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInformation implements Serializable {
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private ArrayList<Credential> credentials;
    private boolean enabled;
    private ArrayList<String> groups;
    //private ArrayList<KeyCloakRoleDto> roles;
    private Attributes attributes;
}

