package com.dsu.hope_bank_app_middleware.service;

import com.dsu.hope_bank_app_middleware.request.navigations.BankRequest;
import com.dsu.hope_bank_app_middleware.request.navigations.GeneralNeededInfoRequest;
import com.dsu.hope_bank_app_middleware.response.navigations.GeneralNeededInfoResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface GeneralNeededInfoService {
    ResponseEntity<GeneralNeededInfoResponse> createGeneralNeededInfo(GeneralNeededInfoRequest request);
    ResponseEntity<GeneralNeededInfoResponse> getGeneralNeededInfoById(String id);
    ResponseEntity<List<GeneralNeededInfoResponse>> getAllGeneralNeededInfo();
    ResponseEntity<List<GeneralNeededInfoResponse>> getAllGeneralNeededInfoByBankId(String bankId);
    ResponseEntity<GeneralNeededInfoResponse> updateGeneralNeededInfo(String id, GeneralNeededInfoRequest request);
    ResponseEntity<GeneralNeededInfoResponse> deleteGeneralNeededInfo(String id);

    ResponseEntity<GeneralNeededInfoResponse> getGeneralNeededInfoByBankName(BankRequest request);
}
