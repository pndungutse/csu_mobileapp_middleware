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
public class BankServiceRequest {
    @JsonProperty("service_name")
    private String serviceName;
    @JsonProperty("service_icon")
    private String serviceIcon;
}
