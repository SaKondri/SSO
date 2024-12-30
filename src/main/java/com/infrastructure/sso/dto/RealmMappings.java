package com.infrastructure.sso.dto;

import com.infrastructure.sso.dto.req.RealmRoles;
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
public class RealmMappings {

    private List<RealmRoles> realmMappings;

}
