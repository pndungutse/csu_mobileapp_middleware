package com.dsu.hope_bank_app_middleware.response.IPSResponse;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Maps JSON from IPS gateway {@code /api/ips/qr} (create).
 * Newer gateway responses include the PNG as base64 ({@code qrImageBase64} / nested {@code qrAsImage}).
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class IpsQrCreateResponse {

    @JsonProperty("qrCodeUrl")
    private String qrCodeUrl;

    @JsonProperty("qrAsText")
    private String qrAsText;

    @JsonProperty("qrHeaderUUID")
    @JsonAlias({"qrHeaderUuid"})
    private String qrHeaderUUID;

    @JsonProperty("qrExtensionUUID")
    @JsonAlias({"qrExtensionUuid"})
    private String qrExtensionUUID;

    private String extractedUuid;
    private String uetr;

    @JsonProperty("qrImageBase64")
    private String qrImageBase64;

    @JsonProperty("qrImageContentType")
    private String qrImageContentType;

    @JsonProperty("qrImageDataUri")
    private String qrImageDataUri;

    private String creditorName;
    private String creditorAccount;
    private String e2e;
    private String amountType;
    private String sum;
    private String currency;
    private String qrType;
    private String memberId;
    private String xmlCreditorBic;
    private QrCreate qrCreate;

    private String rawGatewayResponse;
    private String fileName;
    private String savedFilePath;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class QrCreate {
        private Integer statusCode;
        private String body;
        private String contentType;
        private String requestId;
        private String url;
    }
}
