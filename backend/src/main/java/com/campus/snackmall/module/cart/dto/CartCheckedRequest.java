package com.campus.snackmall.module.cart.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartCheckedRequest {

    @NotNull(message = "选中状态不能为空")
    private Boolean checked;
}
