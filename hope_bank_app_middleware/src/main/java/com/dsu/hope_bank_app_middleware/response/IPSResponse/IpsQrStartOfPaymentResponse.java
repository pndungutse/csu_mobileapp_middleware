package com.dsu.hope_bank_app_middleware.response.IPSResponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Maps JSON from IPS gateway {@code /api/ips/qr/start-of-payment}.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class IpsQrStartOfPaymentResponse {

    private String uetr;
    private String amountType;
    private String sum;
    private String currency;
    private String documentToken;
    private StartOfPaymentMeta startOfPayment;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StartOfPaymentMeta {
        private Integer statusCode;
        private String body;
        private String contentType;
        private String requestId;
        private String url;
    }
}
