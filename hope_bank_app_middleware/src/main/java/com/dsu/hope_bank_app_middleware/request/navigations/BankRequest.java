package com.dsu.hope_bank_app_middleware.request.navigations;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankRequest {
    @JsonProperty("associate_bank_name")
    private String associateBankName;
}
