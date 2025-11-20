package com.dsu.hope_bank_app_middleware.navigations.service.serviceImpl;

import com.dsu.hope_bank_app_middleware.exception.ResourceNotFoundException;
import com.dsu.hope_bank_app_middleware.exception.SuccessResponse;
import com.dsu.hope_bank_app_middleware.general_enumerations.ResponseType;
import com.dsu.hope_bank_app_middleware.navigations.entity.AssociateBank;
import com.dsu.hope_bank_app_middleware.navigations.entity.MainMenu;
import com.dsu.hope_bank_app_middleware.navigations.repository.AssociateBankRepository;
import com.dsu.hope_bank_app_middleware.navigations.repository.MainMenuRepository;
import com.dsu.hope_bank_app_middleware.request.navigations.MainMenuRequest;
import com.dsu.hope_bank_app_middleware.response.navigations.MainMenuResponse;
import com.dsu.hope_bank_app_middleware.navigations.service.MainMenuService;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MainMenuServiceImpl implements MainMenuService {

    @Autowired
    private MainMenuRepository mainMenuRepository;
    @Autowired
    private AssociateBankRepository associateBankRepository;

    @Override
    public ResponseEntity<MainMenuResponse> createMainMenu(MainMenuRequest request) {

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

        if (mainMenus.isEmpty()) {
            throw new ResourceNotFoundException("No main menus found for the associated bank ID: " + associatedBankId);
        }

        // Map each MainMenu entity to a MainMenuResponse object
        List<MainMenuResponse> mainMenuResponses = mainMenus.stream()
                .map(this::mapToResponse) // Reuse the mapping method
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
        return new ResponseEntity<>(mainMenuResponses, HttpStatus.OK);
    }



    @Override
    public ResponseEntity<MainMenuResponse> updateMainMenu(String id, MainMenuRequest request) {
        MainMenu mainMenu = mainMenuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MainMenu not found"));

        mainMenu.setMenuItemName(request.getMenuItemName());
        mainMenu.setMenuItemDesc(request.getMenuItemDesc());
        mainMenu.setMenuItemStatus(request.getMenuItemStatus());
        mainMenu.setIcon(request.getIcon());
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
