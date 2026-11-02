package com.campus.snackmall.module.notice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.snackmall.common.exception.BusinessException;
import com.campus.snackmall.common.model.PageResponse;
import com.campus.snackmall.module.admin.dto.AdminNoticeQueryRequest;
import com.campus.snackmall.module.admin.dto.AdminNoticeSaveRequest;
import com.campus.snackmall.module.notice.entity.SysNotice;
import com.campus.snackmall.module.notice.mapper.SysNoticeMapper;
import com.campus.snackmall.module.notice.service.NoticeService;
import com.campus.snackmall.module.notice.vo.NoticeVO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NoticeServiceImpl implements NoticeService {

    private final SysNoticeMapper noticeMapper;

    public NoticeServiceImpl(SysNoticeMapper noticeMapper) {
        this.noticeMapper = noticeMapper;
    }

    @Override
    public List<NoticeVO> publicList() {
        return noticeMapper.selectList(new LambdaQueryWrapper<SysNotice>()
                        .eq(SysNotice::getStatus, 1)
                        .orderByDesc(SysNotice::getCreateTime))
                .stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public PageResponse<NoticeVO> adminPage(AdminNoticeQueryRequest request) {
        Page<SysNotice> page = new Page<>(request.getCurrent(), request.getSize());
        Page<SysNotice> result = noticeMapper.selectPage(page, new LambdaQueryWrapper<SysNotice>()
                .orderByDesc(SysNotice::getCreateTime));
        List<NoticeVO> records = result.getRecords().stream().map(this::toVO).collect(Collectors.toList());
        return new PageResponse<>(records, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean create(AdminNoticeSaveRequest request) {
        SysNotice notice = new SysNotice();
        notice.setTitle(request.getTitle());
        notice.setContent(request.getContent());
        notice.setStatus(request.getStatus());
        notice.setCreateTime(LocalDateTime.now());
        notice.setUpdateTime(LocalDateTime.now());
        noticeMapper.insert(notice);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean update(Long id, AdminNoticeSaveRequest request) {
        SysNotice notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw new BusinessException(404, "公告不存在");
        }
        notice.setTitle(request.getTitle());
        notice.setContent(request.getContent());
        notice.setStatus(request.getStatus());
        notice.setUpdateTime(LocalDateTime.now());
        noticeMapper.updateById(notice);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delete(Long id) {
        noticeMapper.deleteById(id);
        return true;
    }

    private NoticeVO toVO(SysNotice notice) {
        NoticeVO vo = new NoticeVO();
        vo.setId(notice.getId());
        vo.setTitle(notice.getTitle());
        vo.setContent(notice.getContent());
        vo.setStatus(notice.getStatus());
        vo.setCreateTime(notice.getCreateTime());
        return vo;
    }
}
