package com.dsu.hope_bank_app_middleware.response.navigations;

import com.dsu.hope_bank_app_middleware.general_enumerations.Status;
import com.dsu.hope_bank_app_middleware.exception.SuccessResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssociateBankResponse {
    private String id;
    @JsonProperty("associate_bank_name")
    private String associateBankName;
    @JsonProperty("associate_bank_status")
    private Status associateBankStatus;
    @JsonProperty("associate_bank_added_date")
    private Date associateBankAddedDate;
    @JsonProperty("associate_bank_updated_date")
    private Date associateBankUpdatedDate;
    @JsonProperty("success_response")
    private SuccessResponse successResponse;
}
