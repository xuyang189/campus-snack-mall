package com.campus.snackmall.module.admin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductAuditRequest {

    @NotNull(message = "审核状态不能为空")
    private Integer auditStatus;
}