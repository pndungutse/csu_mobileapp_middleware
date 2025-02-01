package com.dsu.hope_bank_app_middleware.navigations.service.serviceImpl;

import com.dsu.hope_bank_app_middleware.exception.ResourceNotFoundException;
import com.dsu.hope_bank_app_middleware.exception.SuccessResponse;
import com.dsu.hope_bank_app_middleware.general_enumerations.ResponseType;
import com.dsu.hope_bank_app_middleware.navigations.entity.FormElement;
import com.dsu.hope_bank_app_middleware.navigations.entity.SubMenu;
import com.dsu.hope_bank_app_middleware.navigations.repository.FormElementRepository;
import com.dsu.hope_bank_app_middleware.navigations.repository.SubMenuRepository;
import com.dsu.hope_bank_app_middleware.navigations.request.FormElementRequest;
import com.dsu.hope_bank_app_middleware.navigations.response.FormElementFullResponse;
import com.dsu.hope_bank_app_middleware.navigations.response.FormElementResponse;
import com.dsu.hope_bank_app_middleware.navigations.response.SubMenuResponse;
import com.dsu.hope_bank_app_middleware.navigations.service.FormElementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FormElementServiceImpl implements FormElementService {

    @Autowired
    private SubMenuRepository subMenuRepository;

    @Autowired
    private FormElementRepository formElementRepository;

    @Override
    public ResponseEntity<FormElementResponse> createFormElement(String subMenuId, FormElementRequest request) {
        SubMenu subMenu = subMenuRepository.findById(subMenuId)
                .orElseThrow(() -> new ResourceNotFoundException("Sub Menu Item not found"));

        FormElement formElement = new FormElement();
        formElement.setFormElementName(request.getFormElementName());
        formElement.setFormElementDescription(request.getFormElementDescription());
        formElement.setFormElementLabel(request.getFormElementLabel());
        formElement.setFormElementPlaceHolder(request.getFormElementPlaceHolder());
        formElement.setFormElementType(request.getFormElementType());
        formElement.setFormElementValueType(request.getFormElementValueType());
        formElement.setFormElementStatus(request.getFormElementStatus());
        formElement.setFormElementAddedDate(new Date());
        formElement.setSubMenu(subMenu);

        FormElement savedFormElement = formElementRepository.save(formElement);

        var successResponse = SuccessResponse.builder()
                .type(ResponseType.success)
                .description("Sub Menu created successfully")
                .status(HttpStatus.OK)
                .build();

        FormElementResponse formElementResponse = mapToResponse(savedFormElement);
        formElementResponse.setSuccessResponse(successResponse);

        return new ResponseEntity<>(formElementResponse, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<FormElementResponse> getFormElementById(String id) {
        FormElement formElement = formElementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Form Element not found"));

        var successResponse = SuccessResponse.builder()
                .type(ResponseType.success)
                .description("Form element retrieved successfully")
                .status(HttpStatus.OK)
                .build();

        FormElementResponse formElementResponse = mapToResponse(formElement);
        formElementResponse.setSuccessResponse(successResponse);
        return new ResponseEntity<>(formElementResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<FormElementResponse>> getAllFormElementsBySubMenuId(String subMenuId) {
        System.out.println("Id: "+subMenuId);

        // Fetch all main menu items associated with the bank ID
        List<FormElement> formElements = formElementRepository.findBySubMenu_Id(subMenuId);

        if (formElements.isEmpty()) {
            throw new ResourceNotFoundException("No form elements found for the sub menu ID: " + subMenuId);
        }

        // Map each MainMenu entity to a MainMenuResponse object
        List<FormElementResponse> formElementResponses = formElements.stream()
                .map(this::mapToResponse) // Reuse the mapping method
                .collect(Collectors.toList());

        // Create a success response object
        var successResponse = SuccessResponse.builder()
                .type(ResponseType.success)
                .description("Form elements retrieved successfully for the sub menu ID: " + subMenuId)
                .status(HttpStatus.OK)
                .build();

        // Attach the success response to each MainMenuResponse
        formElementResponses.forEach(response -> response.setSuccessResponse(successResponse));

        // Return the list wrapped in a ResponseEntity
        return new ResponseEntity<>(formElementResponses, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<FormElementResponse> updateFormElement(String id, FormElementRequest request) {
        FormElement formElement = formElementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Form Element not found"));

        formElement.setFormElementName(request.getFormElementName());
        formElement.setFormElementDescription(request.getFormElementDescription());
        formElement.setFormElementLabel(request.getFormElementLabel());
        formElement.setFormElementPlaceHolder(request.getFormElementPlaceHolder());
        formElement.setFormElementType(request.getFormElementType());
        formElement.setFormElementValueType(request.getFormElementValueType());
        formElement.setFormElementStatus(request.getFormElementStatus());
        formElement.setFormElementUpdatedDate(new Date());

        FormElement updatedFormElement = formElementRepository.save(formElement);

        var successResponse = SuccessResponse.builder()
                .type(ResponseType.success)
                .description("Sub Menu updated successfully")
                .status(HttpStatus.OK)
                .build();

        FormElementResponse formElementResponse = mapToResponse(updatedFormElement);
        formElementResponse.setSuccessResponse(successResponse);

        return new ResponseEntity<>(formElementResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<FormElementResponse> deleteFormElement(String id) {
        FormElement formElement = formElementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Form Element not found"));

        formElementRepository.delete(formElement);
        var successResponse = SuccessResponse.builder()
                .type(ResponseType.success)
                .description("Form element deleted successfully")
                .status(HttpStatus.OK)
                .build();

        FormElementResponse formElementResponse = mapToResponse(formElement);
        formElementResponse.setSuccessResponse(successResponse);
        return new ResponseEntity<>(formElementResponse, HttpStatus.OK);
    }

    private FormElementResponse mapToResponse(FormElement formElement) {

        return FormElementResponse.builder()
                .id(formElement.getId())
                .formElementName(formElement.getFormElementName())
                .formElementDescription(formElement.getFormElementDescription())
                .formElementLabel(formElement.getFormElementLabel())
                .formElementPlaceHolder(formElement.getFormElementPlaceHolder())
                .formElementValueType(formElement.getFormElementValueType())
                .formElementType(formElement.getFormElementType())
                .formElementStatus(formElement.getFormElementStatus())
                .formElementAddedDate(formElement.getFormElementAddedDate())
                .formElementUpdatedDate(formElement.getFormElementUpdatedDate())
                .subMenuItemName(
                        formElement.getSubMenu() != null ? formElement.getSubMenu().getSubMenuItemName() : "N/A"
                )
                .build();
    }
}
