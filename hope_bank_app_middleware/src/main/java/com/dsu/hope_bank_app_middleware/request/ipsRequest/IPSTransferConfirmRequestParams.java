package com.dsu.hope_bank_app_middleware.request.ipsRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IPSTransferConfirmRequestParams {
    private String customer_account;
    private String customer_name;
    private String amount;
    private String tomember;
    private String creditorName;
    private String creditorAccount;
    private String uetr;
    private String unique_txn_ref;
    private String txn_type;
}
