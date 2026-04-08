package com.dsu.hope_bank_app_middleware.controller.navigations;

import com.dsu.hope_bank_app_middleware.request.navigations.SubMenuRequest;
import com.dsu.hope_bank_app_middleware.response.navigations.SubMenuResponse;
import com.dsu.hope_bank_app_middleware.service.SubMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/navigation")
@CrossOrigin(origins = "*")
public class SubMenuController {
    @Autowired
    private SubMenuService subMenuService;

    //    http://localhost:8080/api/v1/navigation/sub_menus/{id}   POST
    //    Note: Creates SubMenu and AssociateBankSubMenu relationship automatically
    //    You can pass associate_bank_id in request body or use path parameter {id}
    @PostMapping("/sub_menus")
    public ResponseEntity<SubMenuResponse> createSubMenu(@RequestBody SubMenuRequest request) {
        return subMenuService.createSubMenu(request);
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
    @GetMapping("/sub_menus_by_associate_bank_id/{id}")
    public ResponseEntity<List<SubMenuResponse>> getAllSubMenusByBankId(
            @PathVariable String id
    ) {
        return subMenuService.getAllSubMenusByBankId(id);
    }

    @GetMapping("/sub_menus_by_associate_bank_name/{bank_name}")
    public ResponseEntity<List<SubMenuResponse>> getAllSubMenusByBankName(
            @PathVariable String bank_name
    ) {
        return subMenuService.getAllSubMenusByBankName(bank_name);
    }

    //    http://localhost:8080/api/v1/navigation/sub_menus_by_bank_id/{id} GET
    @GetMapping("/sub_menus")
    public ResponseEntity<List<SubMenuResponse>> getAllSubMenus() {
        return subMenuService.getAllSubMenus();
    }

    @DeleteMapping("/sub_menus/{id}")
    public ResponseEntity<SubMenuResponse> deleteSubmenu(@PathVariable String id) {
        return subMenuService.deleteSubmenu(id);
    }
}
