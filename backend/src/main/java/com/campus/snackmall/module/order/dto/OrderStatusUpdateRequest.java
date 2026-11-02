package com.campus.snackmall.module.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderStatusUpdateRequest {

    @NotNull(message = "状态不能为空")
    private Integer status;
}
