package com.infrastructure.sso.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

/**
 * @author SaKondri
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash("UserInformationForGroupReq")
public class UserInformationForGroupReq {
    @Id
    @Indexed
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private boolean enabled;


}
