package com.dsu.hope_bank_app_middleware.controller;

import com.dsu.hope_bank_app_middleware.request.*;
import com.dsu.hope_bank_app_middleware.response.*;
import com.dsu.hope_bank_app_middleware.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/accounts")
@Tag(name = "Account Management", description = "APIs for account operations")     
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

//    http://localhost:8080/api/v1/accounts/information
@Operation(summary = "Get Account Information",
        description = "Retrieve detailed information for a specific account")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Account information retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "404", description = "Account not found")
})
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/information")
    public ResponseEntity<AccountResponse> getAccountInformation(@RequestBody AccountRequest accountRequest) {
        AccountResponse response = accountService.getAccountInformation(accountRequest);
        return ResponseEntity.ok(response);
    }
    // http://localhost:8080/api/v1/accounts/single_information
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/single_information")
    public ResponseEntity<GenericResponse> getSingleAccountInformation(@RequestBody GenericRequest genericRequest) {
        GenericResponse response = accountService.getSingleAccountInformation(genericRequest);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/account/balance")
    public ResponseEntity<AccountBalanceResponse.Result> getAccountBalance(@RequestBody AccountBalanceRequest accountBalanceRequest) {
        AccountBalanceResponse.Result response = accountService.getAccountBalance(accountBalanceRequest);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/account/ministatement")
    public ResponseEntity<List<MiniStatementResponse.Transaction>> getAccountMiniStatement(@RequestBody MiniStatementRequest miniStatementRequest) {
        List<MiniStatementResponse.Transaction> response = accountService.getMiniStatement(miniStatementRequest);
        return ResponseEntity.ok(response);
    }

// Test Generic List Response
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/list_currency")
    public ResponseEntity<List<GenericResponse>> getCurrencyList() {
        List<GenericResponse> response = accountService.getCurrencyList();
        return ResponseEntity.ok(response);
    }

//    @PostMapping("/list_non_loan_accounts")
//    public ResponseEntity<List<GenericResponse>> getNonLoanAccountList() {
//        List<GenericResponse> response = accountService.getNonLoanAccountList();
//        return ResponseEntity.ok(response);
//    }



}
