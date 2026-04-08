package com.dsu.hope_bank_app_middleware.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
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
//    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Parameters {
        private String txn_type;
        private String accountNumber;
        private String Customerid;
        private String legalId;
        private String unique_txn_ref;
    }
}
