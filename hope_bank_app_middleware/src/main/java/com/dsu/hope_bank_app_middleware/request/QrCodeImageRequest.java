package com.dsu.hope_bank_app_middleware.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QrCodeImageRequest {

    /**
     * Content encoded into the QR (typically a payment QR URL).
     */
    @JsonProperty("qr_url")
    private String qrUrl;

    /**
     * Optional image size in pixels (default 300).
     */
    private Integer size;
}
