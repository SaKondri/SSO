package com.infrastructure.sso.services.cache;

import com.infrastructure.sso.dto.GroupDto;
import com.infrastructure.sso.dto.req.user.UserInformation;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * @author SaKondri
 */
@Component
public class UserCacheService {


    private String keyName = "allUser";
    @Bean
    RedisTemplate<String, List<GroupDto>> userCache(RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, List<GroupDto>> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        //redisTemplate.expireAt("aKey",Date.from(LocalDateTime.now().with(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant()));

        return template;
    }

    @Bean
    RedisTemplate<String, List<GroupDto>> usersCache(RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, List<GroupDto>> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        //redisTemplate.expireAt("aKey",Date.from(LocalDateTime.now().with(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant()));

        return template;
    }

    @Resource(name = "usersCache")

    private ListOperations<String, List<UserInformation>> users;

    public void addAllUser(List<UserInformation> groupDtoList) {
        users.leftPush(keyName, groupDtoList);
        users.getOperations().expireAt("allUser", new Date(new Date().getTime()+100000));
    }

    public List<UserInformation> getAllUser() {
        try {
            return users.range(keyName , 0 , 0).get(0);
        }catch (RuntimeException er){
            return null;
        }
    }

    public void removeAll(){
        users.trim(keyName , 0 ,0);
        users.leftPop(keyName);
    }

}
