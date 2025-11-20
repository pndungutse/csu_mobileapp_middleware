package com.dsu.hope_bank_app_middleware.controller.navigations;

import com.dsu.hope_bank_app_middleware.request.navigations.AssociateBankRequest;
import com.dsu.hope_bank_app_middleware.request.navigations.IconRequest;
import com.dsu.hope_bank_app_middleware.request.navigations.SubMenuRequest;
import com.dsu.hope_bank_app_middleware.response.navigations.AssociateBankResponse;
import com.dsu.hope_bank_app_middleware.response.navigations.IconResponse;
import com.dsu.hope_bank_app_middleware.response.navigations.SubMenuResponse;
import com.dsu.hope_bank_app_middleware.service.IconService;
import com.dsu.hope_bank_app_middleware.service.SubMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/icons")
public class IconController {
    @Autowired
    private IconService iconService;

    @PostMapping("")
    public ResponseEntity<IconResponse> createIcon(@RequestBody IconRequest request) {
        return iconService.createIcon(request);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IconResponse> getIconById(@PathVariable String id) {
        return iconService.getIconById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IconResponse> updateIconById(@PathVariable String id, @RequestBody IconRequest request) {
        return iconService.updateIconById(id, request);
    }

    @GetMapping("")
    public ResponseEntity<List<IconResponse>> getAllIconItems() {
        return iconService.getAllIconItems();
    }

}
