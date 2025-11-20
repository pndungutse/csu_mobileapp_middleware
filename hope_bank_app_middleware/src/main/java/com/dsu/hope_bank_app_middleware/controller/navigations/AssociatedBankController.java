package com.dsu.hope_bank_app_middleware.navigations.controller;

import com.dsu.hope_bank_app_middleware.navigations.request.AssociateBankRequest;
import com.dsu.hope_bank_app_middleware.navigations.response.AssociateBankFullResponse;
import com.dsu.hope_bank_app_middleware.navigations.response.AssociateBankResponse;
import com.dsu.hope_bank_app_middleware.navigations.response.NavigationsResponse;
import com.dsu.hope_bank_app_middleware.navigations.service.AssociateBankService;
import com.dsu.hope_bank_app_middleware.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/navigation")
public class AssociatedBankController {

    @Autowired
    private AssociateBankService associateBankService;

    //    http://localhost:8080/api/v1/navigation/associated_banks  POST
    @PostMapping("/associated_banks")
    public ResponseEntity<AssociateBankResponse> createAssociateBank(@RequestBody AssociateBankRequest request) {
        return associateBankService.createAssociateBank(request);
    }

    //    http://localhost:8080/api/v1/navigation/associated_banks/{id}  GET
    @GetMapping("/associated_banks/{id}")
    public ResponseEntity<AssociateBankResponse> getAssociateBankById(@PathVariable String id) {
        return associateBankService.getAssociateBankById(id);
    }

    //    http://localhost:8080/api/v1/navigation/associated_banks/{id} PUT
    @PutMapping("/associated_banks/{id}")
    public ResponseEntity<AssociateBankResponse> updateAssociateBank(@PathVariable String id, @RequestBody AssociateBankRequest request) {
        return associateBankService.updateAssociateBank(id, request);
    }

    //    http://localhost:8080/api/v1/navigation/associated_banks  GET
    @GetMapping("/associated_banks")
    public ResponseEntity<List<AssociateBankResponse>> getAllAssociateBank() {
        return associateBankService.getAllAssociateBanks();
    }

    //    http://localhost:8080/api/v1/navigation/navigations
    @GetMapping("/navigations/{id}")
    public ResponseEntity<NavigationsResponse> getNavigations(@PathVariable String id) {
        return associateBankService.getAllNavigationResponse(id);
    }
}
