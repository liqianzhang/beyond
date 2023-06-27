package com.practice.util;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

/**
 * @MethodName: $
 * 用于解析vm模版（world文档保存为xml格式，然后把里面的值改为velocity变量）
 * 注意：解析excel的xml模版时
 * 必须把<NumberFormat ss:Format="_ * #,##0_ ;_ * \-#,##0_ ;_ * "-"_ ;_ @_ "/> 删掉
 * 否则velocity处理模版后，导出的xls会出现内容空白问题
 * @Author: zhangliqian
 * @Date: $
 */
public class VelocityUtil {
    private static VelocityEngine ve;
    static {
        ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        ve.init();
    }
    public static VelocityEngine getVe() {
        return ve;
    }
}
