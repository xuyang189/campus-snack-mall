package com.campus.snackmall.module.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdatePasswordRequest {

    @NotBlank(message = "原密码不能为空")
    private String oldPassword;

    @Size(min = 6, max = 20, message = "新密码需为6-20位")
    private String newPassword;
}
