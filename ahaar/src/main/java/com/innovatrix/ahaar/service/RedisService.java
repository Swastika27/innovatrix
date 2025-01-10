package com.innovatrix.ahaar.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    public static final String REDIS_PREFIX = "USER:";

    @Autowired
    private RedisTemplate redisTemplate;

    public<T> T get(String key, Class<T> entityClass){
        Object o=redisTemplate.opsForValue().get(key);
        ObjectMapper mapper =new ObjectMapper();
        if(o==null){
            return null;
        }
        try {
            assert o != null;
            return mapper.readValue(o.toString(),entityClass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void set(String key, Object o,long ttl){
        try {
            ObjectMapper objectMapper=new ObjectMapper();
            String jsonValue=objectMapper.writeValueAsString(o);
            redisTemplate.opsForValue().set(key,jsonValue,ttl, TimeUnit.HOURS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
