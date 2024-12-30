package com.infrastructure.sso.controller;

import com.google.common.collect.Maps;
import com.infrastructure.sso.dto.GroupDto;
import com.infrastructure.sso.dto.req.GroupActivityDto;
import com.infrastructure.sso.dto.req.RealmRoles;
import com.infrastructure.sso.dto.resp.ActivityTreeDto;
import com.infrastructure.sso.services.interfaces.SSO_Service;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author SaKondri
 */

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/activity")
@RequiredArgsConstructor
public class ActivityController {


    private final SSO_Service sso_service;


    @PostMapping("/saveRoles")
    @PreAuthorize("hasAnyAuthority('createUser')")
    public void saveRoles(@RequestBody GroupActivityDto groupDto){
        groupDto.getGroups().forEach(group -> {
            try {
                List<RealmRoles> allRoles =  sso_service.getRealmRoles();
                List<RealmRoles> selectedRoles = new ArrayList<>();
                Map<String,RealmRoles> mapRoles = Maps.uniqueIndex(allRoles,RealmRoles::getId);
                    groupDto.getRoles().forEach(selectedRole ->{
                        if(mapRoles.get(selectedRole.getId()) != null){
                            selectedRoles.add(mapRoles.get(selectedRole.getId()));
                        }
                    });
                // todo remove Default roles be careful
                sso_service.removeRoleMapping(group , allRoles);
                sso_service.addRoleMapping(group , selectedRoles);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @PostMapping("/getActivityByRole")
    public Set<RealmRoles> getActivityByRole(@RequestBody List<GroupDto> groupDto) throws IOException, InterruptedException {
        Set<RealmRoles> result =new HashSet<>();
        groupDto.forEach(group -> {
            try {
                List<RealmRoles> selectedRoleByGroupList = sso_service.getActivityByRoleId(group.getId());
                if(selectedRoleByGroupList != null){
                    result.addAll(selectedRoleByGroupList);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });


        return result;
    }
    @GetMapping("/getTree")
    public List<ActivityTreeDto> getTree() throws IOException, InterruptedException {
        List<RealmRoles> allRoles = sso_service.getRealmRoles();
        Map<String,RealmRoles> allRolesMap = Maps.uniqueIndex(allRoles,RealmRoles::getId);
        List<RealmRoles> listOfResultRoles  = new ArrayList<RealmRoles>(allRolesMap.values());
        List<RealmRoles> filterDefaultRoles = listOfResultRoles.stream().filter(realmRoles -> !realmRoles.getDescription().startsWith("${")).collect(Collectors.toList());
        List<ActivityTreeDto> result = filterDefaultRoles.stream().map(role ->
                        ActivityTreeDto.builder().id(role.getId()).containerId(role.getContainerId()).description(role.getDescription()).name(role.getName()).value(role.getName()).children(new ArrayList<>()).build())
                .collect(Collectors.toList());


        Map<String , List<ActivityTreeDto>> groupByRole = groupByRole(result);
        List<ActivityTreeDto> groupTree = mapTolist(groupByRole);
        return groupTree;
    }
    private Map<String , List<ActivityTreeDto>> groupByRole(List<ActivityTreeDto> tree){
        Map<String , List<ActivityTreeDto>> result = tree.stream().collect(Collectors.groupingBy(role -> role.getDescription() ));
        return result;
    }

    private List<ActivityTreeDto> mapTolist(Map<String , List<ActivityTreeDto>> map){
        List<ActivityTreeDto> result =new ArrayList<>();
        map.forEach((key, value) -> {
            ActivityTreeDto dto = ActivityTreeDto.builder().name(key).value(key).children(value).id(key).build();
            result.add(dto);
        });
        return result;
    }
}
