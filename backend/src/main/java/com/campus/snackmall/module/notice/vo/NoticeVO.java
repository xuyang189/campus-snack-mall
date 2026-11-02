package com.campus.snackmall.module.notice.vo;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class NoticeVO {

    private Long id;
    private String title;
    private String content;
    private Integer status;
    private LocalDateTime createTime;
}
