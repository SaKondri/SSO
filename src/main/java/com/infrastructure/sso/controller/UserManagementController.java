package com.infrastructure.sso.controller;


import com.infrastructure.sso.dto.GroupDto;
import com.infrastructure.sso.dto.UserInformationForEdit;
import com.infrastructure.sso.dto.req.UsernamePasswordDto;
import com.infrastructure.sso.dto.req.user.KeyCloakRoleDto;
import com.infrastructure.sso.dto.req.user.UserInformation;
import com.infrastructure.sso.dto.req.user.UserInformationWithRoles;
import com.infrastructure.sso.dto.resp.UserInformationForGroupReq;
import com.infrastructure.sso.services.cache.GroupCacheService;
import com.infrastructure.sso.services.cache.UserCacheService;
import com.infrastructure.sso.services.interfaces.SSO_Service;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author SaKondri
 */

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/UserManagement")
@RequiredArgsConstructor
public class UserManagementController {

    private final SSO_Service sso_service;

    private final UserCacheService userCacheService;

    private final GroupCacheService groupCacheService;

    //@ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/addUser")
    @PreAuthorize("hasAnyAuthority('createUser')")
    public ResponseEntity<?> addUser(@RequestBody UserInformationWithRoles userInformationWithRoles) throws Exception {
        try {
            List<String> roles =  userInformationWithRoles.getRoles().stream().map(KeyCloakRoleDto::getName).collect(Collectors.toList());
            UserInformation userInformation = UserInformation.builder()
                    .attributes(userInformationWithRoles.getAttributes())
                    .email(userInformationWithRoles.getEmail())
                    .credentials(userInformationWithRoles.getCredentials())
                    .lastName(userInformationWithRoles.getLastName())
                    .firstName(userInformationWithRoles.getFirstName())
                    .enabled(userInformationWithRoles.isEnabled())
                    .username(userInformationWithRoles.getUsername())
                    .groups(new ArrayList<>(roles))
                    .build();
            sso_service.addUser(userInformation);
            //userCacheService.addAllUser(null);
            groupCacheService.removeAllGroupCache();
            groupCacheService.removeGroup();
            userCacheService.removeAll();
            return ResponseEntity.ok(userInformationWithRoles);
        } catch (IOException | InterruptedException | RuntimeException e) {
            e.printStackTrace();
           return ResponseEntity.status(500).body(e);
        }
    }


    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/getUsers")
   // @PreAuthorize("hasAnyAuthority('getUsers')")
    public List<UserInformation> showUsers() throws IOException, InterruptedException {
            List<UserInformation> users = sso_service.showUsers();
            List<GroupDto> groupDtos = sso_service.getGroups();
            groupDtos.forEach(groupDto -> {
                List<UserInformationForGroupReq> userGroups = sso_service.getGroupsWithMember(groupDto.getId());
                List<UserInformation> result = users.stream().filter(userInformation -> userGroups.stream().anyMatch(userGroup -> {
                    if(userInformation.getId().equals(userGroup.getId())){
                        if(userInformation.getGroups() == null){
                            userInformation.setGroups(new ArrayList<>());
                        }
                        userInformation.getGroups().add(groupDto.name);
                    }
                    return false;
                })).collect(Collectors.toList());
            });
            return users;
    }


    @ResponseStatus(HttpStatus.CREATED)
    @PutMapping("/editUser")
    @PreAuthorize("hasAnyAuthority('createUser')")
    public void editUser(@RequestBody UserInformationForEdit userInformationForEdit){
        try {
            if(userInformationForEdit.getUserInformation().getUsername() == null || userInformationForEdit.getUserInformation().getEmail() == null){
                throw new RuntimeException("Username or email not be null.");
            }
            sso_service.editUser(userInformationForEdit);
            sso_service.leaveGroup(userInformationForEdit);
            sso_service.joinUserToGroup(userInformationForEdit);
            groupCacheService.removeAllGroupCache();
            groupCacheService.removeGroup();
            userCacheService.removeAll();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @GetMapping("/'getGroups'")
    //@PreAuthorize("hasAnyAuthority('createUser')")
    public List<GroupDto> getGroups() throws IOException, InterruptedException {
        List<GroupDto> groups = sso_service.getGroups();
        //sso_service.getGroupsWithMember();
        return groups;
    }


    @ResponseStatus(HttpStatus.CREATED)
    @DeleteMapping("/deleteUser")
    @PreAuthorize("hasAnyAuthority('createUser')")
    public void deleteUser(@RequestBody UserInformation userInformation) throws IOException, InterruptedException {
        sso_service.deleteUser(userInformation);
        groupCacheService.removeAllGroupCache();
        groupCacheService.removeGroup();
        userCacheService.removeAll();
    }


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/changePassword")
    public void changePassword(@RequestBody UsernamePasswordDto userPass) throws IOException, InterruptedException {
        sso_service.resetPassword(userPass);
        groupCacheService.removeAllGroupCache();
        groupCacheService.removeGroup();
        userCacheService.removeAll();
    }
}
