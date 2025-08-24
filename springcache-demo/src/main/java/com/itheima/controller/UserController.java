package com.itheima.controller;

import com.itheima.entity.User;
import com.itheima.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserMapper userMapper;

    /**
     * 新增用户
     * @param user
     * @return
     */
    @PostMapping
    @CachePut(cacheNames = "userCache",key = "#user.id") //如果使用Spring Cache缓存数据，key的生成：userCache::{{user.id}}
    public User save(@RequestBody User user){
        userMapper.insert(user);
        return user;
    }

    /**
     * 根据id查询用户
     * @param id
     * @return
     */
    @GetMapping
    @Cacheable(cacheNames = "userCache",key = "#id") //key的生成：userCache::1 -> userCache::{{id}}
    public User getById(Long id){
        User user = userMapper.getById(id);
        return user;
    }

    /**
     * 根据id删除用户
     * @param id
     */
    @CacheEvict(cacheNames = "userCache",key = "#id") //key的生成：userCache::1 -> userCache::{{id}}
    @DeleteMapping
    public void deleteById(Long id){
        userMapper.deleteById(id);
    }

    /**
     * 删除所有用户
     */
    @CacheEvict(cacheNames = "userCache",allEntries = true)
	@DeleteMapping("/delAll")
    public void deleteAll(){
        userMapper.deleteAll();
    }

}
