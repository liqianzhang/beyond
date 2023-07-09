package com.sx.demo.impl;

import com.mvcframwork.annotation.SXService;
import com.sx.demo.IDemoService;

/**
 * @MethodName: $
 * @Description: TODO
 * @Param: $
 * @Return: $
 * @Author: zhangliqian
 * @Date: $
 */
@SXService
public class DemoService implements IDemoService {
    @Override
    public String get(String name) {
        return "my girl is :" + name;
    }
}
