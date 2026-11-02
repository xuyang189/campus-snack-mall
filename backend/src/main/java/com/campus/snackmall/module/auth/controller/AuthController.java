package com.campus.snackmall.module.auth.controller;

import com.campus.snackmall.common.result.Result;
import com.campus.snackmall.module.auth.dto.LoginRequest;
import com.campus.snackmall.module.auth.dto.RegisterRequest;
import com.campus.snackmall.module.auth.service.AuthService;
import com.campus.snackmall.module.auth.vo.LoginVO;
import com.campus.snackmall.module.auth.vo.UserInfoVO;
import com.campus.snackmall.security.RequireLogin;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public Result<Boolean> register(@Valid @RequestBody RegisterRequest request) {
        return Result.success("注册成功", authService.register(request));
    }

    @PostMapping("/merchant/register")
    public Result<Boolean> registerMerchant(@Valid @RequestBody RegisterRequest request) {
        return Result.success("入驻成功", authService.registerMerchant(request));
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginRequest request) {
        return Result.success("登录成功", authService.login(request));
    }

    @PostMapping("/merchant/login")
    public Result<LoginVO> merchantLogin(@Valid @RequestBody LoginRequest request) {
        return Result.success("登录成功", authService.merchantLogin(request));
    }

    @PostMapping("/admin/login")
    public Result<LoginVO> adminLogin(@Valid @RequestBody LoginRequest request) {
        return Result.success("登录成功", authService.adminLogin(request));
    }

    @RequireLogin
    @GetMapping("/me")
    public Result<UserInfoVO> me() {
        return Result.success("查询成功", authService.currentUser());
    }

    @RequireLogin
    @PostMapping("/logout")
    public Result<Boolean> logout() {
        return Result.success("退出成功", authService.logout());
    }
}
