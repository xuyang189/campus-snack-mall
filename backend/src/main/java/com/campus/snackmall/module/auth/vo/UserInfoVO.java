package com.campus.snackmall.module.auth.vo;

import lombok.Data;

@Data
public class UserInfoVO {

    private Long id;
    private String username;
    private String nickname;
    private String phone;
    private String avatar;
    private String role;
}
