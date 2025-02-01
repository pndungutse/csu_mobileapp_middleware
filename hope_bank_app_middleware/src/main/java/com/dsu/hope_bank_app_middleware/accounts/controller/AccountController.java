package com.dsu.hope_bank_app_middleware.accounts.controller;

import com.dsu.hope_bank_app_middleware.accounts.request.AccountRequest;
import com.dsu.hope_bank_app_middleware.accounts.response.AccountResponse;
import com.dsu.hope_bank_app_middleware.accounts.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

//    http://localhost:8080/api/v1/accounts/information
    @PostMapping("/information")
    public ResponseEntity<AccountResponse> getAccountInformation(@RequestBody AccountRequest accountRequest) {
        AccountResponse response = accountService.getAccountInformation(accountRequest);
        return ResponseEntity.ok(response);
    }


}
