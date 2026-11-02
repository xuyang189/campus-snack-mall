package com.campus.snackmall.module.admin.dto;

import lombok.Data;

@Data
public class AdminOrderQueryRequest {

    private Long current = 1L;
    private Long size = 10L;
    private String orderNo;
    private Integer status;
}
