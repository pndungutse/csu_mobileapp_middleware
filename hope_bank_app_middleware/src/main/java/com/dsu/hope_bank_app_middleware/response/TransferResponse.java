package com.dsu.hope_bank_app_middleware.response;

import com.fasterxml.jackson.annotation.JsonInclude;
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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Result {
        private String our_reference;
        private String ret_code;
        private String ret_message;
        private String fee_amount;
        private String account_balance;
        private String amount;

        // Optional fields used by IPS QR create (omitted when null on normal transfers)
        private String qrCodeUrl;
        private String qrType;
        private String creditorName;
        private String creditorAccount;
        private String memberId;
        private String currency;
        private String uetr;
        private String qrHeaderUUID;
        private String fileName;
        private String savedFilePath;
        /** Gateway-provided PNG as base64 (when available). */
        private String qrImageBase64;
    }

}
