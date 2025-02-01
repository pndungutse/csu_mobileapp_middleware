package com.dsu.hope_bank_app_middleware.navigations.controller;

import com.dsu.hope_bank_app_middleware.navigations.request.SubMenuRequest;
import com.dsu.hope_bank_app_middleware.navigations.response.SubMenuFullResponse;
import com.dsu.hope_bank_app_middleware.navigations.response.SubMenuResponse;
import com.dsu.hope_bank_app_middleware.navigations.service.SubMenuService;
import com.dsu.hope_bank_app_middleware.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/navigation")
public class SubMenuController {
    @Autowired
    private SubMenuService subMenuService;

    //    http://localhost:8080/api/v1/navigation/sub_menus   POST
    @PostMapping("/sub_menus/{id}")
    public ResponseEntity<SubMenuResponse> createSubMenu(@PathVariable String id, @RequestBody SubMenuRequest request) {
        return subMenuService.createSubMenu(id, request);
    }

    //    http://localhost:8080/api/v1/navigation/sub_menus/{id}  GET
    @GetMapping("/sub_menus/{id}")
    public ResponseEntity<SubMenuResponse> getSubMenuById(@PathVariable String id) {
        return subMenuService.getSubMenuById(id);
    }

    //    http://localhost:8080/api/v1/navigation/sub_menus/{id} PUT
    @PutMapping("/sub_menus/{id}")
    public ResponseEntity<SubMenuResponse> updateSubMenu(@PathVariable String id, @RequestBody SubMenuRequest request) {
        return subMenuService.updateSubMenu(id, request);
    }

    //    http://localhost:8080/api/v1/navigation/sub_menus_by_bank_id/{id} GET
    @GetMapping("/sub_menus_by_main_menu_id/{id}")
    public ResponseEntity<List<SubMenuResponse>> getAllSubMenusByBankId(
            @PathVariable String id
    ) {
        return subMenuService.getAllSubMenusByMenuId(id);
    }
}
