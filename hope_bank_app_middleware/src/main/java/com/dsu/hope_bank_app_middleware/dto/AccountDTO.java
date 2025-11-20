package com.dsu.hope_bank_app_middleware.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDTO {
    @JsonProperty("action")
    private String action = "TRANSACTION";
    @JsonProperty("parameters")
    private Parameters parameters;
    @JsonProperty("sequence")
    private String sequence;

    // Nested Parameters class
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Parameters {
        @JsonProperty("txn_type")
        private String txnType = "AccountInformation";
        @JsonProperty("accountNumber")
        private String accountNumber;
        @JsonProperty("Customerid")
        private String customerId;
        @JsonProperty("legalId")
        private String legalId;
        @JsonProperty("unique_txn_ref")
        private String uniqueTxnRef;
    }
}
