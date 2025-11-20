package com.dsu.hope_bank_app_middleware.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BankToWalletTransferParameters {
    @JsonProperty("customer_account")
    private String customerAccount;
    @JsonProperty("amount")
    private String amount;
    @JsonProperty("narration")
    private String narration;
    @JsonProperty("network_operator")
    private String network_operator;
    @JsonProperty("phone_number")
    private String phone_number;
    @JsonProperty("ccy")
    private String ccy;
    @JsonProperty("country")
    private String country;
    @JsonProperty("unique_txn_ref")
    private String unique_txn_ref;
    @JsonProperty("txn_type")
    private String txn_type;
}
