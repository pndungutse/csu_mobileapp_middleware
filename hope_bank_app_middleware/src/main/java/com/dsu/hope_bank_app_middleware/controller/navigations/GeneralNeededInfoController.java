package com.dsu.hope_bank_app_middleware.controller.navigations;

import com.dsu.hope_bank_app_middleware.request.navigations.BankRequest;
import com.dsu.hope_bank_app_middleware.request.navigations.GeneralNeededInfoRequest;
import com.dsu.hope_bank_app_middleware.response.navigations.GeneralNeededInfoResponse;
import com.dsu.hope_bank_app_middleware.service.GeneralNeededInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/navigation/general-utils")
@CrossOrigin(origins = "*")
public class GeneralNeededInfoController {

    @Autowired
    private GeneralNeededInfoService generalNeededInfoService;

    //    http://localhost:8080/api/v1/general-needed-info   POST
    @PostMapping
    public ResponseEntity<GeneralNeededInfoResponse> createGeneralNeededInfo(
            @RequestBody GeneralNeededInfoRequest request) {
        return generalNeededInfoService.createGeneralNeededInfo(request);
    }

    //    http://localhost:8080/api/v1/general-needed-info/{id}   GET
    @GetMapping("/{id}")
    public ResponseEntity<GeneralNeededInfoResponse> getGeneralNeededInfoById(@PathVariable String id) {
        return generalNeededInfoService.getGeneralNeededInfoById(id);
    }

    //    http://localhost:8080/api/v1/general-needed-info   GET
    @GetMapping
    public ResponseEntity<List<GeneralNeededInfoResponse>> getAllGeneralNeededInfo() {
        return generalNeededInfoService.getAllGeneralNeededInfo();
    }

    //    http://localhost:8080/api/v1/general-needed-info/by-bank/{bankId}   GET
    @GetMapping("/by-bank/{bankId}")
    public ResponseEntity<List<GeneralNeededInfoResponse>> getAllGeneralNeededInfoByBankId(
            @PathVariable String bankId) {
        return generalNeededInfoService.getAllGeneralNeededInfoByBankId(bankId);
    }

    @GetMapping("/by-bank")
    public ResponseEntity<GeneralNeededInfoResponse> getGeneralNeededInfoByBankName(@RequestBody BankRequest request) {
        return generalNeededInfoService.getGeneralNeededInfoByBankName(request);
    }

    //    http://localhost:8080/api/v1/general-needed-info/{id}   PUT
    @PutMapping("/{id}")
    public ResponseEntity<GeneralNeededInfoResponse> updateGeneralNeededInfo(
            @PathVariable String id,
            @RequestBody GeneralNeededInfoRequest request) {
        return generalNeededInfoService.updateGeneralNeededInfo(id, request);
    }

    //    http://localhost:8080/api/v1/general-needed-info/{id}   DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralNeededInfoResponse> deleteGeneralNeededInfo(@PathVariable String id) {
        return generalNeededInfoService.deleteGeneralNeededInfo(id);
    }
}
