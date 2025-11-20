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
public class MiniStatementRequest {
    @JsonProperty("account")
    private String account;
    @JsonProperty("NumberOfTransactions")
    private String NumberOfTransactions;
}
