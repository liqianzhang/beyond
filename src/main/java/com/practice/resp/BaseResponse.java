package com.practice.resp;

import javax.servlet.http.HttpServletResponse;

/**
 * @MethodName: $
 * @Description: TODO
 * @Param: $
 * @Return: $
 * @Author: zhangliqian
 * @Date: $
 */
public class BaseResponse  {
    private String code;
    private String message;



    public BaseResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public BaseResponse(StatusCode success) {
        this.code = success.getCode();
        this.message = success.getMessage();
    }
}
