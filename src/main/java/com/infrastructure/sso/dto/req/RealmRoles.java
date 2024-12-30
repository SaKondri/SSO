package com.infrastructure.sso.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author SaKondri
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class RealmRoles {

    private String id;
    private String name;
    private String description;
    private boolean composite;
    private boolean clientRole;
    private String containerId;

    public void setName(String name) {
        this.name = name.replace("activity." , "");
    }
}
