package com.dsu.hope_bank_app_middleware.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AirtimeTopUpParameters {
    private String customer_account;
    private String phone_number;
    private String narration;
    private String amount;
    private String unique_txn_ref;
    private String txn_type;
}
