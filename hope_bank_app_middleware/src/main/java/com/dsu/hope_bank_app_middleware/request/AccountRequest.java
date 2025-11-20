package com.dsu.hope_bank_app_middleware.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountRequest {
    @JsonProperty("txn_type")
    private String txnType;
    @JsonProperty("accountNumber")
    private String accountNumber;
    @JsonProperty("Customerid")
    private String customerId;
    @JsonProperty("legalId")
    private String legalId;
}
