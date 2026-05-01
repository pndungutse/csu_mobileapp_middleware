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
public class GeneralNeededInfoRequest {
    @JsonProperty("associate_bank")
    private String associateBank;
    @JsonProperty("primary_color")
    private String primaryColor;
    @JsonProperty("secondary_color")
    private String secondaryColor;
    @JsonProperty("third_color")
    private String thirdColor;
    @JsonProperty("fourth_color")
    private String fourthColor;
    @JsonProperty("contact_email")
    private String contactEmail;
    @JsonProperty("contact_phone_call")
    private String contactPhoneCall;
    @JsonProperty("contact_phone_sms")
    private String contactPhoneSms;
    @JsonProperty("is_any_service_down")
    private String isAnyServiceDown;
    @JsonProperty("banner_warning_message")
    private String bannerWarningMessage;
}
