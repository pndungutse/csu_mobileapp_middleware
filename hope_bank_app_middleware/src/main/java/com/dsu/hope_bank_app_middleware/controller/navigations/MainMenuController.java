package com.dsu.hope_bank_app_middleware.controller.navigations;

import com.dsu.hope_bank_app_middleware.request.navigations.MainMenuRequest;
import com.dsu.hope_bank_app_middleware.response.navigations.MainMenuAllResponse;
import com.dsu.hope_bank_app_middleware.response.navigations.MainMenuResponse;
import com.dsu.hope_bank_app_middleware.service.MainMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/navigation")
@CrossOrigin(origins = "*")
public class MainMenuController {
    @Autowired
    private MainMenuService mainMenuService;

    //    http://localhost:8080/api/v1/navigation/main_menus   POST
    @PostMapping("/main_menus")
    public ResponseEntity<MainMenuResponse> createMainMenu(@RequestBody MainMenuRequest request) {
        return mainMenuService.createMainMenu(request);
    }

    //    http://localhost:8080/api/v1/navigation/main_menus  GET
    @GetMapping("/main_menus")
    public ResponseEntity<List<MainMenuAllResponse>> getAllMainMenus() {
        return mainMenuService.getAllMainMenus();
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

    @GetMapping("/main_menus_by_bank_name/{bank_name}")
    public ResponseEntity<List<MainMenuResponse>> getAllMainMenusByBankName(
            @PathVariable String bank_name
    ) {
        return mainMenuService.getAllMainMenusByBankName(bank_name);
    }
}
