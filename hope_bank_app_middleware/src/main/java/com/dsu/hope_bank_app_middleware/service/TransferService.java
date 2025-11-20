package com.dsu.hope_bank_app_middleware.service;

import com.dsu.hope_bank_app_middleware.request.AirtimeTopUpRequest;
import com.dsu.hope_bank_app_middleware.request.BankToWalletTransferRequest;
import com.dsu.hope_bank_app_middleware.request.InternalTransferRequest;
import com.dsu.hope_bank_app_middleware.response.TransferResponse;

public interface TransferService {
    TransferResponse.Result processInternalTransfer(InternalTransferRequest request);
    TransferResponse.Result processBankToWalletTransfer(BankToWalletTransferRequest request);
    TransferResponse.Result processBAirtimeTopUp(AirtimeTopUpRequest request);

}
