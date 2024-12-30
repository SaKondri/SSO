package com.infrastructure.sso.dto.req;

import com.infrastructure.sso.dto.GroupDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author SaKondri
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupActivityDto {

    private List<GroupDto> groups;
    private List<RealmRoles> roles;
}
