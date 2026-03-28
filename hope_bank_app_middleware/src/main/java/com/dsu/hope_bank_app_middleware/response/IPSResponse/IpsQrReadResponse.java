package com.dsu.hope_bank_app_middleware.response.IPSResponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Maps JSON from IPS gateway {@code /api/ips/qr/read}.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class IpsQrReadResponse {

    @JsonProperty("qrCodeUrl")
    private String qrCodeUrl;

    private String extractedUuid;
    private String uetr;
    private String creditorName;
    private String creditorAccount;
    private String e2e;
    private String amountType;
    private String sum;
    private String currency;
    private String xmlCreditorBic;
    private QrLookup qrLookup;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class QrLookup {
        private Integer statusCode;
        private String body;
        private String contentType;
        private String requestId;
        private String url;
    }
}
