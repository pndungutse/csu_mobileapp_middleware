package com.dsu.hope_bank_app_middleware.response;

import lombok.Data;

@Data
public class TransferResponse {
    private String uniqueReference;
    private String serviceStatus;
    private ResponseMessage responseMessage;

    @Data
    public static class ResponseMessage {
        private String sequence;
        private String unique_txn_ref;
        private String response_code;
        private String response_message;
        private String txn_type;
        private String t24UniqueRef;
        private Result result;
    }

    @Data
    public static class Result {
        private String our_reference;
        private String ret_code;
        private String ret_message;
        private String fee_amount;
        private String account_balance;
    }

}
