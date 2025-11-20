package com.dsu.hope_bank_app_middleware.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SelfRegistrationResponse {
    private String uniqueReference;
    private String serviceStatus;
    private ResponseMessage responseMessage;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResponseMessage {
        private Result result;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Result {
        private String ret_code;
        private String customerNumber;
        @JsonProperty("Status")
        private String status;
        @JsonProperty("NumberExists")
        private String numberExists;
        @JsonProperty("LegalIdNumber")
        private String legalIdNumber;
        @JsonProperty("CustomerName")
        private String customerName;
        @JsonProperty("CustomerLanguage")
        private String customerLanguage;
    }
}
