package com.dsu.hope_bank_app_middleware.controller;

import com.dsu.hope_bank_app_middleware.request.LoanInfoRequest;
import com.dsu.hope_bank_app_middleware.request.LoanRepaymentAccountRequest;
import com.dsu.hope_bank_app_middleware.request.LoanRepaymentMomoRequest;
import com.dsu.hope_bank_app_middleware.response.LoanBalanceResponse;
import com.dsu.hope_bank_app_middleware.response.LoanRepaymentResponse;
import com.dsu.hope_bank_app_middleware.service.LoanService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/loans")
public class LoanController {
    private final LoanService loanService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/loan")
    public ResponseEntity<LoanBalanceResponse.Result> getLoanInfo(@RequestBody LoanInfoRequest loanInfoRequest) {
        LoanBalanceResponse.Result response = loanService.getLoanInfo(loanInfoRequest);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/pay_loan_momo")
    public ResponseEntity<LoanRepaymentResponse.Result> payLoanWithMomo(@RequestBody LoanRepaymentMomoRequest loanInfoRequest) {
        LoanRepaymentResponse.Result response = loanService.payLoanWithMomo(loanInfoRequest);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/pay_loan_account")
    public ResponseEntity<LoanRepaymentResponse.Result> payLoanWithAccount(@RequestBody LoanRepaymentAccountRequest loanRepaymentAccountRequest) {
        LoanRepaymentResponse.Result response = loanService.payLoanWithAccount(loanRepaymentAccountRequest);
        return ResponseEntity.ok(response);
    }


}
