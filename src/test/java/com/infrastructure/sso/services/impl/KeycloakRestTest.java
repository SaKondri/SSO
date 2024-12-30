package com.infrastructure.sso.services.impl;


import com.infrastructure.sso.dto.GroupDto;
import com.infrastructure.sso.dto.Token;
import com.infrastructure.sso.dto.UserInformationForEdit;
import com.infrastructure.sso.dto.req.LoginRequest;
import com.infrastructure.sso.dto.req.RefreshToken;
import com.infrastructure.sso.dto.req.user.Attributes;
import com.infrastructure.sso.dto.req.user.Credential;
import com.infrastructure.sso.dto.req.user.UserInformation;
import com.infrastructure.sso.services.interfaces.SSO_Service;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author SaKondri
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class KeycloakRestTest {


    @LocalServerPort
    private int port;

    private Token token;


    private  String authAddress;
    private  String userManagementAddress;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SSO_Service sso_service;
    private List<UserInformation> listOfUser;
    private List<GroupDto> groupList;


    @BeforeAll()
    void initAddress() {
        String baseAddress = "http://localhost:";

        authAddress = baseAddress+port+"/sso/api/auth";
        userManagementAddress = baseAddress+port+"/sso/api/UserManagement";
    }


    @Test
    @Order(0)
    void keycloakAdmin_AddAdminUser() throws IOException, InterruptedException {
        UserInformation userInformation = UserInformation.builder()
                .username("saeedKondri")
                .email("saeid.kondri@gmail.com")
                .firstName("sa")
                .lastName("am")
                .enabled(true)
                .groups(new ArrayList<>(Arrays.asList("admin" , "manger" ))) // "admin" and "manger" These Roles must add in RealmRoles section in keycloak otherwise you cannot add user
                .build();
        Attributes attributes= Attributes.builder()
                .address(new ArrayList<>(Arrays.asList("Iran")))
                .phone(new ArrayList<>(Arrays.asList("+989122136508")))
                .zipCode(new ArrayList<>(Arrays.asList("11111")))
                .build();
        Credential credential = Credential.builder()
                .temporary(false)
                .value("123") // password value
                .type("password")
                .build();
        userInformation.setCredentials(new ArrayList<>(Arrays.asList(credential)));
        userInformation.setAttributes(attributes);

        sso_service.addUser(userInformation);
    }


    @Test
    @Order(1)
    void loginCreatedAdmin(){
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("saeedKondri");
        loginRequest.setPassword("123");
        String api = authAddress +"/signin";
        HttpEntity<?> httpEntity = new HttpEntity<Object>(loginRequest);
        ResponseEntity<Token> response = restTemplate.exchange(api, HttpMethod.POST, httpEntity, Token.class);
        assertEquals(response.getStatusCode().value() ,200);
        assertNotNull(response.getBody().getAccess_token());
        token = response.getBody();
        token.setAccess_token("Bearer "+token.getAccess_token());
    }

    @Test
    @Order(2)
    void getGroups(){
        String api = userManagementAddress+"/getGroups";
        ResponseEntity<GroupDto[]> entity = restTemplate.exchange(
                api, HttpMethod.GET, new HttpEntity<>(authorizationHeader()),
                GroupDto[].class);
        GroupDto[] groups = entity.getBody();
        //listOfUser =  Arrays.stream(users).collect(Collectors.toList());
        groupList = Arrays.stream(groups).collect(Collectors.toList());
        assertTrue(groups.length > 0);
    }


    @Test
    @Order(3)
    void addUserByAdminRealm(){
        UserInformation userInformation = UserInformation.builder()
                .username("SaeedAmini")
                .email("saeed.yahoo00@yahoo.com")
                .enabled(true)
                .firstName("saeed")
                .lastName("amini")
                .groups(new ArrayList<>(Arrays.asList("admin" , "manger" ))) // "admin" and "manger" These Roles must add in RealmRoles section in keycloak otherwise you cannot add user
                .build();
        Attributes attributes= Attributes.builder()
                .address(new ArrayList<>(Arrays.asList("Iran")))
                .phone(new ArrayList<>(Arrays.asList("+989122136508")))
                .zipCode(new ArrayList<>(Arrays.asList("11111")))
                .build();
        Credential credential = Credential.builder()
                .temporary(false)
                        .value("123") // password value
                                .type("password")
                .build();
        userInformation.setCredentials(new ArrayList<>(Arrays.asList(credential)));
        userInformation.setAttributes(attributes);
        String api = userManagementAddress+"/addUser";
        HttpEntity<?> httpEntity = new HttpEntity<Object>(userInformation,authorizationHeader());
        ResponseEntity response = restTemplate.exchange(api, HttpMethod.POST, httpEntity,void.class);
        assertEquals(response.getStatusCode().value() ,201);

    }



    @Test
    @Order(4)
    void refreshToken(){
        String api = authAddress+"/refreshtoken";
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setRefreshToken(token.getRefreshToken());
        HttpEntity<?> httpEntity = new HttpEntity<Object>(refreshToken);
        ResponseEntity<Token> response = restTemplate.exchange(api, HttpMethod.POST, httpEntity, Token.class);
        token = response.getBody();
        token.setAccess_token("Bearer "+token.getAccess_token());
        assertNotNull(response.getBody().getAccess_token() , "! Error access token null");
    }

    @Test
    @Order(5)
    void getUsers(){
        String api = userManagementAddress+"/getUsers";
        ResponseEntity<UserInformation[]> entity = restTemplate.exchange(
                api, HttpMethod.GET, new HttpEntity<>(authorizationHeader()),
                UserInformation[].class);
        UserInformation[] users = entity.getBody();
        listOfUser =  Arrays.stream(users).collect(Collectors.toList());
        assertTrue(users.length > 0);
    }

    @Test
    @Order(6)
    void editUser(){
       UserInformation userInformation =  listOfUser.get(0);
       userInformation.setEmail("myEmail@gmail.com");
       userInformation.setGroups(new ArrayList<>(Arrays.asList("user")));
       UserInformationForEdit req = UserInformationForEdit.builder().userInformation(userInformation).allGroups(groupList).build();
        String api = userManagementAddress+"/editUser";
        HttpEntity<?> httpEntity = new HttpEntity<Object>(req,authorizationHeader());
        ResponseEntity response = restTemplate.exchange(api, HttpMethod.PUT, httpEntity,void.class);
        assertEquals(response.getStatusCode().value() ,201);
    }

    @Test
    @Order(7)
    void deleteUser(){

        listOfUser.forEach(userInformation -> {
            HttpEntity<?> httpEntity = new HttpEntity<Object>(userInformation,authorizationHeader());
            String api = userManagementAddress+"/deleteUser";
            ResponseEntity<Void> response = restTemplate.exchange(api, HttpMethod.DELETE, httpEntity, Void.class);
            assertEquals(response.getStatusCode().value() ,201);
        });
    }

    @Test
    @Order(8)
    void signOut(){
        String api = authAddress+"/signOut";
        String header = token.getAccess_token().replace("Bearer " , "");
        authorizationHeader().add("Authorization", header);
        HttpEntity<?> httpEntity = new HttpEntity<Object>(authorizationHeader());
        ResponseEntity response = restTemplate.exchange(api, HttpMethod.GET, httpEntity,void.class);
        assertEquals(response.getStatusCode().value() ,200);
    }





    private MultiValueMap authorizationHeader(){
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");
        headers.add("Authorization", token.getAccess_token());
        return headers;
    }

}