package com.dsu.hope_bank_app_middleware.service;

import com.dsu.hope_bank_app_middleware.request.navigations.BankServiceRequest;
import com.dsu.hope_bank_app_middleware.response.navigations.BankServiceFullResponse;
import com.dsu.hope_bank_app_middleware.response.navigations.BankServiceResponse;
import org.springframework.http.ResponseEntity;

public interface BankServiceService {
    ResponseEntity<BankServiceResponse> createBankService(BankServiceRequest request);
    ResponseEntity<BankServiceResponse> getBankServiceById(String id);
    ResponseEntity<BankServiceFullResponse> getAllBankServices(int pageNo, int pageSize, String sortBy, String sortDir);
    ResponseEntity<BankServiceResponse> updateBankService(String id, BankServiceRequest request);
}
