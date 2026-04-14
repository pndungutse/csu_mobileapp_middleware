package com.dsu.hope_bank_app_middleware.service.serviceImpl;

import com.dsu.hope_bank_app_middleware.entity.navigations.AssociateBank;
import com.dsu.hope_bank_app_middleware.entity.navigations.MainMenu;
import com.dsu.hope_bank_app_middleware.exception.ResourceNotFoundException;
import com.dsu.hope_bank_app_middleware.exception.SuccessResponse;
import com.dsu.hope_bank_app_middleware.enumeration.ResponseType;
import com.dsu.hope_bank_app_middleware.entity.navigations.SubMenu;
import com.dsu.hope_bank_app_middleware.repository.AssociateBankRepository;
import com.dsu.hope_bank_app_middleware.repository.SubMenuRepository;
import com.dsu.hope_bank_app_middleware.request.navigations.SubMenuRequest;
import com.dsu.hope_bank_app_middleware.response.navigations.MainMenuResponse;
import com.dsu.hope_bank_app_middleware.response.navigations.SubMenuResponse;
import com.dsu.hope_bank_app_middleware.service.SubMenuService;
import com.dsu.hope_bank_app_middleware.utils.Mappings;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubMenuServiceImpl implements SubMenuService {

    @Autowired
    private AssociateBankRepository associateBankRepository;

    @Autowired
    private SubMenuRepository subMenuRepository;

    private final Mappings mappings;

    @Autowired
    public SubMenuServiceImpl(Mappings mappings) {
        this.mappings = mappings;
    }

    @Override
    public ResponseEntity<SubMenuResponse> createSubMenu(SubMenuRequest request) {
        System.out.println("Request: "+request);
        
        // Validate that associate bank exists
        AssociateBank associateBank = associateBankRepository.findByAssociateBankName(request.getAssociateBank())
                .orElseThrow(() -> new ResourceNotFoundException("Associate bank not found with bank name: " + request.getAssociateBank()));

        // Create SubMenu entity
        SubMenu subMenu = new SubMenu();
        subMenu.setSubMenuItemName(request.getSubMenuItemName());
        subMenu.setSubMenuItemDesc(request.getSubMenuItemDesc());
        subMenu.setSubMenuItemStatus(request.getSubMenuItemStatus());
        subMenu.setSubMenuCategory(request.getSubMenuCategory());
        subMenu.setIcon(request.getIcon());
        subMenu.setSubMenuActionUrl(request.getSubMenuActionUrl());
        subMenu.setSubMenuBelongToMenu(request.getSubMenuBelongToMenu());
        subMenu.setServiceWaitResponse(request.getServiceWaitResponse());
        if (request.getSubMenuDisplayOrder() != null) {
            subMenu.setSubMenuDisplayOrder(request.getSubMenuDisplayOrder());
        }
        subMenu.setBank(associateBank);
        subMenu.setSubMenuItemAddedDate(new Date());

        // Save SubMenu with bank relationship
        SubMenu savedSubMenu = subMenuRepository.save(subMenu);

        SuccessResponse successResponse = SuccessResponse.builder()
                .type(ResponseType.success)
                .description("Sub Menu created successfully")
                .status(HttpStatus.CREATED)
                .build();

        SubMenuResponse subMenuResponse = mappings.mapToSubMenuResponse(savedSubMenu);
        subMenuResponse.setSuccessResponse(successResponse);

        return new ResponseEntity<>(subMenuResponse, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<SubMenuResponse> getSubMenuById(String id) {
        SubMenu subMenu = subMenuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sub Menu not found"));

        SuccessResponse successResponse = SuccessResponse.builder()
                .type(ResponseType.success)
                .description("Sub Menu retrieved successfully")
                .status(HttpStatus.OK)
                .build();

        SubMenuResponse subMenuResponse = mappings.mapToSubmenuResponse(subMenu);
        subMenuResponse.setSuccessResponse(successResponse);
        return new ResponseEntity<>(subMenuResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<SubMenuResponse>> getAllSubMenusByBankId(String associateBankId) {
        System.out.println("Associate bank id: "+associateBankId);

        // Validate bank exists
        AssociateBank associateBank = associateBankRepository.findById(associateBankId)
                .orElseThrow(() -> new ResourceNotFoundException("Associate bank not found"));

        // Fetch all submenus associated with the bank ID using direct relationship
        List<SubMenu> subMenus = subMenuRepository.findByBank_Id(associateBankId);

        if (subMenus.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        // Map SubMenus to responses
        List<SubMenuResponse> subMenuResponses = subMenus.stream()
                .map(mappings::mapToSubmenuResponse)
                .collect(Collectors.toList());

        // Create a success response object
        SuccessResponse successResponse = SuccessResponse.builder()
                .type(ResponseType.success)
                .description("Sub menus retrieved successfully for the associate bank ID: " + associateBankId)
                .status(HttpStatus.OK)
                .build();

        // Attach the success response to each MainMenuResponse
        subMenuResponses.forEach(response -> response.setSuccessResponse(successResponse));

        // Return the list wrapped in a ResponseEntity
        return new ResponseEntity<>(subMenuResponses, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<SubMenuResponse>> getAllSubMenus() {
        // Fetch all submenus associated with the bank ID using direct relationship
        List<SubMenu> subMenus = subMenuRepository.findAll();

        if (subMenus.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        // Map SubMenus to responses
        List<SubMenuResponse> subMenuResponses = subMenus.stream()
                .map(mappings::mapToSubmenuResponse)
                .collect(Collectors.toList());

        // Create a success response object
        SuccessResponse successResponse = SuccessResponse.builder()
                .type(ResponseType.success)
                .description("Sub menus retrieved successfully")
                .status(HttpStatus.OK)
                .build();

        // Attach the success response to each MainMenuResponse
        subMenuResponses.forEach(response -> response.setSuccessResponse(successResponse));

        // Return the list wrapped in a ResponseEntity
        return new ResponseEntity<>(subMenuResponses, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<SubMenuResponse> deleteSubmenu(String id) {
        SubMenu subMenu = subMenuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SubMenu not found"));

        subMenuRepository.delete(subMenu);

        var successResponse = SuccessResponse.builder()
                .type(ResponseType.success)
                .description("MainMenu deleted successfully")
                .status(HttpStatus.OK)
                .build();

        SubMenuResponse subMenuResponse = mappings.mapToSubMenuResponse(subMenu);
        subMenuResponse.setSuccessResponse(successResponse);
        return new ResponseEntity<>(subMenuResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<SubMenuResponse> updateSubMenu(String id, SubMenuRequest request) {
        System.out.println("Update SubMenu Request: "+request);
        SubMenu subMenu = subMenuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sub Menu not found"));

        subMenu.setSubMenuItemName(request.getSubMenuItemName());
        subMenu.setSubMenuItemDesc(request.getSubMenuItemDesc());
        subMenu.setSubMenuItemStatus(request.getSubMenuItemStatus());
        subMenu.setIcon(request.getIcon());
        subMenu.setSubMenuCategory(request.getSubMenuCategory());
        subMenu.setSubMenuBelongToMenu(request.getSubMenuBelongToMenu());
        subMenu.setSubMenuActionUrl(request.getSubMenuActionUrl());
        subMenu.setServiceWaitResponse(request.getServiceWaitResponse());
        
        // Update display order if provided
        if (request.getSubMenuDisplayOrder() != null) {
            subMenu.setSubMenuDisplayOrder(request.getSubMenuDisplayOrder());
        }
        
        subMenu.setSubMenuItemUpdatedDate(new Date());

        SubMenu updatedSubMenu = subMenuRepository.save(subMenu);
        System.out.println("Submenu to update: "+updatedSubMenu);

        SuccessResponse successResponse = SuccessResponse.builder()
                .type(ResponseType.success)
                .description("Sub Menu updated successfully")
                .status(HttpStatus.OK)
                .build();


        SubMenuResponse subMenuResponse = mappings.mapToSubmenuResponse(updatedSubMenu);

        subMenuResponse.setSuccessResponse(successResponse);

        System.out.println("Response: "+subMenuResponse);

        return new ResponseEntity<>(subMenuResponse, HttpStatus.OK);
    }


}
