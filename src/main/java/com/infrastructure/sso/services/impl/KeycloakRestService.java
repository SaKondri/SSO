package com.infrastructure.sso.services.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.infrastructure.sso.dto.GroupDto;
import com.infrastructure.sso.dto.RealmMappings;
import com.infrastructure.sso.dto.Token;
import com.infrastructure.sso.dto.UserInformationForEdit;
import com.infrastructure.sso.dto.req.RealmRoles;
import com.infrastructure.sso.dto.req.UsernamePasswordDto;
import com.infrastructure.sso.dto.req.user.UserInformation;
import com.infrastructure.sso.dto.resp.UserInformationForGroupReq;
import com.infrastructure.sso.security.jwt.JwtUtils;
import com.infrastructure.sso.services.cache.GroupCacheService;
import com.infrastructure.sso.services.cache.UserCacheService;
import com.infrastructure.sso.services.interfaces.SSO_Service;
import com.infrastructure.sso.utils.ConvertJsonArrayToList;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
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


    private final GroupCacheService groupCacheService;

    private final RestTemplate restTemplate;

    private final JwtUtils jwtUtils;

    private final UserCacheService userCacheService;

    @Value("${realmRoles}")
    private String realmRolesUrl;

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
    //@CircuitBreaker(name = "sso")
    //@TimeLimiter(name = "sso")
   // @Retry(name = "sso")
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
        String credentials = "admin-cli" + ":" + "vkg69S1w4OYLNCT8eh96xZ3ERymJJKgq";
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
        log.warn("keycloak error cannot admin login. Try again" +response.body());
        throw new RuntimeException("keycloak error cannot add new user. Try again");
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
        //List<UserInformation> users = userCacheService.getAllUser();
        List<UserInformation> users = null;
           if(users == null){
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
            userCacheService.addAllUser(result);
            return result;
        }else {
            return users;
        }

    }

    @Override
    public String resetPassword(UsernamePasswordDto usernamePasswordDto) throws IOException, InterruptedException {
       List<UserInformation> users =  showUsers();
        UserInformation result = users.stream().filter(user -> usernamePasswordDto.getUsername().equals(user.getUsername())).findAny().orElse(null);
        HttpClient client = HttpClient.newHttpClient();

        String body = """
                { "type": "password", "temporary": false, "value":" """;
        body = body + usernamePasswordDto.getPassword()+ "\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(allThisApplicationUsers+"/"+result.getId()+"/reset-password"))
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .setHeader("Content-Type", "application/json")
                .setHeader("Authorization", "bearer " + adminLogin().getAccess_token())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
       return response.body();
    }

    @Override
    public void deleteUser(UserInformation userInformation) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(allThisApplicationUsers+"/"+userInformation.getId()))
                .DELETE()
                .setHeader("Content-Type", "application/json")
                .setHeader("Authorization", "bearer " + adminLogin().getAccess_token())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

       log.debug(userInformation.getId()+" for delete and response from server: "+response.body());
    }

    @Override
    public List<GroupDto> getGroups() throws IOException, InterruptedException {
        log.debug("getGroups call." );
        List<GroupDto> resultCache = groupCacheService.getAllGroupsById();
        //List<GroupDto> resultCache =null;
        if(resultCache == null){
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
            groupCacheService.addAllGroup(result);
                return result;
        }
       return resultCache;
    }

    @Override
    public List<UserInformationForGroupReq> getGroupsWithMember(String groupId)  {
        //List<UserInformationForGroupReq> cache= (List<UserInformationForGroupReq>) userInformationForGroupReqRepository.findAllById(Arrays.asList(groupId));
        log.debug("getGroups call.");
        List<UserInformationForGroupReq> result = new ArrayList<>();
        HttpClient client = HttpClient.newHttpClient();
        Token adminToken = null;
        try {
            adminToken = adminLogin();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(allThisApplicationRoleGroup + "/" + groupId + "/members"))
                .GET()
                .setHeader("Authorization", "Bearer " + adminToken.getAccess_token())
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        JSONArray ja = new JSONArray(response.body());
        ConvertJsonArrayToList utils = new ConvertJsonArrayToList();
        ja.forEach(user -> {
            result.add(utils.getUserInformationForGroupReq((JSONObject) user));
        });
        //userInformationForGroupReqRepository.save(result);
       return result;
    }

    @Override
    public List<RealmRoles> getRealmRoles() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        Token adminToken = adminLogin();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(realmRolesUrl))
                .GET()
                .setHeader("Authorization", "Bearer " + adminToken.getAccess_token())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<RealmRoles> result = new ConvertJsonArrayToList().convertJsonArrayToRealmRoles(response.body());
        if(response.statusCode() != 200){
            log.warn("Show groups error" +response.body());
            throw new RuntimeException("Show groups error because "+response.body());
        }
        return result;
    }


    @Override
    public List<RealmRoles> getActivityByRoleId(String groupId) throws IOException, InterruptedException {
        //todo http://localhost:8080/admin/realms/webApplication/groups/bc40fa33-7adb-4339-a420-7c105c2a295f/role-mappings do get Roles from each group ex : Admin("createUser" , "DeleteGeneralParameter")
        HttpClient client = HttpClient.newHttpClient();
        Token adminToken = adminLogin();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(allThisApplicationRoleGroup+"/"+groupId+"/"+"role-mappings"))
                .GET()
                .setHeader("Authorization", "Bearer " + adminToken.getAccess_token())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        RealmMappings result = new ConvertJsonArrayToList().convertJsonToRealmMappings(response.body());
        if(response.statusCode() != 200){
            log.warn("Show groups error" +response.body());
            throw new RuntimeException("Show groups error because "+response.body());
        }
        return result.getRealmMappings();
    }

    @Override
    public void addRoleMapping(GroupDto groupDto, List<RealmRoles> roles) throws IOException, InterruptedException {
        //http://localhost:5440/auth/admin/realms/{yourRealm}/groups/8129e7ed-db5f-423b-91f5-779b9d448d3b/role-mappings/realm
        HttpClient client = HttpClient.newHttpClient();
        Token adminToken = adminLogin();
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(roles);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(allThisApplicationRoleGroup+"/"+groupDto.getId()+"/role-mappings/realm"))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .setHeader("Authorization", "Bearer " + adminToken.getAccess_token())
                .setHeader("Content-Type" , "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if(response.statusCode() != 204){
            log.warn("Add roles error." +response.body());
            throw new RuntimeException("Add roles error because "+response.body());
        }
    }

    @Override
    public void removeRoleMapping(GroupDto groupDto, List<RealmRoles> roles) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        Token adminToken = adminLogin();
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(roles);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(allThisApplicationRoleGroup+"/"+groupDto.getId()+"/role-mappings/realm"))
                .method("DELETE" , HttpRequest.BodyPublishers.ofString(body))
                .setHeader("Authorization", "Bearer " + adminToken.getAccess_token())
                .setHeader("Content-Type" , "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if(response.statusCode() != 204){
            log.warn("Remove roles error." +response.body());
            throw new RuntimeException("Remove roles error because "+response.body());
        }
    }

}
