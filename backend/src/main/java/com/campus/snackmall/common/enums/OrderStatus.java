package com.campus.snackmall.common.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    CREATED(1, "待处理"),
    DELIVERING(2, "配送中"),
    FINISHED(3, "已完成"),
    CANCELLED(4, "已取消");

    private final int code;
    private final String text;

    OrderStatus(int code, String text) {
        this.code = code;
        this.text = text;
    }

    public static String getText(Integer code) {
        if (code == null) {
            return "";
        }
        for (OrderStatus status : values()) {
            if (status.code == code) {
                return status.text;
            }
        }
        return "";
    }
}
