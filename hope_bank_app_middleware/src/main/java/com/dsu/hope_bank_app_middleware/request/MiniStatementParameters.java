package com.dsu.hope_bank_app_middleware.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MiniStatementParameters {
    @JsonProperty("account")
    private String account;
    private String NumberOfTransactions;
    @JsonProperty("unique_txn_ref")
    private String unique_txn_ref;
    @JsonProperty("txn_type")
    private String txn_type;
}
