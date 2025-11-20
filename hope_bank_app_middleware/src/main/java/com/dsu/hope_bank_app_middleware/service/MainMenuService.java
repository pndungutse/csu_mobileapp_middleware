package com.dsu.hope_bank_app_middleware.service;

import com.dsu.hope_bank_app_middleware.request.navigations.MainMenuRequest;
import com.dsu.hope_bank_app_middleware.response.navigations.MainMenuAllResponse;
import com.dsu.hope_bank_app_middleware.response.navigations.MainMenuResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface MainMenuService {

    ResponseEntity<MainMenuResponse> createMainMenu(MainMenuRequest request);
    ResponseEntity<MainMenuResponse> getMainMenuById(String id);
    ResponseEntity<List<MainMenuResponse>> getAllMainMenusByBankId(String associatedBankId);
    ResponseEntity<MainMenuResponse> updateMainMenu(String id, MainMenuRequest request);
    ResponseEntity<MainMenuResponse> deleteMainMenu(String id);

    ResponseEntity<List<MainMenuAllResponse>> getAllMainMenus();
}
