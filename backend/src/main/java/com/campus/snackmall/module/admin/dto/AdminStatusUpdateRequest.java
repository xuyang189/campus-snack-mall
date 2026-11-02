package com.campus.snackmall.module.admin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminStatusUpdateRequest {

    @NotNull(message = "状态不能为空")
    private Integer status;
}
