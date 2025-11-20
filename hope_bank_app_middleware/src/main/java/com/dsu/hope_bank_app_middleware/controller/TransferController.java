package com.dsu.hope_bank_app_middleware.controller;

import com.dsu.hope_bank_app_middleware.request.AirtimeTopUpRequest;
import com.dsu.hope_bank_app_middleware.request.BankToWalletTransferRequest;
import com.dsu.hope_bank_app_middleware.request.InternalTransferRequest;
import com.dsu.hope_bank_app_middleware.request.LoanInfoRequest;
import com.dsu.hope_bank_app_middleware.response.LoanBalanceResponse;
import com.dsu.hope_bank_app_middleware.response.TransferResponse;
import com.dsu.hope_bank_app_middleware.service.TransferService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/transfers")
public class TransferController {
    private final TransferService transferService;

    @PostMapping("/internal_transfer")
    public ResponseEntity<TransferResponse.Result> processInternalTransfer(@RequestBody InternalTransferRequest internalTransferRequest) {
        TransferResponse.Result response = transferService.processInternalTransfer(internalTransferRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/bank_to_wallet_transfer")
    public ResponseEntity<TransferResponse.Result> processBankToWalletTransfer(@RequestBody BankToWalletTransferRequest bankToWalletTransferRequest) {
        TransferResponse.Result response = transferService.processBankToWalletTransfer(bankToWalletTransferRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/airtime_top_up")
    public ResponseEntity<TransferResponse.Result> processBAirtimeTopUp(@RequestBody AirtimeTopUpRequest airtimeTopUpRequest) {
        TransferResponse.Result response = transferService.processBAirtimeTopUp(airtimeTopUpRequest);
        return ResponseEntity.ok(response);
    }
}
