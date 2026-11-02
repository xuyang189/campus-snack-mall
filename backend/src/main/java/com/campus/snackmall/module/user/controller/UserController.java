package com.campus.snackmall.module.user.controller;

import com.campus.snackmall.common.result.Result;
import com.campus.snackmall.module.user.dto.UpdatePasswordRequest;
import com.campus.snackmall.module.user.dto.UpdateProfileRequest;
import com.campus.snackmall.module.user.service.UserService;
import com.campus.snackmall.module.user.vo.UserProfileVO;
import com.campus.snackmall.security.RequireLogin;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequireLogin
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public Result<UserProfileVO> profile() {
        return Result.success("查询成功", userService.getProfile());
    }

    @PutMapping("/profile")
    public Result<Boolean> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        return Result.success("修改成功", userService.updateProfile(request));
    }

    @PutMapping("/password")
    public Result<Boolean> updatePassword(@Valid @RequestBody UpdatePasswordRequest request) {
        return Result.success("密码修改成功", userService.updatePassword(request));
    }
}
