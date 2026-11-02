package com.campus.snackmall.module.auth.service;

import com.campus.snackmall.module.auth.dto.LoginRequest;
import com.campus.snackmall.module.auth.dto.RegisterRequest;
import com.campus.snackmall.module.auth.vo.LoginVO;
import com.campus.snackmall.module.auth.vo.UserInfoVO;

public interface AuthService {

    Boolean register(RegisterRequest request);

    Boolean registerMerchant(RegisterRequest request);

    LoginVO login(LoginRequest request);

    LoginVO merchantLogin(LoginRequest request);

    LoginVO adminLogin(LoginRequest request);

    UserInfoVO currentUser();

    Boolean logout();
}
