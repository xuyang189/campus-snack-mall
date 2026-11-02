package com.campus.snackmall.module.user.service;

import com.campus.snackmall.common.model.PageResponse;
import com.campus.snackmall.module.admin.dto.AdminUserQueryRequest;
import com.campus.snackmall.module.admin.dto.AdminStatusUpdateRequest;
import com.campus.snackmall.module.user.dto.UpdatePasswordRequest;
import com.campus.snackmall.module.user.dto.UpdateProfileRequest;
import com.campus.snackmall.module.user.vo.UserProfileVO;

public interface UserService {

    UserProfileVO getProfile();

    Boolean updateProfile(UpdateProfileRequest request);

    Boolean updatePassword(UpdatePasswordRequest request);

    PageResponse<UserProfileVO> adminPage(AdminUserQueryRequest request);

    Boolean updateUserStatus(Long id, AdminStatusUpdateRequest request);
}
