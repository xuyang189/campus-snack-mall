package com.campus.snackmall.module.auth.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @Pattern(regexp = "^[A-Za-z0-9_]{4,20}$", message = "用户名需为4-20位字母、数字或下划线")
    private String username;

    @Size(min = 6, max = 20, message = "密码需为6-20位")
    private String password;

    @Size(max = 10, message = "昵称长度不能超过10位")
    private String nickname;

    @Pattern(regexp = "^$|^1\\d{10}$", message = "手机号格式不正确")
    private String phone;
}
