package com.practice.service.impl;

import com.practice.dao.NikiDao;
import com.practice.service.NikiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @MethodName: $
 * @Description: TODO
 * @Param: $
 * @Return: $
 * @Author: zhangliqian
 * @Date: $
 */
@Service
public class NikiServiceImpl implements NikiService {
    @Autowired
    NikiDao nikiDao;
    public String getNiki() {
        String niki = nikiDao.queryNiki();
        return niki;
    }
}
