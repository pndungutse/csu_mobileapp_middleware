package com.dsu.hope_bank_app_middleware.service;

import com.dsu.hope_bank_app_middleware.request.navigations.AssociateBankRequest;
import com.dsu.hope_bank_app_middleware.response.navigations.AssociateBankResponse;
import com.dsu.hope_bank_app_middleware.response.navigations.NavigationsResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AssociateBankService {
    ResponseEntity<AssociateBankResponse> createAssociateBank(AssociateBankRequest request);
    ResponseEntity<AssociateBankResponse> getAssociateBankById(String id);
    ResponseEntity<List<AssociateBankResponse>> getAllAssociateBanks();
    ResponseEntity<AssociateBankResponse> updateAssociateBank(String id, AssociateBankRequest request);
    ResponseEntity<AssociateBankResponse> deleteAssociateBank(String id);

    ResponseEntity<NavigationsResponse> getAllNavigationResponse(String bankId);
}
