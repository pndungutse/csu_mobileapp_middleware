package com.dsu.hope_bank_app_middleware.response;

import com.dsu.hope_bank_app_middleware.enumeration.Status;
import com.dsu.hope_bank_app_middleware.exception.SuccessResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeneficiaryBankResponse {
    @JsonProperty("beneficiary_code")
    private String beneficiaryCode;
    @JsonProperty("beneficiary_name")
    private String beneficiaryName;

    @JsonProperty("success_response")
    private SuccessResponse successResponse;
}
