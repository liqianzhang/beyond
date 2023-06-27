package com.practice.controller;

import com.practice.resp.BaseResponse;
import com.practice.resp.StatusCode;
import com.practice.service.QRService;
import com.practice.util.DateUtils;
import com.practice.util.QRCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @MethodName: $
 * @Description: TODO
 * @Param: $
 * @Return: $
 * @Author: zhangliqian
 * @Date: $
 */
@RestController
@Slf4j
public class QrCodeController {


    private static final String RootPath="E:\\shFiles\\QRCode";
    private static final String FileFormat=".png";


    // 生成二维码并将其存放于本地目录
    @PostMapping("generate/v1")
    public BaseResponse generateV1(@RequestParam("content") String content){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            final String fileName = DateUtils.getDate();
            QRCodeUtil.createCodeToFile(content,new File(RootPath),fileName+FileFormat);
        }catch (Exception e){
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
         return response;
    }

    //生成二维码并将其返回给前端调用者
    @PostMapping("generate/v2")
    public void generateV2(@RequestBody String content, HttpServletResponse servletResponse){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            QRCodeUtil.createCodeToOutputStream(content,servletResponse.getOutputStream());

        }catch (Exception e){
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
//        return response;
    }

    @Autowired
    QRService qrService;
    @RequestMapping("123")
    public void generateV3(String content, HttpServletResponse servletResponse) throws IOException, IOException {
        qrService.generateStream(content,servletResponse);
    }
}
