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
public class SelfRegistrationRequest {
    @JsonProperty("phone_number")
    private String PhoneNumber;
    @JsonProperty("account_number")
    private String AccountNumber;
    @JsonProperty("legal_id")
    private String legalId;
    @JsonProperty("channel")
    private String Channel;
    @JsonProperty("is_registered")
    private String IsRegistered;

}
