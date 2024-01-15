package com.infrastructure.sso.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

/**
 * @author SaKondri
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupDto{
    public String id;
    public String name;
    public String path;
    public ArrayList<GroupDto> subGroups;
}
