package com.dsu.hope_bank_app_middleware.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SingleAccountResponse {
    private String customerName;
    private String customerNumber;
    private String accountNumber;
    private String retCode;
}
