package com.dsu.hope_bank_app_middleware.request.ipsRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IpsQrStartOfPaymentRequest {
    private String uetr;
    private String amountType;
    private String sum;
}
