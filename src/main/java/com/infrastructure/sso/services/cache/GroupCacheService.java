package com.infrastructure.sso.services.cache;

import com.infrastructure.sso.dto.GroupDto;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author SaKondri
 */
@Component
public class GroupCacheService {

    private String keyName = "AllGroup";

    @Bean
    RedisTemplate<String, List<GroupDto>> groupCache(RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, List<GroupDto>> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        //redisTemplate.expireAt("aKey",Date.from(LocalDateTime.now().with(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant()));

        return template;
    }


    @Bean
    RedisTemplate<String, List<GroupDto>> allGroupCache(RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, List<GroupDto>> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        //redisTemplate.expireAt("aKey",Date.from(LocalDateTime.now().with(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant()));

        return template;
    }

    @Resource(name = "groupCache")
    private ListOperations<String, List<GroupDto>> listOps;


    @Resource(name = "allGroupCache")
    private ListOperations<String, List<GroupDto>> allGroupCache;


    public void removeAllGroupCache(){
        allGroupCache.trim(keyName , 0 ,0);
        allGroupCache.leftPop(keyName);
    }


    public void addAllGroup(List<GroupDto> groupDtoList) {
        allGroupCache.leftPush(keyName, groupDtoList);
        allGroupCache.getOperations().expireAt(keyName, new Date(new Date().getTime()+100000));
    }


    public void addGroup(String userId, List<GroupDto> groupDtoList) {
        listOps.leftPush(userId, groupDtoList);
        listOps.getOperations().expireAt(userId, new Date(new Date().getTime()+60000));
    }


    public List<GroupDto> getAllGroupsById() {
        try {
            return allGroupCache.range("AllGroup" , 0 , 0).get(0);
        }catch (RuntimeException er){
            return null;
        }
    }

    public List<GroupDto> getGroupsById(String userId) {
        try {
           return listOps.range(userId , 0 , 0).get(0);
        }catch (RuntimeException er){
            return null;
        }
    }



    public void removeGroup(){
        listOps.trim(keyName , 0 ,0);
        listOps.leftPop(keyName);
    }

}
