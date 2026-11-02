package com.campus.snackmall.module.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import lombok.Data;

@Data
public class CreateOrderRequest {

    @NotEmpty(message = "购物车ID不能为空")
    private List<Long> cartIds;

    @NotBlank(message = "收货人不能为空")
    private String receiverName;

    @Pattern(regexp = "^1\\d{10}$", message = "手机号格式不正确")
    private String receiverPhone;

    @NotBlank(message = "收货地址不能为空")
    private String receiverAddress;

    private String remark;
}
