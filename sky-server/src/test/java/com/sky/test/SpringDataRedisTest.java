package com.sky.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class SpringDataRedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testRedis() {
        System.out.println(redisTemplate);
        ValueOperations valueOperations = redisTemplate.opsForValue();
        HashOperations hashOperations = redisTemplate.opsForHash();
        ListOperations listOperations = redisTemplate.opsForList();
        SetOperations setOperations = redisTemplate.opsForSet();
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
    }

    /**
     * 测试redis字符串操作
     */
    @Test
    public void testString(){
        // set get setex setnx
        redisTemplate.opsForValue().set("name","张三");
        String name = (String) redisTemplate.opsForValue().get("name");
        System.out.println(name);

        redisTemplate.opsForValue().set("code", "1234", 3, TimeUnit.MINUTES);
        redisTemplate.opsForValue().setIfAbsent("lock", "1");
        redisTemplate.opsForValue().setIfAbsent("lock", "2");
    }

    /**
     * 测试Redis Hash操作
     */
    @Test
    public void testHash() {
        // hset hget hdel hkeys hvals
        redisTemplate.opsForHash().put("user:1", "name", "张三");
        redisTemplate.opsForHash().put("user:1", "age", "20");

        String name = (String)redisTemplate.opsForHash().get("user:1", "name");
        System.out.println(name);

        Set keys = redisTemplate.opsForHash().keys("user:1");
        System.out.println(keys);

        List values = redisTemplate.opsForHash().values("user:1");
        System.out.println(values);

        redisTemplate.opsForHash().delete("user:1", "age");
    }
}
