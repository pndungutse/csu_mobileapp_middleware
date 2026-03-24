package com.dsu.hope_bank_app_middleware.security.request;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    private String first_name;
    private String last_name;
    private String customer_number;
    private String env;
    private String username;
    private String email;
    private String password;
}
