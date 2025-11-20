package com.dsu.hope_bank_app_middleware.navigations.controller;

import com.dsu.hope_bank_app_middleware.navigations.request.FormElementRequest;
import com.dsu.hope_bank_app_middleware.navigations.response.FormElementFullResponse;
import com.dsu.hope_bank_app_middleware.navigations.response.FormElementResponse;
import com.dsu.hope_bank_app_middleware.navigations.service.FormElementService;
import com.dsu.hope_bank_app_middleware.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/navigation")
public class FormElementController {

    @Autowired
    private FormElementService formElementService;

    //    http://localhost:8080/api/v1/navigation/form_elements   POST
    @PostMapping("/form_elements/{id}")
    public ResponseEntity<FormElementResponse> createFormElement(@PathVariable String id, @RequestBody FormElementRequest request) {
        return formElementService.createFormElement(id, request);
    }

    //    http://localhost:8080/api/v1/navigation/form_elements/{id}  GET
    @GetMapping("/form_elements/{id}")
    public ResponseEntity<FormElementResponse> getFormElementById(@PathVariable String id) {
        return formElementService.getFormElementById(id);
    }

    //    http://localhost:8080/api/v1/navigation/form_elements/{id}  GET
    @DeleteMapping("/form_elements/{id}")
    public ResponseEntity<FormElementResponse> deleteFormElementById(@PathVariable String id) {
        return formElementService.deleteFormElement(id);
    }

    //    http://localhost:8080/api/v1/navigation/form_elements/{id} PUT
    @PutMapping("/form_elements/{id}")
    public ResponseEntity<FormElementResponse> updateFormElement(@PathVariable String id, @RequestBody FormElementRequest request) {
        return formElementService.updateFormElement(id, request);
    }

    //    http://localhost:8080/api/v1/navigation/form_elements_by_sub_menu_id/{id} GET
    @GetMapping("/form_elements_by_sub_menu_id/{id}")
    public ResponseEntity<List<FormElementResponse>> getAllFormElementsByBankId(
            @PathVariable String id
    ) {
        return formElementService.getAllFormElementsBySubMenuId(id);
    }
}
