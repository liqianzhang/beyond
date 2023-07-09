package com.mvcframwork.annotation;

import java.lang.annotation.*;

/**
 * 自定义service注解
 *
 * @Target  此注解说明注解的作用目标，默认值为任何元素 被这个 @Target 注解修饰的注解将只能作用在成员字段上，不能用于修饰方法或者类
           ElementType.TYPE：允许被修饰的注解作用在类、接口和枚举上
           ElementType.FIELD：允许作用在属性字段上
           ElementType.METHOD：允许作用在方法上
           ElementType.PARAMETER：允许作用在方法参数上
           ElementType.CONSTRUCTOR：允许作用在构造器上
           ElementType.LOCAL_VARIABLE：允许作用在本地局部变量上
           ElementType.ANNOTATION_TYPE：允许作用在注解上
           ElementType.PACKAGE：允许作用在包上
 * @Documented  @Documented 注解表明这个注解应该被 javadoc工具记录. 默认情况下,javadoc是不包括注解的.
 * 但如果声明注解时指定了 @Documented,则它会被 javad
 * oc 之类的工具处理, 所以注解类型信息也会被包括在生成的文档中，是一个标记注解，没有成员。
 * @Retention
 *@Retention 注解是用来注解的注解，称为元注解，其作用可以简单理解为设置注解的生命周期。
 *
 * @Retention 注解传入的是 RetentionPolicy 枚举，该枚举有三个常量，分别是 SOURCE、CLASS 和 RUNTIME
 *
 * 三者区别如下：
 *
 * SOURCE 代表着注解仅保留在源级别中，编译器将Java文件编译成class文件时将之遗弃。
 * CLASS 代表着注解被保留在class文件中，JVM加载class文件时将之遗弃。
 * RUNTIME 代表着标记的注解会由JVM保留，因此运行时环境可以使用它。

 *
 */
@Target({ElementType.TYPE})
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface SXService {
    String value() default "";
}
