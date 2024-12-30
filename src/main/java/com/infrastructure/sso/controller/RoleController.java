package com.infrastructure.sso.controller;

import com.infrastructure.sso.dto.GroupDto;
import com.infrastructure.sso.dto.req.RealmRoles;
import com.infrastructure.sso.dto.req.user.KeyCloakRoleDto;
import com.infrastructure.sso.dto.req.user.UserInformation;
import com.infrastructure.sso.dto.resp.UserInformationForGroupReq;
import com.infrastructure.sso.services.interfaces.RoleService;
import com.infrastructure.sso.services.interfaces.SSO_Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author SaKondri
 */

@RestController
@RequestMapping("/api/role")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class RoleController {


    private final SSO_Service sso_service;
    private final RoleService roleService;

    @GetMapping("/getCurrentUserRole")
    @ResponseStatus(HttpStatus.CREATED)
    public List<String> getCurrentUserRole(JwtAuthenticationToken authenticationToken) throws IOException, InterruptedException {
       List<GroupDto> userGroups = roleService.getCurrentUserRole(authenticationToken.getName() , sso_service);
        return userGroups.stream().map(GroupDto::getName).collect(Collectors.toList());
    }


    @ResponseStatus(HttpStatus.CREATED)
    @GetMapping("/getGroups")
    //@PreAuthorize("hasAnyAuthority('createUser')")
    public List<GroupDto> getGroups() throws IOException, InterruptedException {
        List<GroupDto> groups = sso_service.getGroups();
        //sso_service.getGroupsWithMember();
        return groups;
    }


    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/getRealmRoles")
    public List<RealmRoles> getAllRealmRoles(){
        List<RealmRoles> result= null;
        try {
            result = sso_service.getRealmRoles();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return result;
    }


}
