package com.dsu.hope_bank_app_middleware.request.ipsRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IPSPayQrRequest {
    private String customer_account; // from client
    private String customer_name;
    private String amount; //from client
    private String tomember;
    private String member_name;
    private String member_account;
    private String qr_reference; //from client
    private String unique_txn_ref;
    private String txn_type;
}
