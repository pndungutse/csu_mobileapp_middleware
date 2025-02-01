package com.dsu.hope_bank_app_middleware.navigations.service;

import com.dsu.hope_bank_app_middleware.navigations.request.FormElementRequest;
import com.dsu.hope_bank_app_middleware.navigations.response.FormElementFullResponse;
import com.dsu.hope_bank_app_middleware.navigations.response.FormElementResponse;
import com.dsu.hope_bank_app_middleware.navigations.response.MainMenuResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface FormElementService {
//    FormElement createFormElement(String subMenuItemId, FormElement formElement);
//    FormElement updateFormElement(String formElementId, FormElement formElement);
//    void deleteFormElement(String formElementId);
//    FormElement getFormElementById(String formElementId);
//    List<FormElement> getAllFormElementsBySubMenuId(String subMenuItemId);

    ResponseEntity<FormElementResponse> createFormElement(String subMenuId, FormElementRequest request);
    ResponseEntity<FormElementResponse> getFormElementById(String id);
    ResponseEntity<List<FormElementResponse>> getAllFormElementsBySubMenuId(String subMenuId);
    ResponseEntity<FormElementResponse> updateFormElement(String id, FormElementRequest request);
    ResponseEntity<FormElementResponse> deleteFormElement(String id);
}
