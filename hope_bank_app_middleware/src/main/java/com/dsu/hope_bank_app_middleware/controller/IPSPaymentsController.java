package com.dsu.hope_bank_app_middleware.controller;

import com.dsu.hope_bank_app_middleware.request.GenericRequest;
import com.dsu.hope_bank_app_middleware.request.ipsRequest.IPSPayQrRequest;
import com.dsu.hope_bank_app_middleware.request.ipsRequest.IPSTransferRequest;
import com.dsu.hope_bank_app_middleware.request.ipsRequest.IpsQrStartOfPaymentRequest;
import com.dsu.hope_bank_app_middleware.response.GenericDataResponse;
import com.dsu.hope_bank_app_middleware.response.GenericResponse;
import com.dsu.hope_bank_app_middleware.response.IPSResponse.IpsQrReadResponse;
import com.dsu.hope_bank_app_middleware.response.IPSResponse.IpsQrStartOfPaymentResponse;
import com.dsu.hope_bank_app_middleware.response.TransferResponse;
import com.dsu.hope_bank_app_middleware.service.IPSPaymentsService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/transfers/ips")
public class IPSPaymentsController {

    @Autowired
    private IPSPaymentsService ipsPaymentsService;

    // http://localhost:8080/api/v1/accounts/single_information
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/single_information")
    public ResponseEntity<GenericResponse> getIpsAccountInformation(@RequestBody GenericRequest genericRequest) {
        GenericResponse response = ipsPaymentsService.getIpsAccountInformation(genericRequest);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/transfer_to_other")
    public ResponseEntity<TransferResponse.Result> processTransferIpsOther(@RequestBody IPSTransferRequest ipsTransferRequest) {
        TransferResponse.Result response = ipsPaymentsService.processTransferIpsOther(ipsTransferRequest);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/transfer_to_ips_qr")
    public ResponseEntity<TransferResponse.Result> processTransferIpsPayQr(@RequestBody IPSPayQrRequest ipsPayQrRequest) {
        TransferResponse.Result response = ipsPaymentsService.processTransferIpsPayQr(ipsPayQrRequest);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/single_qr_information")
    public ResponseEntity<GenericResponse> getIpsQrInformation(@RequestBody GenericRequest genericRequest) {
        GenericResponse response = ipsPaymentsService.getIpsQrInformation(genericRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/qr_code_information")
    public ResponseEntity<GenericDataResponse<IpsQrReadResponse>> getIpsQrCodeInfo(@RequestBody GenericRequest genericRequest) {
        GenericDataResponse<IpsQrReadResponse> response = ipsPaymentsService.getIpsQrCodeInfo(genericRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/qr_start_of_payment")
    public ResponseEntity<GenericDataResponse<IpsQrStartOfPaymentResponse>> startIpsQrStartOfPayment(
            @RequestBody IpsQrStartOfPaymentRequest request) {
        GenericDataResponse<IpsQrStartOfPaymentResponse> response = ipsPaymentsService.startIpsQrStartOfPayment(request);
        return ResponseEntity.ok(response);
    }
}
