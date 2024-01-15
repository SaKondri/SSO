package com.infrastructure.sso.dto.req.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author SaKondri
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public  class Credential{
    private String type;
    private String value;
    private boolean temporary;

}