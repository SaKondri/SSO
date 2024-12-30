package com.infrastructure.sso.dto.resp;

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
public class ActivityTreeDto {

    private  String id;
    private String name;
    private String value;
    private String description;
    private boolean composite;
    private boolean clientRole;
    private String containerId;

    private List<ActivityTreeDto> children;
}
