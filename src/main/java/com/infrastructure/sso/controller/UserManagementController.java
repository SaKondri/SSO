package com.infrastructure.sso.controller;


import com.infrastructure.sso.dto.GroupDto;
import com.infrastructure.sso.dto.UserInformationForEdit;
import com.infrastructure.sso.dto.req.user.UserInformation;
import com.infrastructure.sso.services.interfaces.SSO_Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

/**
 * @author SaKondri
 */

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/UserManagement")
@RequiredArgsConstructor
public class UserManagementController {

    private final SSO_Service sso_service;


    @GetMapping("/test")
    @PreAuthorize("hasAnyAuthority('createUser')")
    public void test(){
        System.out.println("test call");
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/addUser")
    @PreAuthorize("hasAnyAuthority('createUser')")
    public void addUser(@RequestBody UserInformation userInformation){
        try {
            sso_service.addUser(userInformation);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/showUsers")
    @PreAuthorize("hasAnyAuthority('showUsers')")
    public List<UserInformation> showUsers() throws IOException, InterruptedException {
       return sso_service.showUsers();
    }


    @ResponseStatus(HttpStatus.CREATED)
    @PutMapping("/editUser")
    @PreAuthorize("hasAnyAuthority('createUser')")
    public void editUser(@RequestBody UserInformationForEdit userInformationForEdit){
        try {
            sso_service.editUser(userInformationForEdit);
            sso_service.leaveGroup(userInformationForEdit);
            sso_service.joinUserToGroup(userInformationForEdit);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @GetMapping("/getGroups")
    //@PreAuthorize("hasAnyAuthority('createUser')")
    public List<GroupDto> getGroups() throws IOException, InterruptedException {
        List<GroupDto> groups = sso_service.getGroups();
        return groups;
    }
}
