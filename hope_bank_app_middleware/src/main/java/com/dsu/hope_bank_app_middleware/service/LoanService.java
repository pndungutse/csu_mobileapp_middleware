package com.dsu.hope_bank_app_middleware.service;

import com.dsu.hope_bank_app_middleware.request.LoanInfoRequest;
import com.dsu.hope_bank_app_middleware.request.LoanRepaymentAccountRequest;
import com.dsu.hope_bank_app_middleware.request.LoanRepaymentMomoRequest;
import com.dsu.hope_bank_app_middleware.response.LoanBalanceResponse;
import com.dsu.hope_bank_app_middleware.response.LoanRepaymentResponse;
import com.dsu.hope_bank_app_middleware.response.TransferResponse;

public interface LoanService {
    LoanBalanceResponse.Result getLoanInfo(LoanInfoRequest request);
    LoanRepaymentResponse.Result payLoanWithMomo(LoanRepaymentMomoRequest request);
    LoanRepaymentResponse.Result payLoanWithAccount(LoanRepaymentAccountRequest request);

}
