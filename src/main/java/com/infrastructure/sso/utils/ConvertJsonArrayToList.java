package com.infrastructure.sso.utils;

/**
 * @author SaKondri
 */
import java.lang.reflect.Type;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.infrastructure.sso.dto.GroupDto;
import com.infrastructure.sso.dto.req.user.UserInformation;


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

}