package com.dsu.hope_bank_app_middleware.response;

import lombok.Data;

// import java.util.List;

@Data
public class AccountBalanceResponse {
    private String uniqueReference;
    private String serviceStatus;
    private ResponseMessage responseMessage;

    @Data
    public static class ResponseMessage {
        private Result result;
    }

    @Data
    public static class Result {
        private String our_reference;
        private String account;
        private String available_balance;
        private String currency;
        private String system_date;
        private String date;
        private String ret_code;
        private String ret_message;
        private String working_balance;
        private String fee_amount;
        private String limit;
        private String locked_amount;
    }

}
