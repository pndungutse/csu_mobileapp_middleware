package com.dsu.hope_bank_app_middleware.request.ipsRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IpsRequestToPayRequest {
    private String uetr;
    private String txn_amount;
    private String accountCurrency;
    private String customerName;
    private String cust_account;
    private String receiver_name;
    private String receiver_account;
    private String beneficiary_name;
    private String txn_type;
}
