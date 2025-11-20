package com.dsu.hope_bank_app_middleware.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MiniStatementResponse {
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
        private String ret_code;
        private List<Transaction> txn;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Transaction {
        private String amount;
        private String available_balance;
        private String current_balance;
        private String date;
        private String description;
        private String narrative;
        private String reference;
        private String type;
    }
}
