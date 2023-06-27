package com.practice.service;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class QRServiceTest {

    @Test
    void generateFile() throws IOException {
        //1.设置velocity资源加载器
        Properties properties = new Properties();
        //固定写法
        properties.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        //2.初始化velocity引擎
        Velocity.init(properties);
        //3.创建velocity容器
        VelocityContext context = new VelocityContext();
        context.put("name", "lisi");
        //4.加载velocity模板文件
        Template template = Velocity.getTemplate("vms/niki.vm", "UTF-8");
        //5.合并数据到模板
        FileWriter fw = new FileWriter("D:\\projectdemo\\java8\\src\\main\\resources\\html\\01-quickstart.html");
        template.merge(context, fw);
        //6.释放资源
        fw.close();
    }

    @Test
    void generateStream() {
    }
}