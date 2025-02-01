package com.dsu.hope_bank_app_middleware.navigations.controller;

import com.dsu.hope_bank_app_middleware.navigations.request.MainMenuRequest;
import com.dsu.hope_bank_app_middleware.navigations.response.MainMenuFullResponse;
import com.dsu.hope_bank_app_middleware.navigations.response.MainMenuResponse;
import com.dsu.hope_bank_app_middleware.navigations.service.MainMenuService;
import com.dsu.hope_bank_app_middleware.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/navigation")
public class MainMenuController {
    @Autowired
    private MainMenuService mainMenuService;

    //    http://localhost:8080/api/v1/navigation/main_menus   POST
    @PostMapping("/main_menus")
    public ResponseEntity<MainMenuResponse> createMainMenu(@RequestBody MainMenuRequest request) {
        return mainMenuService.createMainMenu(request);
    }

    //    http://localhost:8080/api/v1/navigation/main_menus/{id}  GET
    @GetMapping("/main_menus/{id}")
    public ResponseEntity<MainMenuResponse> getMainMenuById(@PathVariable String id) {
        return mainMenuService.getMainMenuById(id);
    }

    //    http://localhost:8080/api/v1/navigation/main_menus/{id}  GET
    @DeleteMapping("/main_menus/{id}")
    public ResponseEntity<MainMenuResponse> deleteMainMenuById(@PathVariable String id) {
        return mainMenuService.deleteMainMenu(id);
    }


    //    http://localhost:8080/api/v1/navigation/main_menus/{id} PUT
    @PutMapping("/main_menus/{id}")
    public ResponseEntity<MainMenuResponse> updateMainMenu(@PathVariable String id, @RequestBody MainMenuRequest request) {
        return mainMenuService.updateMainMenu(id, request);
    }

    //    http://localhost:8080/api/v1/navigation/main_menus_by_bank_id/{id} GET
    @GetMapping("/main_menus_by_bank_id/{id}")
    public ResponseEntity<List<MainMenuResponse>> getAllMainMenusByBankId(
            @PathVariable String id
    ) {
        return mainMenuService.getAllMainMenusByBankId(id);
    }
}
