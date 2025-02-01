package com.dsu.hope_bank_app_middleware.bank_services.controller;

import com.dsu.hope_bank_app_middleware.bank_services.request.BankServiceRequest;
import com.dsu.hope_bank_app_middleware.bank_services.response.BankServiceFullResponse;
import com.dsu.hope_bank_app_middleware.bank_services.response.BankServiceResponse;
import com.dsu.hope_bank_app_middleware.bank_services.service.BankServiceService;
import com.dsu.hope_bank_app_middleware.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bankServices")
public class BankServiceController {

    @Autowired
    private BankServiceService bankServiceService;

    //    http://localhost:8080/api/bankServices   POST
    @PostMapping
    public ResponseEntity<BankServiceResponse> createBankService(@RequestBody BankServiceRequest request) {
        return bankServiceService.createBankService(request);
    }

    //    http://localhost:8080/api/bankServices/{id}  GET
    @GetMapping("/{id}")
    public ResponseEntity<BankServiceResponse> getBankServiceById(@PathVariable String id) {
        return bankServiceService.getBankServiceById(id);
    }

    //    http://localhost:8080/api/bankServices/{id} PUT
    @PutMapping("/{id}")
    public ResponseEntity<BankServiceResponse> updateBankService(@PathVariable String id, @RequestBody BankServiceRequest request) {
        return bankServiceService.updateBankService(id, request);
    }

    //    http://localhost:8080/api/bankServices  GET
    @GetMapping
    public ResponseEntity<BankServiceFullResponse> getAllBankService(
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir
    ) {
        return bankServiceService.getAllBankServices(pageNo, pageSize, sortBy, sortDir);
    }

}
