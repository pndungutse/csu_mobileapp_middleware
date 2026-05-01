package com.dsu.hope_bank_app_middleware.response.IPSResponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class RtpParsedTransaction {
    private String id;
    private String uetr;
    @JsonProperty("msg_id")
    private String msgId;
    @JsonProperty("message_type")
    private String messageType;
    @JsonProperty("rtp_flow_state")
    private String rtpFlowState;
    @JsonProperty("received_at")
    private String receivedAt;
    @JsonProperty("tracking_id")
    private String trackingId;

    @JsonProperty("debtor_name")
    private String debtorName;
    @JsonProperty("debtor_account")
    private String debtorAccount;
    @JsonProperty("debtor_agent")
    private String debtorAgent;

    @JsonProperty("creditor_name")
    private String creditorName;
    @JsonProperty("creditor_account")
    private String creditorAccount;
    @JsonProperty("creditor_agent")
    private String creditorAgent;

    private String amount;
    private String currency;

    @JsonProperty("requested_execution_date")
    private String requestedExecutionDate;
    @JsonProperty("expiry_date")
    private String expiryDate;
    @JsonProperty("creation_date")
    private String creationDate;

    @JsonProperty("initiating_party")
    private String initiatingParty;
}
