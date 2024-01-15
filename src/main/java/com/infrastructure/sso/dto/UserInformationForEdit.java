package com.infrastructure.sso.dto;

import com.infrastructure.sso.dto.req.user.UserInformation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author SaKondri
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInformationForEdit {

    private UserInformation userInformation;
    private List<GroupDto> allGroups;
    private Map<String , GroupDto> selectedGroup;
}
