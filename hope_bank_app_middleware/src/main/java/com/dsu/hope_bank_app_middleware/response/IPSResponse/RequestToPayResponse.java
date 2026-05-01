package com.dsu.hope_bank_app_middleware.response.IPSResponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestToPayResponse {
    private String uniqueReference;
    private String serviceStatus;
    private String date;
    private String time;
    private ResponseMessage responseMessage;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResponseMessage {
        private String trackingId;
        private String status;
        private String message;
    }
}
