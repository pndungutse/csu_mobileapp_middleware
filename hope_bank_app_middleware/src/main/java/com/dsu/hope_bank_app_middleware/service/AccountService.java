package com.dsu.hope_bank_app_middleware.service;

import com.dsu.hope_bank_app_middleware.request.*;
import com.dsu.hope_bank_app_middleware.response.*;

import java.util.List;

public interface AccountService {
    AccountResponse getAccountInformation(AccountRequest accountRequest);

    AccountBalanceResponse.Result getAccountBalance(AccountBalanceRequest request);
    List<MiniStatementResponse.Transaction> getMiniStatement(MiniStatementRequest request);

    GenericResponse getSingleAccountInformation(GenericRequest genericRequest);

    List<GenericResponse> getCurrencyList();
}
