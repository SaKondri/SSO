package com.infrastructure.sso.services.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.infrastructure.sso.dto.GroupDto;
import com.infrastructure.sso.dto.Token;
import com.infrastructure.sso.dto.UserInformationForEdit;
import com.infrastructure.sso.dto.req.user.UserInformation;
import com.infrastructure.sso.security.jwt.JwtUtils;
import com.infrastructure.sso.services.interfaces.SSO_Service;
import com.infrastructure.sso.utils.ConvertJsonArrayToList;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import com.google.common.base.Strings;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * @author SaKondri
 */

@Service
@RequiredArgsConstructor
@Configuration
@PropertySource(
        ignoreResourceNotFound = false,
        value = "classpath:keycloak-config.properties")
@Log4j2
public class KeycloakRestService implements SSO_Service {

    private final RestTemplate restTemplate;

    private final JwtUtils jwtUtils;

    @Value("${allThisApplicationUsers}")
    private String allThisApplicationUsers;

    @Value("${keycloak.token-uri}")
    private String keycloakTokenUri;

    @Value("${keycloak.user-info-uri}")
    private String keycloakUserInfo;

    @Value("${keycloak.logout}")
    private String keycloakLogout;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${adminRealms}")
    private String adminRealms;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @Value("${allThisApplicationRoleGroup}")
    private String allThisApplicationRoleGroup;

    @Override
    public Token login(String username, String password) {
        log.debug("call Login for username {} " , username);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("username", username);
        map.add("password", password);
        map.add("client_id", clientId);
        map.add("grant_type", "password");
        map.add("client_secret", clientSecret);
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        log.info("Send login request to SSO, username: {}" , username);
        String result = "";
        try{
          result = restTemplate.postForObject(keycloakTokenUri, request, String.class);
        }catch (RuntimeException exception){
            throw new RuntimeException(exception.getMessage());
        }
        if(Strings.isNullOrEmpty(result)){
            throw new RuntimeException("error Login fail!");
        }
        Token token = jwtUtils.getToken(result);
        return token;
    }

    @Override
    public Token refreshToken(String refreshToken) {
        log.info("call refreshToken with : {} " , refreshToken);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", clientId);
        map.add("grant_type", "refresh_token");
        map.add("client_secret" , clientSecret);
        map.add("refresh_token", refreshToken);
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        log.info("Send refreshToken request to SSO, refreshToken: {}" ,refreshToken);
        String result = restTemplate.postForObject(keycloakTokenUri, request, String.class);
        Token token = jwtUtils.getToken(result);
        return token;
    }

    @Override
    public Token adminLogin() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        log.debug("Admin Login Start. ");
        String credentials = "admin-cli" + ":" + "17XltshmI3NS7oszVzYKigchmUBJcojU";
        String auth = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(adminRealms))
                .POST(HttpRequest.BodyPublishers.ofString("username=admin&password=admin&grant_type=password"))
                .setHeader("content-type", "application/x-www-form-urlencoded")
                .setHeader("authorization", auth)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if(response.statusCode() == 200){
            log.info("Admin Login done. ");
            Token token = jwtUtils.getToken(response.body());
            return token;
        }
        log.warn("KeyClock error cannot add new user. Try again" +response.body());
        throw new RuntimeException("KeyClock error cannot add new user. Try again");
    }

    private List<GroupDto> selectedGroup(UserInformationForEdit userInformationForEdit){
        ArrayList<String> selectedGroup = userInformationForEdit.getUserInformation().getGroups();
        List<GroupDto> allGroups = userInformationForEdit.getAllGroups();
        List<GroupDto> result = new ArrayList<>();
        selectedGroup.forEach(grp -> {
            allGroups.forEach(grpItr -> {
                if(grp.equals(grpItr.getName())){
                    result.add(grpItr);
                }
            });
        });
        return result;
    }

    @Override
    public void joinUserToGroup(UserInformationForEdit informationForEdit) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        String userId = informationForEdit.getUserInformation().getId();
        List<GroupDto> groups = selectedGroup(informationForEdit);
        groups.stream().forEach(group->{
            HttpRequest request = null;
            try {
                request = HttpRequest.newBuilder()
                        .uri(URI.create(allThisApplicationUsers +"/"+ userId + "/groups/" + group.getId()))
                        .PUT(HttpRequest.BodyPublishers.ofString(""))
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Authorization", "bearer " + adminLogin().getAccess_token())
                        .build();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            HttpResponse<String> response = null;
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(response.statusCode() != 204){
                log.warn("Cannot edit new user because " +response.body());
                throw new RuntimeException("Cannot edit new user because "+response.body());
            }
        });

    }


    @Override
    public void editUser(UserInformationForEdit informationForEdit) throws IOException, InterruptedException {
        UserInformation userInformation = informationForEdit.getUserInformation();
        log.debug("Edit user for this username : {} " , userInformation.getUsername());
        Token adminToken = adminLogin();
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(userInformation);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(allThisApplicationUsers+"/"+userInformation.getId()))
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .setHeader("Content-Type", "application/json")
                .setHeader("Authorization", "Bearer " + adminToken.getAccess_token())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if(response.statusCode() != 204){
            log.warn("Cannot edit new user because " +response.body());
            throw new RuntimeException("Cannot edit new user because "+response.body());
        }
        log.info("edit user done. and " + response.body() );
    }

    @Override
    public  void leaveGroup(UserInformationForEdit informationForEdit) throws IOException, InterruptedException {
        log.debug("username {} leave Group  {}  " , informationForEdit.getUserInformation().getUsername() , informationForEdit.getSelectedGroup());
        informationForEdit.getAllGroups().forEach(grp ->{


            HttpClient client = HttpClient.newHttpClient();
            Token adminToken = null;
            try {
                adminToken = adminLogin();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(allThisApplicationUsers+"/"+informationForEdit.getUserInformation().getId()+"/groups/"+grp.getId()))
                    .DELETE()
                    .setHeader("Authorization", "Bearer " + adminToken.getAccess_token())
                    .build();
            HttpResponse<String> response = null;
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(response.statusCode() != 204){
                log.warn("Show groups error" +response.body());
                throw new RuntimeException("Show groups error because "+response.body());
            }

        });

    }


    @Override
    public void addUser(UserInformation userInformation) throws IOException, InterruptedException {
        log.debug("Add user for this username : {} " , userInformation.getUsername());
        Token adminToken = adminLogin();
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(userInformation);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(allThisApplicationUsers))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .setHeader("Content-Type", "application/json")
                .setHeader("Authorization", "Bearer " + adminToken.getAccess_token())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if(response.statusCode() != 201){
            log.warn("Cannot add new user because " +response.body());
            throw new RuntimeException("Cannot add new user because "+response.body());
        }
        log.info("add user done. and " + response.body() );
    }



    @Override
    public List<UserInformation> showUsers() throws IOException, InterruptedException {
        log.debug("showUsers call." );
        HttpClient client = HttpClient.newHttpClient();
        Token adminToken = adminLogin();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(allThisApplicationUsers))
                .GET()
                .setHeader("Authorization", "Bearer " + adminToken.getAccess_token())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<UserInformation> result = new ConvertJsonArrayToList().convertJsonArrayUsingGsonLibrary(response.body());
        if(response.statusCode() != 200){
            log.warn("Show user error" +response.body());
            throw new RuntimeException("Show user error because "+response.body());
        }
        // Map<String , UserInformation> usersMap= result.stream().collect(Collectors.toMap(UserInformation::getUsername, Function.identity()));
        return result;
    }

    @Override
    public void resetPassword() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/admin/realms/testrealm/users/d0375203-631f-4c32-8fad-bc4482be3c29/reset-password"))
                .PUT(HttpRequest.BodyPublishers.ofString("{ \"type\": \"password\", \"temporary\": false, \"value\": \"my-new-password\" }"))
                .setHeader("Content-Type", "application/json")
                .setHeader("Authorization", "bearer " + System.getenv("access_token"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Override
    public void deleteUser() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/admin/realms/testrealm/users/d0375203-631f-4c32-8fad-bc4482be3c29"))
                .DELETE()
                .setHeader("Content-Type", "application/json")
                .setHeader("Authorization", "bearer " + System.getenv("access_token"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


    }

    @Override
    public List<GroupDto> getGroups() throws IOException, InterruptedException {
        log.debug("getGroups call." );
        HttpClient client = HttpClient.newHttpClient();
        Token adminToken = adminLogin();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(allThisApplicationRoleGroup))
                .GET()
                .setHeader("Authorization", "Bearer " + adminToken.getAccess_token())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<GroupDto> result = new ConvertJsonArrayToList().convertJsonArrayToGroup(response.body());
        if(response.statusCode() != 200){
            log.warn("Show groups error" +response.body());
            throw new RuntimeException("Show groups error because "+response.body());
        }
        // Map<String , UserInformation> usersMap= result.stream().collect(Collectors.toMap(UserInformation::getUsername, Function.identity()));
        return result;
    }
}
