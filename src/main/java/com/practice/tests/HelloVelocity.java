package com.practice.tests;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @MethodName: $
 * @Description: 个人觉得Velocity最好的使用场景，是在文件模板的生成方面，现在有很多场景需要打印报表，
 * 生成对应的文件，而Velocity便是一个过渡的比较好用的轻量的插件
 * @Param: $
 * @Return: $
 * @Author: zhangliqian
 * @Date: $
 */

/*
                #set( $iAmVariable = "good!" )
                Welcome $name to velocity.com
                today is $date.
                #foreach ($i in $list)
                $i
                #end
                $iAmVariable

            VelocityEngine.ri :    RuntimeInstance ri = new RuntimeInstance();
 */
public class HelloVelocity {
    public static void main(String[] args) {
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        velocityEngine.init();
        // 载入（获取）模板对象
        Template template = velocityEngine.getTemplate("vms/Hellovelocity.vm");
        VelocityContext ctx = new VelocityContext();
        // 域对象加入参数值
        ctx.put("name", "李智龙");
        ctx.put("date", (new Date()).toString());
        // list集合
        List temp = new ArrayList();
        temp.add("1");
        temp.add("2");
        ctx.put("list", temp);

        StringWriter stringWriter = new StringWriter();
        template.merge(ctx, stringWriter);

        System.out.println(stringWriter.toString());
    }

    public static void inputstreamtofile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}