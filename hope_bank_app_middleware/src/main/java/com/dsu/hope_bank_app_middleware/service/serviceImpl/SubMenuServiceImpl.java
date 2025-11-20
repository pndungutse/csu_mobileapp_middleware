package com.dsu.hope_bank_app_middleware.navigations.service.serviceImpl;

import com.dsu.hope_bank_app_middleware.exception.ResourceNotFoundException;
import com.dsu.hope_bank_app_middleware.exception.SuccessResponse;
import com.dsu.hope_bank_app_middleware.general_enumerations.ResponseType;
import com.dsu.hope_bank_app_middleware.navigations.entity.MainMenu;
import com.dsu.hope_bank_app_middleware.navigations.entity.SubMenu;
import com.dsu.hope_bank_app_middleware.navigations.repository.MainMenuRepository;
import com.dsu.hope_bank_app_middleware.navigations.repository.SubMenuRepository;
import com.dsu.hope_bank_app_middleware.request.navigations.SubMenuRequest;
import com.dsu.hope_bank_app_middleware.response.navigations.SubMenuResponse;
import com.dsu.hope_bank_app_middleware.navigations.service.SubMenuService;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubMenuServiceImpl implements SubMenuService {

    @Autowired
    private MainMenuRepository mainMenuRepository;

    @Autowired
    private SubMenuRepository subMenuRepository;

    @Override
    public ResponseEntity<SubMenuResponse> createSubMenu(String mainMenuId, SubMenuRequest request) {
        MainMenu mainMenu = mainMenuRepository.findById(mainMenuId)
                .orElseThrow(() -> new ResourceNotFoundException("Main Menu Item not found"));

        SubMenu subMenu = new SubMenu();
        subMenu.setSubMenuItemName(request.getSubMenuItemName());
        subMenu.setSubMenuItemDesc(request.getSubMenuItemDesc());
        subMenu.setSubMenuItemStatus(request.getSubMenuItemStatus());
        subMenu.setSubMenuCategory(request.getSubMenuCategory());
        subMenu.setIcon(request.getIcon());
        subMenu.setSubMenuItemAddedDate(new Date());
        subMenu.setMainMenu(mainMenu);

        SubMenu savedSubMenu = subMenuRepository.save(subMenu);

        var successResponse = SuccessResponse.builder()
                .type(ResponseType.success)
                .description("Sub Menu created successfully")
                .status(HttpStatus.OK)
                .build();

        SubMenuResponse subMenuResponse = mapToResponse(savedSubMenu);
        subMenuResponse.setSuccessResponse(successResponse);

        return new ResponseEntity<>(subMenuResponse, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<SubMenuResponse> getSubMenuById(String id) {
        SubMenu subMenu = subMenuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sub Menu not found"));

        var successResponse = SuccessResponse.builder()
                .type(ResponseType.success)
                .description("Sub Menu retrieved successfully")
                .status(HttpStatus.OK)
                .build();

        SubMenuResponse subMenuResponse = mapToResponse(subMenu);
        subMenuResponse.setSuccessResponse(successResponse);
        return new ResponseEntity<>(subMenuResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<SubMenuResponse>> getAllSubMenusByMenuId(String menuId) {
        System.out.println("Id: "+menuId);

        // Fetch all main menu items associated with the bank ID
        List<SubMenu> subMenus = subMenuRepository.findByMainMenu_Id(menuId);

        if (subMenus.isEmpty()) {
            throw new ResourceNotFoundException("No main menus found for the associated bank ID: " + menuId);
        }

        // Map each MainMenu entity to a MainMenuResponse object
        List<SubMenuResponse> subMenuResponses = subMenus.stream()
                .map(this::mapToResponse) // Reuse the mapping method
                .collect(Collectors.toList());

        // Create a success response object
        var successResponse = SuccessResponse.builder()
                .type(ResponseType.success)
                .description("Sub menus retrieved successfully for the main menu ID: " + menuId)
                .status(HttpStatus.OK)
                .build();

        // Attach the success response to each MainMenuResponse
        subMenuResponses.forEach(response -> response.setSuccessResponse(successResponse));

        // Return the list wrapped in a ResponseEntity
        return new ResponseEntity<>(subMenuResponses, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<SubMenuResponse> updateSubMenu(String id, SubMenuRequest request) {
        SubMenu subMenu = subMenuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sub Menu not found"));

        subMenu.setSubMenuItemName(request.getSubMenuItemName());
        subMenu.setSubMenuItemDesc(request.getSubMenuItemDesc());
        subMenu.setSubMenuItemStatus(request.getSubMenuItemStatus());
        subMenu.setIcon(request.getIcon());
        subMenu.setSubMenuCategory(request.getSubMenuCategory());
        subMenu.setSubMenuItemUpdatedDate(new Date());

        SubMenu updatedSubMenu = subMenuRepository.save(subMenu);

        var successResponse = SuccessResponse.builder()
                .type(ResponseType.success)
                .description("Sub Menu updated successfully")
                .status(HttpStatus.OK)
                .build();

        SubMenuResponse subMenuResponse = mapToResponse(updatedSubMenu);
        subMenuResponse.setSuccessResponse(successResponse);

        return new ResponseEntity<>(subMenuResponse, HttpStatus.OK);
    }

    private SubMenuResponse mapToResponse(SubMenu subMenu) {
        return SubMenuResponse.builder()
                .id(subMenu.getId())
                .subMenuItemName(subMenu.getSubMenuItemName())
                .subMenuItemDesc(subMenu.getSubMenuItemDesc())
                .subMenuItemStatus(subMenu.getSubMenuItemStatus())
                .icon(subMenu.getIcon())
                .subMenuCategory(subMenu.getSubMenuCategory())
                .subMenuItemAddedDate(subMenu.getSubMenuItemAddedDate())
                .subMenuItemUpdatedDate(subMenu.getSubMenuItemUpdatedDate())
                .mainMenuItemName(
                        subMenu.getMainMenu() != null ? subMenu.getMainMenu().getMenuItemName() : "N/A"
                )
                .build();
    }
}
