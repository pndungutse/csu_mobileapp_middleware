package com.dsu.hope_bank_app_middleware.controller;

import com.dsu.hope_bank_app_middleware.request.BeneficiaryBankRequest;
import com.dsu.hope_bank_app_middleware.request.navigations.AssociateBankRequest;
import com.dsu.hope_bank_app_middleware.response.BeneficiaryBankResponse;
import com.dsu.hope_bank_app_middleware.response.GenericResponse;
import com.dsu.hope_bank_app_middleware.response.navigations.AssociateBankResponse;
import com.dsu.hope_bank_app_middleware.service.BeneficiaryBankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/beneficiary_bank")
public class BeneficiaryBankController {

    @Autowired
    private BeneficiaryBankService beneficiaryBankService;

    @PostMapping()
    public ResponseEntity<BeneficiaryBankResponse> createBeneficiaryBank(@RequestBody BeneficiaryBankRequest request) {
        return beneficiaryBankService.createBeneficiaryBank(request);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/list_beneficiary")
    public ResponseEntity<List<GenericResponse>> getBeneficiaryList() {
        List<GenericResponse> response = beneficiaryBankService.getBeneficiaryList();
        return ResponseEntity.ok(response);
    }
}
