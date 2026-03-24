package com.dsu.hope_bank_app_middleware.security.service;

import com.dsu.hope_bank_app_middleware.security.request.ChangePasswordRequest;
import com.dsu.hope_bank_app_middleware.security.request.LoginRequest;
import com.dsu.hope_bank_app_middleware.security.request.RegisterRequest;
import com.dsu.hope_bank_app_middleware.security.response.JWTAuthResponse;

public interface AuthService {
    JWTAuthResponse login(LoginRequest loginRequest);
    String register(RegisterRequest registerRequest);
    String changePassword(ChangePasswordRequest changePasswordRequest);
    String logout(String authorizationHeader);
}
