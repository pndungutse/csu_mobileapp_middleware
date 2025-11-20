package com.dsu.hope_bank_app_middleware.accounts.service;

import com.dsu.hope_bank_app_middleware.request.AccountRequest;
import com.dsu.hope_bank_app_middleware.response.AccountResponse;

public interface AccountService {
    AccountResponse getAccountInformation(AccountRequest accountRequest);

}
