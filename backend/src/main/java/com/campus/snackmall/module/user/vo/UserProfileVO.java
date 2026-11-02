package com.campus.snackmall.module.user.vo;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class UserProfileVO {

    private Long id;
    private String username;
    private String nickname;
    private String phone;
    private String avatar;
    private Integer status;
    private LocalDateTime createTime;
}
