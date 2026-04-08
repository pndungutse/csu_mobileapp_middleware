package com.dsu.hope_bank_app_middleware.service.serviceImpl;

import com.dsu.hope_bank_app_middleware.exception.ResourceNotFoundException;
import com.dsu.hope_bank_app_middleware.exception.SuccessResponse;
import com.dsu.hope_bank_app_middleware.enumeration.ResponseType;
import com.dsu.hope_bank_app_middleware.entity.navigations.AssociateBank;
import com.dsu.hope_bank_app_middleware.entity.navigations.MainMenu;
import com.dsu.hope_bank_app_middleware.repository.AssociateBankRepository;
import com.dsu.hope_bank_app_middleware.repository.MainMenuRepository;
import com.dsu.hope_bank_app_middleware.repository.SubMenuRepository;
import com.dsu.hope_bank_app_middleware.request.navigations.MainMenuRequest;
import com.dsu.hope_bank_app_middleware.response.navigations.MainMenuAllResponse;
import com.dsu.hope_bank_app_middleware.response.navigations.MainMenuResponse;
import com.dsu.hope_bank_app_middleware.response.navigations.SubMenuResponse;
import com.dsu.hope_bank_app_middleware.service.MainMenuService;
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
public class MainMenuServiceImpl implements MainMenuService {

    @Autowired
    private MainMenuRepository mainMenuRepository;
    
    @Autowired
    private AssociateBankRepository associateBankRepository;
    
    @Autowired
    private SubMenuRepository subMenuRepository;
    
    @Autowired
    private Mappings mappings;

    @Override
    public ResponseEntity<MainMenuResponse> createMainMenu(MainMenuRequest request) {
        System.out.println("Request: "+request);

        AssociateBank associateBank = associateBankRepository.findByAssociateBankName(request.getAssociateBankName())
                .orElseThrow(() -> new ResourceNotFoundException("Associate Bank not found"));

        MainMenu mainMenu = new MainMenu();
        mainMenu.setMenuItemName(request.getMenuItemName());
        mainMenu.setMenuItemDesc(request.getMenuItemDesc());
        mainMenu.setMenuItemStatus(request.getMenuItemStatus());
        mainMenu.setIcon(request.getIcon());
        mainMenu.setMenuItemAddedDate(new Date());
        mainMenu.setBank(associateBank);

        MainMenu savedMainMenu = mainMenuRepository.save(mainMenu);

        var successResponse = SuccessResponse.builder()
                .type(ResponseType.success)
                .description("MainMenu created successfully")
                .status(HttpStatus.OK)
                .build();

        MainMenuResponse mainMenuResponse = mapToResponse(savedMainMenu);
        mainMenuResponse.setSuccessResponse(successResponse);

        return new ResponseEntity<>(mainMenuResponse, HttpStatus.CREATED);


    }

    @Override
    public ResponseEntity<MainMenuResponse> getMainMenuById(String id) {

        MainMenu mainMenu = mainMenuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MainMenu not found"));

        var successResponse = SuccessResponse.builder()
                .type(ResponseType.success)
                .description("MainMenu retrieved successfully")
                .status(HttpStatus.OK)
                .build();

        MainMenuResponse mainMenuResponse = mapToResponse(mainMenu);
        mainMenuResponse.setSuccessResponse(successResponse);
        return new ResponseEntity<>(mainMenuResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<MainMenuResponse>> getAllMainMenusByBankId(String associatedBankId) {
        // Fetch all main menu items associated with the bank ID
        List<MainMenu> mainMenus = mainMenuRepository.findByBank_Id(associatedBankId);

        // If no main menus found, return an empty list
        if (mainMenus.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        // Map each MainMenu entity to a MainMenuResponse object
        List<MainMenuResponse> mainMenuResponses = mainMenus.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        // Create a success response object
        var successResponse = SuccessResponse.builder()
                .type(ResponseType.success)
                .description("Main menus retrieved successfully for the associated bank ID: " + associatedBankId)
                .status(HttpStatus.OK)
                .build();

        // Attach the success response to each MainMenuResponse
        mainMenuResponses.forEach(response -> response.setSuccessResponse(successResponse));

        // Return the list wrapped in a ResponseEntity
        return ResponseEntity.ok(mainMenuResponses);
    }

    @Override
    public ResponseEntity<List<MainMenuResponse>> getAllMainMenusByBankName(String bankName) {
        AssociateBank associateBank = associateBankRepository.findByAssociateBankName(bankName)
                .orElseThrow(() -> new ResourceNotFoundException("Associate Bank not found"));
        return getAllMainMenusByBankId(associateBank.getId());
    }

    @Override
    public ResponseEntity<MainMenuResponse> updateMainMenu(String id, MainMenuRequest request) {
        System.out.println("Request: "+request);
        System.out.println("ID: "+id);
        MainMenu mainMenu = mainMenuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MainMenu not found"));

        AssociateBank associateBank = associateBankRepository.findByAssociateBankName(request.getAssociateBankName())
                .orElseThrow(() -> new ResourceNotFoundException("Associate Bank not found"));

        mainMenu.setMenuItemName(request.getMenuItemName());
        mainMenu.setMenuItemDesc(request.getMenuItemDesc());
        mainMenu.setMenuItemStatus(request.getMenuItemStatus());
        mainMenu.setIcon(request.getIcon());
        mainMenu.setBank(associateBank);
        mainMenu.setMenuItemUpdatedDate(new Date());

        MainMenu updatedMainMenu = mainMenuRepository.save(mainMenu);

        var successResponse = SuccessResponse.builder()
                .type(ResponseType.success)
                .description("MainMenu updated successfully")
                .status(HttpStatus.OK)
                .build();

        MainMenuResponse mainMenuResponse = mapToResponse(updatedMainMenu);
        mainMenuResponse.setSuccessResponse(successResponse);

        return new ResponseEntity<>(mainMenuResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MainMenuResponse> deleteMainMenu(String id) {
        MainMenu mainMenu = mainMenuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MainMenu not found"));

        mainMenuRepository.delete(mainMenu);

        var successResponse = SuccessResponse.builder()
                .type(ResponseType.success)
                .description("MainMenu deleted successfully")
                .status(HttpStatus.OK)
                .build();

        MainMenuResponse mainMenuResponse = mapToResponse(mainMenu);
        mainMenuResponse.setSuccessResponse(successResponse);
        return new ResponseEntity<>(mainMenuResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<MainMenuAllResponse>> getAllMainMenus() {
        // Fetch all main menus
        List<MainMenu> mainMenus = mainMenuRepository.findAll();

        if (mainMenus.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        // Map all main menus to MainMenuAllResponse
        List<MainMenuAllResponse> responses = mainMenus.stream()
                .map(this::mapToMainMenuAllResponse)
                .collect(Collectors.toList());

        SuccessResponse successResponse = SuccessResponse.builder()
                .type(ResponseType.success)
                .description("All main menus retrieved successfully")
                .status(HttpStatus.OK)
                .build();

        // Attach the success response to each MainMenuAllResponse
        responses.forEach(response -> response.setSuccessResponse(successResponse));

        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    private MainMenuAllResponse mapToMainMenuAllResponse(MainMenu mainMenu) {
        return MainMenuAllResponse.builder()
                .id(mainMenu.getId())
                .menuItemName(mainMenu.getMenuItemName())
                .menuItemDesc(mainMenu.getMenuItemDesc())
                .menuItemStatus(mainMenu.getMenuItemStatus())
                .icon(mainMenu.getIcon())
                .associatedBank(mainMenu.getBank() != null ? mainMenu.getBank().getAssociateBankName() : "N/A")
                .associateBank(mainMenu.getBank().getAssociateBankName())
//                .subMenus(subMenuResponses)
                .menuItemAddedDate(mainMenu.getMenuItemAddedDate())
                .menuItemUpdatedDate(mainMenu.getMenuItemUpdatedDate())
                .build();
    }

    private MainMenuResponse mapToResponse(MainMenu mainMenu) {

        return MainMenuResponse.builder()
                .id(mainMenu.getId())
                .menuItemName(mainMenu.getMenuItemName())
                .menuItemDesc(mainMenu.getMenuItemDesc())
                .menuItemStatus(mainMenu.getMenuItemStatus())
                .icon(mainMenu.getIcon())
                .menuItemAddedDate(mainMenu.getMenuItemAddedDate())
                .menuItemUpdatedDate(mainMenu.getMenuItemUpdatedDate())
                .associatedBank(
                        mainMenu.getBank() != null ? mainMenu.getBank().getAssociateBankName() : "N/A"
                )
                .build();
    }
}
