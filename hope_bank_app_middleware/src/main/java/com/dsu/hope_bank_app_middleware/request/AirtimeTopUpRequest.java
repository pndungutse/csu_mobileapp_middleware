package com.dsu.hope_bank_app_middleware.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AirtimeTopUpRequest {
    private String customer_account;
    private String phone_number;
    private String narration;
    private String amount;
}
