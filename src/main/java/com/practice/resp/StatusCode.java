package com.practice.resp;

import lombok.Getter;

@Getter
public enum StatusCode {

    Success("0000", "成功"),
    Fail("0001", "失败")
    ;

    private String code;
    private String message;

    private StatusCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
