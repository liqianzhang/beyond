package com.practice.controller;

import com.practice.entity.User;
import com.practice.mapper.UserMapper;
import com.practice.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @MethodName: $
 * @Description: TODO
 * @Param: $
 * @Return: $
 * @Author: zhangliqian
 * @Date: $
 */
@RestController
@Slf4j
public class FunctionIntegrationTestController {
    @Autowired
    UserMapper userMapper;

    @PostMapping("redis/test1")
    public String test1(@RequestParam("key") String key, @RequestParam("value") String value, @RequestParam("type") String type) {
        RedisUtil.set(key, value);
        String val = RedisUtil.get(key).toString();
        log.info("==> val={}", val);

        return null;
    }

    @PostMapping("niki/select")
    public List testMybatis() {
        List<User> users = userMapper.selectAll();
        return users;
    }
}
