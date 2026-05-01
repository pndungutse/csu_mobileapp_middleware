package com.dsu.hope_bank_app_middleware.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanScheduleRequest {
    private String txn_type;
    private String accountNumber;
    private String unique_txn_ref;
}
