package com.dsu.hope_bank_app_middleware.controller.navigations;

import com.dsu.hope_bank_app_middleware.request.navigations.AssociateBankRequest;
import com.dsu.hope_bank_app_middleware.response.navigations.AssociateBankResponse;
import com.dsu.hope_bank_app_middleware.response.navigations.NavigationsResponse;
import com.dsu.hope_bank_app_middleware.service.AssociateBankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/navigation")
@CrossOrigin(origins = "*")
public class AssociatedBankController {

    @Autowired
    private AssociateBankService associateBankService;

    //    http://localhost:8080/api/v1/navigation/associated_banks  POST
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/associated_banks")
    public ResponseEntity<AssociateBankResponse> createAssociateBank(@RequestBody AssociateBankRequest request) {
        return associateBankService.createAssociateBank(request);
    }

    //    http://localhost:8080/api/v1/navigation/associated_banks/{id}  GET
    @GetMapping("/associated_banks/{id}")
    public ResponseEntity<AssociateBankResponse> getAssociateBankById(@PathVariable String id) {
        return associateBankService.getAssociateBankById(id);
    }

    @GetMapping("/associated_banks/by_name/{bank_name}")
    public ResponseEntity<AssociateBankResponse> getAssociateBankByBankName(@PathVariable String bank_name) {
        return associateBankService.getAssociateBankByBankName(bank_name);
    }

    //    http://localhost:8080/api/v1/navigation/associated_banks/{id} PUT
    @PutMapping("/associated_banks/{id}")
    public ResponseEntity<AssociateBankResponse> updateAssociateBank(@PathVariable String id, @RequestBody AssociateBankRequest request) {
        return associateBankService.updateAssociateBank(id, request);
    }

    //    http://localhost:8080/api/v1/navigation/associated_banks/{id} DELETE
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/associated_banks/{id}")
    public ResponseEntity<AssociateBankResponse> deleteAssociateBank(@PathVariable String id) {
        return associateBankService.deleteAssociateBank(id);
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
