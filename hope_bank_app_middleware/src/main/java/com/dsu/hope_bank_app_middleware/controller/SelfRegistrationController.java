package com.dsu.hope_bank_app_middleware.controller;

import com.dsu.hope_bank_app_middleware.request.LoanRepaymentAccountRequest;
import com.dsu.hope_bank_app_middleware.request.SelfRegistrationRequest;
import com.dsu.hope_bank_app_middleware.response.LoanRepaymentResponse;
import com.dsu.hope_bank_app_middleware.response.SelfRegistrationResponse;
import com.dsu.hope_bank_app_middleware.service.SelfRegistrationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/registration")
public class SelfRegistrationController {
    private final SelfRegistrationService selfRegistrationService;

    @PostMapping("/self_registration")
    public ResponseEntity<SelfRegistrationResponse.Result> makeSelfRegistration(@RequestBody SelfRegistrationRequest selfRegistrationRequest) {
        SelfRegistrationResponse.Result response = selfRegistrationService.makeSelfRegistration(selfRegistrationRequest);
        return ResponseEntity.ok(response);
    }
}
