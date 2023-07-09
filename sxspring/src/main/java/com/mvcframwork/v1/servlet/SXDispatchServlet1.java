package com.mvcframwork.v1.servlet;

import com.mvcframwork.annotation.SXAutoWired;
import com.mvcframwork.annotation.SXController;
import com.mvcframwork.annotation.SXRequestMapping;
import com.mvcframwork.annotation.SXService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @MethodName: $
 * @Description: TODO
 * @Param: $
 * @Return: $
 * @Author: zhangliqian
 * @Date: $
 */
public class SXDispatchServlet1 extends HttpServlet {

    private Map<String, Object> mapping = new HashMap<String, Object>();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws IOException, InvocationTargetException, IllegalAccessException {
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath, "").replaceAll("/+", "/");
        if (!this.mapping.containsKey(url)) {
            resp.getWriter().write("404 not found");
            return;
        }
        Method method = (Method) this.mapping.get(url);
        Map<String, String[]> params = req.getParameterMap();
        method.invoke(this.mapping.get(method.getDeclaringClass().getName()), new Object[]{req, resp, params.get("name")[0]});
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            resp.getWriter().write("500 exception" + Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        InputStream is = null;
        Properties configContext = new Properties();
        is = this.getClass().getClassLoader().getResourceAsStream(config.getInitParameter("contextConfigLocation"));
        try {
            configContext.load(is);
            String scanPackage = configContext.getProperty("scanPackage");
            doScanner(scanPackage);
            for (String className : mapping.keySet()) {
                if (!className.contains(".")) {
                    continue;
                }
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(SXController.class)) {
                    mapping.put(className, clazz.newInstance());
                    String baseUrl = "";
                    if (clazz.isAnnotationPresent(SXRequestMapping.class)) {
                        SXRequestMapping requestMapping = clazz.getAnnotation(SXRequestMapping.class);
                        baseUrl = requestMapping.value();
                    }
                    Method[] methods = clazz.getMethods();
                    for (Method method : methods) {
                        if (!method.isAnnotationPresent(SXRequestMapping.class)) {
                            continue;
                        }
                        SXRequestMapping requestMapping = method.getAnnotation(SXRequestMapping.class);
                        String url = (baseUrl + "." + requestMapping.value()).replaceAll("\\.", "/");
                        mapping.put(url, method);
                        System.out.println("mapped " + url + "," + method);
                    }
                } else if (clazz.isAnnotationPresent(SXService.class)) {
                    SXService service = clazz.getAnnotation(SXService.class);
                    String beanName = service.value();
                    if ("".equals(beanName)) {
                        beanName = clazz.getName();
                    }
                    Object instance = clazz.newInstance();
                    mapping.put(beanName, instance);
                    for (Class<?> i : clazz.getInterfaces()) {
                        mapping.put(i.getName(), instance);
                    }
                } else {
                    continue;
                }
            }

            for (Object object : mapping.values()) {
                if (object == null){continue;}
                Class clazz = object.getClass();
                if (clazz.isAnnotationPresent(SXController.class)) {
                    Field[] fields = clazz.getDeclaredFields();
                    for (Field field : fields) {
                        if (!clazz.isAnnotationPresent(SXAutoWired.class)){continue;}
                        SXAutoWired autoWired = field.getAnnotation(SXAutoWired.class);
                        String beanName = autoWired.value();
                        if ("".equals(beanName)) {
                            beanName = field.getType().getName();
                        }
                        field.setAccessible(true);
                        field.set(mapping.get(clazz.getName()), mapping.get(beanName));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("SX MVC Framework is init");
    }

    private void doScanner(String scanPackage) {
        URL url = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.", "/"));
        File classDir = new File(url.getFile());
        for (File file : classDir.listFiles()) {
            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
            } else {
                if (!file.getName().endsWith(".class")) {
                    continue;
                }
                String clazzName = (scanPackage + "." + file.getName()).replace(".class", "");
                mapping.put(clazzName, null);
            }
        }
    }
}
