package com.campus.snackmall.module.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("snack_product")
public class SnackProduct {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long categoryId;
    private Long merchantId;
    private String name;
    private String coverImage;
    private BigDecimal price;
    private Integer stock;
    private Integer sales;
    private String description;
    private Integer status;
    private Integer auditStatus;
    private Integer isDeleted;
    private Integer version;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}