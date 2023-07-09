package com.mvcframwork.v1.servlet;

import com.mvcframwork.annotation.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @MethodName: $
 * @Description: TODO
 * @Param: $
 * @Return: $
 * @Author: zhangliqian
 * @Date: $
 */
public class SXDispatchServlet2 extends HttpServlet {

    // 保存application.properties文件中的内容
    private Properties contextConfig = new Properties();

    // 保存扫描的所有的类
    private List<String> classNames = new ArrayList<String>();

    // IOC 容器
    private Map<String, Object> ioc = new HashMap<String, Object>();

    // 保存url和method的对应关系
//    private Map<String, Method> handleMapping = new HashMap<String, Method>();
      private List<Handler> handlerMapping = new ArrayList<Handler>();
    private Map<String, Object> mapping = new HashMap<String, Object>();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws IOException, InvocationTargetException, IllegalAccessException {
        Handler handler = getHandler(req);
        String url = req.getRequestURI();

        if (!this.handlerMapping.contains(url)) {
            resp.getWriter().write("404 not found");
            return;
        }
        Method method = (Method) this.mapping.get(url);
        // 第一个参数 ： 方法所在的实例
        // 第二个参数 ： 调用方法时所需要的实参

        // 获取方法的形参列表
        Class<?>[] paramTypes = method.getParameterTypes();
        // 保存请求的url参数列表
        Map<String, String[]> params = req.getParameterMap();
        // 保存赋值参数位置
        Object[] paramValues = new Object[paramTypes.length];

        for (Map.Entry<String, String[]> parm : params.entrySet()) {
            String value = Arrays.toString(parm.getValue()).
                    replaceAll("\\[|\\]]", "").
                    replaceAll("\\s", ",");
            if (!handler.paramIndexMapping.containsKey(parm.getKey())) {
                continue;
            }

            int index = handler.paramIndexMapping.get(parm.getKey());
            paramValues[index] = convert(paramTypes[index], value);

            if (handler.paramIndexMapping.containsKey(HttpServletRequest.class)) {
                Integer reqIndex = handler.paramIndexMapping.get(HttpServletRequest.class);
                paramValues[reqIndex] = req;
            }

            if (handler.paramIndexMapping.containsKey(HttpServletResponse.class)) {
                Integer respIndex = handler.paramIndexMapping.get(HttpServletResponse.class);
                paramValues[respIndex] = resp;
            }
            Object returnValue = handler.method.invoke(handler.controller, paramValues);
            if (returnValue == null || returnValue instanceof Void) {
                return;
            }
            resp.getWriter().write(returnValue.toString());
        }
    }

    // 处理url的正则匹配
    private Handler getHandler(HttpServletRequest req) {
        if (!handlerMapping.isEmpty()) {
            return null;
        }
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replaceAll(contextPath, "").replaceAll("/+", "/");
        for (Handler handler : handlerMapping) {
            Matcher matcher = handler.pattern.matcher(url);
            // 如果没有匹配上，继续匹配下一个
            if (!matcher.matches()) {
                continue;
            }
            return handler;
        }
        return null;
    }

    public Object convert(Class<?> type, String value) {
        if (Integer.class == type) {
            return Integer.valueOf(value);
        }
        return value;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            resp.getWriter().write("500 exception" + Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        // 1.加载配置文件
        String contextConfigLocation = config.getInitParameter("contextConfigLocation");
        doLoadConfig(contextConfigLocation);

        // 2.扫描相关的类
        doScanner(contextConfig.getProperty("scanPackage"));


        // 3.初始化扫描到的类，并且将它们放入到IOC容器
        try {
            doInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 4. 完成依赖注入
        doAutoWired();

        // 5. 初始化HandleMapping
        initHandleMapping();

        System.out.println("SX spring framework is init");
    }


    // 初始化 url 和 Method 的一对一的关系
    private void initHandleMapping() {
        if (ioc.isEmpty()) {return;}
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            Class<?> clazz = entry.getValue().getClass();
            if (!clazz.isAnnotationPresent(SXController.class)) {
                continue;
            }
            // 保存写在类上面的 @SXRequestMapping
            String baseUrl = "";
            if (clazz.isAnnotationPresent(SXRequestMapping.class)) {
                SXRequestMapping requestMapping = clazz.getAnnotation(SXRequestMapping.class);
                baseUrl = requestMapping.value();
            }

            // 默认获取所有的 public 类型的方法
            for (Method method : clazz.getMethods()) {
                if (!method.isAnnotationPresent(SXRequestMapping.class)) {
                    continue;
                }
                SXRequestMapping requestMapping = method.getAnnotation(SXRequestMapping.class);
                String regex = ("/" + baseUrl + requestMapping.value()).replaceAll("/+", "");
                Pattern pattern = Pattern.compile(regex);
                handlerMapping.add(new Handler(entry.getValue(), method,pattern ));
                System.out.println("Mapping:" + regex + "," + method);
            }
        }

    }

    // 完成依赖注入
    private void doAutoWired() {
        if (ioc.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            // 获取所有的字段，包括 private，protected，default
            // 正常来说，普通的oop编程只能获得public类型的字段
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for (Field field : fields) {
                if (!field.isAnnotationPresent(SXAutoWired.class)) {
                    continue;
                }
                SXAutoWired autoWired = field.getAnnotation(SXAutoWired.class);
                // 如果用户没有自定义beanName,默认根据类型注入
                String beanName = autoWired.value().trim();
                if ("".equals(beanName)) {
                    beanName = field.getType().getName();
                }
                // 如果是public以外的类型，只要加了@AutoWired注解的都要强制赋值
                // 反射中叫做暴力访问
                field.setAccessible(true);

                try {
                    // 利用反射机制动态给字段赋值
                    field.set(entry.getValue(), ioc.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 初始化扫描到的类
    private void doInstance() throws Exception {
        // 初始化，为DI做准备
        if (classNames.isEmpty()) {
            return;
        }
        for (String className : classNames) {
            Class<?> clazz = Class.forName(className);
            if (clazz.isAnnotationPresent(SXController.class)) {
                Object instance = clazz.newInstance();
                String beanName = toLowerFirstCase(clazz.getSimpleName());
                ioc.put(beanName, instance);
            } else if (clazz.isAnnotationPresent(SXService.class)) {
                // 自定义的beanName
                SXService service = clazz.getAnnotation(SXService.class);
                String beanName = service.value();
                // 默认类名
                if ("".equals(beanName.trim())) {
                    beanName = toLowerFirstCase(clazz.getSimpleName());
                }
                Object instance = clazz.newInstance();
                ioc.put(beanName, instance);
                // 3.根据类型自动赋值，这是投机取巧的方式-->这是什么目的呢？
                for (Class<?> i : clazz.getInterfaces()) {
                    if (ioc.containsKey(i.getName())) {
                        throw new Exception("the “" + i.getName() + "”is exists");
                    }
                    // 把接口的类型直接当成key
                    ioc.put(i.getName(), instance);
                }
            } else {
                continue;
            }
        }
    }

    // 将类名首字母改为小写
    private String toLowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    // 加载配置文件
    private void doLoadConfig(String contextConfigLocation) {
        InputStream fis = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);
        try {
            contextConfig.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doScanner(String scanPackage) {
        // scanPackage = com.sx.demo
        // 转换为文件路径，实际上就是把.替换为/
        URL url = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.", "/"));
        File classPath = new File(url.getFile());
        for (File file : classPath.listFiles()) {
            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
            } else {
                if (!file.getName().endsWith(".class")) {
                    continue;
                }
                String clazzName = (scanPackage + "." + file.getName()).replace(".class", "");
                classNames.add(clazzName);
            }
        }
    }

    // Handler记录Controller中的RequestMapping和Method的对应关系
    private class Handler {
        protected Object controller;// 保存方法对应的实例
        protected Method method;// 保存映射的方法
        protected Pattern pattern;
        protected Map<String, Integer> paramIndexMapping; // 参数顺序

        public Handler(Object controller, Method method, Pattern pattern) {
            this.controller = controller;
            this.method = method;
            this.pattern = pattern;
            paramIndexMapping = new HashMap<String, Integer>();
            putParamIndexMapping(method);
        }

        // 构造一个handle的基本参数
        private void putParamIndexMapping(Method method) {
            // 提取方法中加了参数的注解
            Annotation[][] pa = method.getParameterAnnotations();
            for (int i = 0; i < pa.length; i++) {
                for (Annotation a : pa[i]) {
                    if (a instanceof SXRequestParam) {
                        String paramName = ((SXRequestParam) a).value();
                        if (!"".equals(paramName.trim())) {
                            paramIndexMapping.put(paramName, i);
                        }
                    }
                }
            }

            // 提取方法中的request和response参数
            Class<?>[] paramTypes = getParameterTypes(method);
            for (int i = 0; i < paramTypes.length; i++) {
                Class<?> paramType = paramTypes[i];
                if (paramType == HttpServletRequest.class || paramType == HttpServletResponse.class) {
                    paramIndexMapping.put(paramType.getName(), i);
                }
            }
        }

        private Class<?>[] getParameterTypes(Method method) {
            return method.getParameterTypes();
        }

    }
}
