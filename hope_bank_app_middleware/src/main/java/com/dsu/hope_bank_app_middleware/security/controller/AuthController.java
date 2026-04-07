package com.dsu.hope_bank_app_middleware.security.controller;

import com.dsu.hope_bank_app_middleware.security.request.ChangePasswordRequest;
import com.dsu.hope_bank_app_middleware.security.request.LoginRequest;
import com.dsu.hope_bank_app_middleware.security.request.LogoutRequest;
import com.dsu.hope_bank_app_middleware.security.request.PasscodeValidationRequest;
import com.dsu.hope_bank_app_middleware.security.request.RefreshTokenRequest;
import com.dsu.hope_bank_app_middleware.security.request.RegisterRequest;
import com.dsu.hope_bank_app_middleware.security.response.JWTAuthResponse;
import com.dsu.hope_bank_app_middleware.security.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    //    Build login rest api
    @PostMapping("/login")
    public ResponseEntity<JWTAuthResponse> login(@RequestBody LoginRequest loginRequest) {
        JWTAuthResponse jwtAuthResponse = authService.login(loginRequest);
        return ResponseEntity.ok(jwtAuthResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<JWTAuthResponse> refresh(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        return ResponseEntity.ok(authService.refreshAccessToken(refreshTokenRequest));
    }

    //    Build register rest api
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        String response = authService.register(registerRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        String response = authService.changePassword(changePasswordRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody(required = false) LogoutRequest logoutRequest) {
        return ResponseEntity.ok(authService.logout(authorizationHeader, logoutRequest));
    }

    @PostMapping("/isPasscodeValid")
    public ResponseEntity<Boolean> isPasscodeValid(
            @RequestBody PasscodeValidationRequest passcodeValidationRequest) {
        return ResponseEntity.ok(authService.isPasscodeValid(passcodeValidationRequest));
    }
}
