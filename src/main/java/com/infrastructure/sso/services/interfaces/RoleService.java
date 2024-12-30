package com.infrastructure.sso.services.interfaces;

import com.infrastructure.sso.dto.GroupDto;
import com.infrastructure.sso.dto.req.user.KeyCloakRoleDto;

import java.io.IOException;
import java.util.List;

/**
 * @author SaKondri
 */
public interface RoleService {

    List<GroupDto> getCurrentUserRole(String userId , SSO_Service sso_service) throws IOException, InterruptedException;
}
