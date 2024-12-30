package com.infrastructure.sso.repository.cache;

import com.infrastructure.sso.dto.Token;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author SaKondri
 */
@Repository
public interface TokenRepository extends CrudRepository<Token, String> {

         Token findByUsername(String username);

         Token findByRefreshToken(String Refresh_token);
         String deleteByUsername(String username);

}