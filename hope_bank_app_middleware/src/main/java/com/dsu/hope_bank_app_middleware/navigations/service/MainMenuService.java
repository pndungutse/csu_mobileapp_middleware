package com.dsu.hope_bank_app_middleware.navigations.service;

import com.dsu.hope_bank_app_middleware.navigations.request.BankRequest;
import com.dsu.hope_bank_app_middleware.navigations.request.MainMenuRequest;
import com.dsu.hope_bank_app_middleware.navigations.response.MainMenuFullResponse;
import com.dsu.hope_bank_app_middleware.navigations.response.MainMenuResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface MainMenuService {

    ResponseEntity<MainMenuResponse> createMainMenu(MainMenuRequest request);
    ResponseEntity<MainMenuResponse> getMainMenuById(String id);
    ResponseEntity<List<MainMenuResponse>> getAllMainMenusByBankId(String associatedBankId);
    ResponseEntity<MainMenuResponse> updateMainMenu(String id, MainMenuRequest request);
    ResponseEntity<MainMenuResponse> deleteMainMenu(String id);
}
