package com.dsu.hope_bank_app_middleware.request.ipsRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IPSPayQrRequest {
    private String customer_account;
    private String customer_name;
    private String amount;
    private String tomember;
    private String member_name;
    private String qr_reference;
    private String unique_txn_ref;
    private String txn_type;
}
