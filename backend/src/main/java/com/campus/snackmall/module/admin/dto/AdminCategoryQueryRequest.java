package com.campus.snackmall.module.admin.dto;

import lombok.Data;

@Data
public class AdminCategoryQueryRequest {

    private Long current = 1L;
    private Long size = 10L;
    private String keyword;
}
