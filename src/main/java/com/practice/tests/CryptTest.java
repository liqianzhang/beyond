package com.practice.tests;

import org.apache.commons.codec.binary.Base64;
import org.springframework.util.DigestUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * @MethodName: $
 * @Description: TODO
 * @Param: $
 * @Return: $
 * @Author: zhangliqian
 * @Date: $
 */
public class CryptTest {

    private static final BASE64Decoder decoder = new BASE64Decoder();
    private static final BASE64Encoder encoder = new BASE64Encoder();
    private static final Base64 base64 = new Base64();

    /**
     * BASE64加密
     *
     * @param key
     * @return
     * @throws Exception
     */
    public static String encryptBASE64(String key) throws Exception {
        if (key == null || key.length() < 1) {
            return "";
        }
        //return new String(encoder.encode(key.getBytes()));
        return new String(base64.encodeBase64URLSafe((new String(encoder.encode(key.getBytes()))).getBytes()));
    }


    public void changeCodeGBK(){

    }
    /**
     * BASE64解密
     *
     * @param key
     * @return
     * @throws Exception
     */
    public static String decryptBASE64(String key) throws Exception {
        if (key == null || key.length() < 1) {
            return "";
        }
        return new String(decoder.decodeBuffer(new String(base64.decodeBase64(key.getBytes()))));
        //return new String(base64.decodeBase64(key.getBytes()));


    }

    public static void main(String[] args) throws Exception {
        String s= CryptTest.encryptBASE64("我爱高倩");
//        System.out.println(s);
//        System.out.println(CryptTest.decryptBASE64(s));
        String md5Encode = getMd5Encode(s);
        String url = "/tjweb/m?t=" + md5Encode;
        System.out.println("url = " + url);
        System.out.println(URLEncoder.encode(url, "UTF-8"));
        System.out.println(URLDecoder.decode(url, "UTF-8"));


    }

    public static String getMd5Encode(String passWord) {
        return DigestUtils.md5DigestAsHex(passWord.getBytes());
    }
}
