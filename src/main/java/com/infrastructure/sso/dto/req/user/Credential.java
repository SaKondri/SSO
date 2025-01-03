package com.infrastructure.sso.dto.req.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author SaKondri
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public  class Credential implements Serializable {
    private String type;
    private String value;
    private boolean temporary;

}