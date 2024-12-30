package com.infrastructure.sso.utils;

/**
 * @author SaKondri
 */
import java.lang.reflect.Type;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.infrastructure.sso.dto.GroupDto;
import com.infrastructure.sso.dto.RealmMappings;
import com.infrastructure.sso.dto.UserInformationForEdit;
import com.infrastructure.sso.dto.req.RealmRoles;
import com.infrastructure.sso.dto.req.user.UserInformation;
import com.infrastructure.sso.dto.resp.UserInformationForGroupReq;
import org.json.JSONObject;


public class ConvertJsonArrayToList {



    public  List<UserInformation> convertJsonArrayUsingGsonLibrary(String jsonArray) {

        Gson gson = new Gson();

        Type listType = new TypeToken<List<UserInformation>>() {}.getType();

        List<UserInformation> gsonList = gson.fromJson(jsonArray, listType);
        return gsonList;
    }

    public  List<GroupDto> convertJsonArrayToGroup(String jsonArray) {

        Gson gson = new Gson();

        Type listType = new TypeToken<List<GroupDto>>() {}.getType();

        List<GroupDto> gsonList = gson.fromJson(jsonArray, listType);
        return gsonList;
    }


    public  List<RealmRoles> convertJsonArrayToRealmRoles(String jsonArray) {

        Gson gson = new Gson();

        Type listType = new TypeToken<List<RealmRoles>>() {}.getType();

        List<RealmRoles> gsonList = gson.fromJson(jsonArray, listType);
        return gsonList;
    }



    public RealmMappings convertJsonToRealmMappings(String jsonArray) {

        Gson gson = new Gson();

        Type listType = new TypeToken<RealmMappings>() {}.getType();

        RealmMappings gsonList = gson.fromJson(jsonArray, listType);
        return gsonList;
    }


    public UserInformationForGroupReq getUserInformationForGroupReq(JSONObject jsonObject){
        String id =jsonObject.get("id").toString();
        String lastName = Strings.isNullOrEmpty(jsonObject.get("lastName").toString()) ? jsonObject.get("lastName").toString() : null;
        String firstName =jsonObject.get("firstName").toString();
        String email = jsonObject.get("email").toString();
        String username = jsonObject.get("username").toString();
        boolean enabled = (boolean) jsonObject.get("enabled");
       return UserInformationForGroupReq.builder()
                .id(id)
                .lastName(lastName)
                .firstName(firstName)
                .enabled(enabled)
                .username(username)
                .email(email)
                .build();
    }

}