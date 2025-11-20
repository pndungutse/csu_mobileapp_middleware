package com.dsu.hope_bank_app_middleware.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanBalanceResponse {
    private String uniqueReference;
    private String serviceStatus;
    private ResponseMessage responseMessage;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResponseMessage {
        private Result result;
        private String sequence;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Result {
        private String our_reference;
        private String loan;
        private String currency;
        private String principal_balance;
        private String system_date;
        private String date;
        private String ret_code;
        private String Product;
        private String Branch;
        private String overdue_balance;
        private String maturity_date;
        private String loan_status;
    }
}
