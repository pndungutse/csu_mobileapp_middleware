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
public class InternalTransferParameters {
    @JsonProperty("customer_account")
    private String customerAccount;
    @JsonProperty("receiver_account")
    private String receiverAccount;
    @JsonProperty("Narration")
    private String narration;
    @JsonProperty("amount")
    private String amount;
    @JsonProperty("amount_ccy")
    private String amount_ccy;
    @JsonProperty("unique_txn_ref")
    private String unique_txn_ref;
    @JsonProperty("txn_type")
    private String txn_type;
}
