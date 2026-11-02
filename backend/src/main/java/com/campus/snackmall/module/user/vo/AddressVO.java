package com.campus.snackmall.module.user.vo;

import lombok.Data;

@Data
public class AddressVO {

    private Long id;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private Boolean isDefault;
}
