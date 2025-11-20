package com.dsu.hope_bank_app_middleware.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanRepaymentAccountParameters {
    private String txn_type;
    private String loan_account;
    private String account;
    private String transaction_details;
    private String amount;
    private String unique_txn_ref;
}
