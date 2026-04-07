package com.dsu.hope_bank_app_middleware.security.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasscodeValidationRequest {
    private String username;
    private String password;
}
