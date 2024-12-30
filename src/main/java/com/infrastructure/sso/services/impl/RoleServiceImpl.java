package com.infrastructure.sso.services.impl;

import com.infrastructure.sso.dto.GroupDto;
import com.infrastructure.sso.dto.req.user.KeyCloakRoleDto;
import com.infrastructure.sso.dto.resp.UserInformationForGroupReq;
import com.infrastructure.sso.services.cache.GroupCacheService;
import com.infrastructure.sso.services.interfaces.RoleService;
import com.infrastructure.sso.services.interfaces.SSO_Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author SaKondri
 */

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final GroupCacheService groupCacheService;

    @Override
    public List<GroupDto> getCurrentUserRole(String userId , SSO_Service sso_service) throws IOException, InterruptedException {
        List<GroupDto> result = groupCacheService.getGroupsById(userId);
        if(result == null || result.size() == 0){
            List<GroupDto> groups = new ArrayList<>();
            List<GroupDto> groupDtos = sso_service.getGroups();
            groupDtos.forEach(groupDto -> {
                List<UserInformationForGroupReq> userGroups = sso_service.getGroupsWithMember(groupDto.getId());
                userGroups.forEach(users ->{
                    if(users.getId().equals(userId)){
                        groups.add(groupDto);
                    }
                } );
            });
            groupCacheService.addGroup(userId,groups);
            return groups;
        }else {
           return result;
        }

    }
}
