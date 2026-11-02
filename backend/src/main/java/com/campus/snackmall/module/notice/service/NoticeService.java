package com.campus.snackmall.module.notice.service;

import com.campus.snackmall.common.model.PageResponse;
import com.campus.snackmall.module.admin.dto.AdminNoticeQueryRequest;
import com.campus.snackmall.module.admin.dto.AdminNoticeSaveRequest;
import com.campus.snackmall.module.notice.vo.NoticeVO;
import java.util.List;

public interface NoticeService {

    List<NoticeVO> publicList();

    PageResponse<NoticeVO> adminPage(AdminNoticeQueryRequest request);

    Boolean create(AdminNoticeSaveRequest request);

    Boolean update(Long id, AdminNoticeSaveRequest request);

    Boolean delete(Long id);
}
