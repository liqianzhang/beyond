package com.sx.demo.mvc;

import com.mvcframwork.annotation.SXAutoWired;
import com.mvcframwork.annotation.SXController;
import com.mvcframwork.annotation.SXRequestMapping;
import com.mvcframwork.annotation.SXRequestParam;
import com.sx.demo.IDemoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @MethodName: $
 * @Description: TODO
 * @Param: $
 * @Return: $
 * @Author: zhangliqian
 * @Date: $
 */
@SXController
@SXRequestMapping("/demo")
public class DemoAction {
    @SXAutoWired
    private IDemoService demoService;

    @SXRequestMapping("/query")
    public void query(HttpServletRequest request, HttpServletResponse response, @SXRequestParam("name") String name) {
        String result = demoService.get(name);
        try {
            response.getWriter().write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SXRequestMapping("/add")
    public void add(HttpServletRequest request, HttpServletResponse response, @SXRequestParam("a") Integer a, @SXRequestParam("b") Integer b) {
        try {
            response.getWriter().write(a + "+" + b + "=:" + (a + b));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/remove")
    public void remove(HttpServletRequest request, HttpServletResponse response, @RequestParam("id") Integer id) {

    }

}
