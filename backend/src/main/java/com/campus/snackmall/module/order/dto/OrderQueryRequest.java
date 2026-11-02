package com.campus.snackmall.module.order.dto;

import lombok.Data;

@Data
public class OrderQueryRequest {

    private Long current = 1L;
    private Long size = 10L;
    private Integer status;
}
