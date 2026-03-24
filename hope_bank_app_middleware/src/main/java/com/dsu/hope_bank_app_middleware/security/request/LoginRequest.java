package com.dsu.hope_bank_app_middleware.security.request;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {
    private String usernameOrEmail;
    private String password;
}
