package com.dsu.hope_bank_app_middleware.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanRepaymentAccountRequest {
    private String loan_account;
    private String account;
    private String transaction_details;
    private String amount;
}
