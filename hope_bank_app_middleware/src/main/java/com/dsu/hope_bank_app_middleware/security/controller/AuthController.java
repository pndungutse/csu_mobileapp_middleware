package com.dsu.hope_bank_app_middleware.security.controller;

import com.dsu.hope_bank_app_middleware.security.request.ChangePasswordRequest;
import com.dsu.hope_bank_app_middleware.security.request.LoginRequest;
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
    public ResponseEntity<JWTAuthResponse> login(@RequestBody LoginRequest loginRequest){
        JWTAuthResponse jwtAuthResponse = authService.login(loginRequest);
        return ResponseEntity.ok(jwtAuthResponse);
    }

    //    Build register rest api
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest){
        String response = authService.register(registerRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest){
        String response = authService.changePassword(changePasswordRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authorizationHeader){
        String response = authService.logout(authorizationHeader);
        return ResponseEntity.ok(response);
    }
}
