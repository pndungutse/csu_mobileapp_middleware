package com.dsu.hope_bank_app_middleware.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanRepaymentMomoRequest {
    private String loan_account;
    private String transaction_details;
    private String phone_number;
    private String amount;
}
