package com.dsu.hope_bank_app_middleware.security.service;

import com.dsu.hope_bank_app_middleware.security.request.LoginRequest;
import com.dsu.hope_bank_app_middleware.security.request.RegisterRequest;

public interface AuthService {
    String login(LoginRequest loginRequest);
    String register(RegisterRequest registerRequest);
}
