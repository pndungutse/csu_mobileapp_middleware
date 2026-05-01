package com.dsu.hope_bank_app_middleware.request.ipsRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IPSTransferConfirmRequest {
    private String debtor_account;
    private String debtor_name;
    private String creditor_account;
    private String creditor_name;
    private String creditor_agent;
    private String amount;
    private String currency;
    private String uetr;
}
