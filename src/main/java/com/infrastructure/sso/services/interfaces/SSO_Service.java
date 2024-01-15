package com.infrastructure.sso.services.interfaces;


import com.infrastructure.sso.dto.GroupDto;
import com.infrastructure.sso.dto.Token;
import com.infrastructure.sso.dto.UserInformationForEdit;
import com.infrastructure.sso.dto.req.user.UserInformation;
import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author SaKondri
 */
public interface SSO_Service {

    public Token login(String username, String password);

    public Token refreshToken(String refreshToken);

    public Token adminLogin() throws IOException, InterruptedException;


    void joinUserToGroup(UserInformationForEdit informationForEdit) throws IOException, InterruptedException;

    void editUser(UserInformationForEdit informationForEdit) throws IOException, InterruptedException;

    void leaveGroup(UserInformationForEdit informationForEdit) throws IOException, InterruptedException;

    void addUser(UserInformation userInformation) throws IOException, InterruptedException;

    List<UserInformation> showUsers() throws IOException, InterruptedException;

    void resetPassword() throws IOException, InterruptedException;

    void deleteUser() throws IOException, InterruptedException;

    List<GroupDto> getGroups() throws IOException, InterruptedException;
}
