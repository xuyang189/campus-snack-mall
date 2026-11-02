package com.campus.snackmall.module.cart.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Data;

@Data
public class CartBatchDeleteRequest {

    @NotEmpty(message = "购物车ID不能为空")
    private List<Long> ids;
}
