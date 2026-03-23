package com.dsu.hope_bank_app_middleware.service;

import com.dsu.hope_bank_app_middleware.request.BeneficiaryBankRequest;
import com.dsu.hope_bank_app_middleware.response.BeneficiaryBankResponse;
import com.dsu.hope_bank_app_middleware.response.GenericResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BeneficiaryBankService {
    ResponseEntity<BeneficiaryBankResponse> createBeneficiaryBank(BeneficiaryBankRequest request);
    List<GenericResponse> getBeneficiaryList();
}
