package com.dsu.hope_bank_app_middleware.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanBalanceParameters {
    @JsonProperty("loan")
    private String loan;
    @JsonProperty("unique_txn_ref")
    private String uniqueTxnRef;
    @JsonProperty("txn_type")
    private String txnType;
}
