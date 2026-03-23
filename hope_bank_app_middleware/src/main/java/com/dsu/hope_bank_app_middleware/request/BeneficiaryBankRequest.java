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
public class BeneficiaryBankRequest {
    @JsonProperty("beneficiary_code")
    private String beneficiaryCode;
    @JsonProperty("beneficiary_name")
    private String beneficiaryName;
}
