package com.campus.snackmall.module.notice.controller;

import com.campus.snackmall.common.result.Result;
import com.campus.snackmall.module.notice.service.NoticeService;
import com.campus.snackmall.module.notice.vo.NoticeVO;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notices")
public class NoticeController {

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @GetMapping
    public Result<List<NoticeVO>> list() {
        return Result.success("查询成功", noticeService.publicList());
    }
}
