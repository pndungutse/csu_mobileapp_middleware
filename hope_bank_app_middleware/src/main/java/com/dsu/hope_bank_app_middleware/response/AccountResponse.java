package com.dsu.hope_bank_app_middleware.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponse {
    private String uniqueReference;
    private String serviceStatus;
    private String customerName;
    private List<AccountDetails> accounts;

    // Nested class for account details
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AccountDetails {
        private String accountId;
        private String linkedLoan;
        private String loanStatus;
        private String loanProduct;
        private String accountCategory;
        private String accountTitle;
        private String accountCurrency;
        private String accountStatus;
        private String retCode;
    }
}
