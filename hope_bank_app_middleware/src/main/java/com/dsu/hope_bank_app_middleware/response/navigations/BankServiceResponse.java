package com.dsu.hope_bank_app_middleware.response.navigations;

import com.dsu.hope_bank_app_middleware.exception.SuccessResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankServiceResponse {
    @JsonProperty("id")
    private String Id;
    @JsonProperty("service_name")
    private String serviceName;
    @JsonProperty("service_icon")
    private String serviceIcon;
    @JsonProperty("success_response")
    private SuccessResponse successResponse;
}
