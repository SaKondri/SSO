package com.infrastructure.sso.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Iterator;

/**
 * @author SaKondri
 */
public class RedisConfig {

//
//    @Value("${spring.data.redis.host}")
//    private String redisHost;
//
//    @Value("${spring.data.redis.port}")
//    private int redisPort;

    public RedisConfig() {
    }

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
       // RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(redisHost, redisPort);
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();

//        redisSentinelConfiguration.master(this.redisProperties.getSentinel().getMaster());
//        redisSentinelConfiguration.setPassword(this.redisProperties.getPassword());
//        Iterator var2 = this.redisProperties.getSentinel().getNodes().iterator();
//
//        while(var2.hasNext()) {
//            String node = (String)var2.next();
//            String[] props = node.split(":");
//            redisSentinelConfiguration.sentinel(props[0], Integer.parseInt(props[1]));
//        }

        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(redisStandaloneConfiguration);
        //jedisConnectionFactory.setDatabase(this.redisProperties.getDatabase());

        //jedisConnectionFactory.setDatabase(2);
        return jedisConnectionFactory;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate() {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(this.jedisConnectionFactory());
        return stringRedisTemplate;
    }
}
