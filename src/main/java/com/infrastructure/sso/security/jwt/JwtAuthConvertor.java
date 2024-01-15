package com.infrastructure.sso.security.jwt;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;


import io.jsonwebtoken.Jwt;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author SaKondri
 */
public class JwtAuthConvertor implements Converter<Object, Collection<GrantedAuthority>>{


        @Override
        public Collection<GrantedAuthority> convert(Object jwt) {

            System.out.println(jwt);
            return null;
        }

        @Override
    public JavaType getInputType(TypeFactory typeFactory) {
        return null;
    }

    @Override
    public JavaType getOutputType(TypeFactory typeFactory) {
        return null;
    }
}
