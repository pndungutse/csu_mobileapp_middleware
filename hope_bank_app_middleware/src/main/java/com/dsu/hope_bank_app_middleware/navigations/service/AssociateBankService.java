package com.dsu.hope_bank_app_middleware.navigations.service;

import com.dsu.hope_bank_app_middleware.navigations.request.AssociateBankRequest;
import com.dsu.hope_bank_app_middleware.navigations.response.AssociateBankFullResponse;
import com.dsu.hope_bank_app_middleware.navigations.response.AssociateBankResponse;
import com.dsu.hope_bank_app_middleware.navigations.response.NavigationsResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AssociateBankService {
    ResponseEntity<AssociateBankResponse> createAssociateBank(AssociateBankRequest request);
    ResponseEntity<AssociateBankResponse> getAssociateBankById(String id);
    ResponseEntity<List<AssociateBankResponse>> getAllAssociateBanks();
    ResponseEntity<AssociateBankResponse> updateAssociateBank(String id, AssociateBankRequest request);

    ResponseEntity<NavigationsResponse> getAllNavigationResponse(String bankId);
}
