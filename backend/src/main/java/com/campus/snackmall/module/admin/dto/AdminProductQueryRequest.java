package com.campus.snackmall.module.admin.dto;

import lombok.Data;

@Data
public class AdminProductQueryRequest {

    private Long current = 1L;
    private Long size = 10L;
    private String keyword;
    private Long categoryId;
    private Integer status;
    private Integer auditStatus;
}