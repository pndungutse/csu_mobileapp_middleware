package com.dsu.hope_bank_app_middleware.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanScheduleResponse {
    private String uniqueReference;
    private String serviceStatus;
    private ResponseMessage responseMessage;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResponseMessage {
        private String sequence;
        private String unique_txn_ref;
        private String response_code;
        private String response_message;
        private String txn_type;
        private String t24UniqueRef;
        private String MappingRecord;
        private Result result;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Result {
        private String ret_code;
        private String loanId;
        private List<LoanSchedules> loanSchedules;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LoanSchedules {
        private String scheduleDate;
        private String scheduleAmount;
        private String scheduleActualInstallment;
        private String scheduleInterest;
    }
}
