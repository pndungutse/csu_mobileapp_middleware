package com.dsu.hope_bank_app_middleware.response.IPSResponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RtpPendingTransactionsResponse {
    private int count;
    private List<RtpTransaction> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RtpTransaction {
        private String messageType;
        private String uetr;
        private String msgId;
        private String rtpFlowState;
        private String id;
        private String receivedAt;
        private String processedMessage;
        private String rawMessage;
        private String trackingId;
    }
}
