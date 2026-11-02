package com.campus.snackmall.module.auth.vo;

import lombok.Data;

@Data
public class LoginVO {

    private String token;
    private UserInfoVO userInfo;
}
