package com.dsu.hope_bank_app_middleware.utils;

import com.dsu.hope_bank_app_middleware.navigations.entity.AssociateBank;
import com.dsu.hope_bank_app_middleware.navigations.entity.FormElement;
import com.dsu.hope_bank_app_middleware.navigations.entity.MainMenu;
import com.dsu.hope_bank_app_middleware.navigations.entity.SubMenu;
import com.dsu.hope_bank_app_middleware.navigations.repository.AssociateBankRepository;
import com.dsu.hope_bank_app_middleware.navigations.repository.FormElementRepository;
import com.dsu.hope_bank_app_middleware.navigations.repository.MainMenuRepository;
import com.dsu.hope_bank_app_middleware.navigations.repository.SubMenuRepository;
import com.dsu.hope_bank_app_middleware.navigations.response.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class Mappings {

    @Autowired
    private AssociateBankRepository associateBankRepository;

    @Autowired
    private MainMenuRepository mainMenuRepository;

    @Autowired
    private SubMenuRepository subMenuRepository;

    @Autowired
    private FormElementRepository formElementRepository;

    public NavigationsResponse mapToNavigationsResponse(AssociateBank associateBank) {
        // Fetch main menus associated with the given AssociateBank
        List<MainMenu> mainMenus = mainMenuRepository.findByBank_Id(associateBank.getId());

        // Map each MainMenu to MainMenuResponse, including nested SubMenus and FormElements
        List<MainMenuResponse> mainMenuResponses = mainMenus.stream()
                .map(this::mapToMainMenuResponse)
                .collect(Collectors.toList());

        return NavigationsResponse.builder()
                .id(associateBank.getId())
                .associateBankName(associateBank.getAssociateBankName())
                .associateBankStatus(associateBank.getAssociateBankStatus())
                .associateBankAddedDate(associateBank.getAssociateBankAddedDate())
                .associateBankUpdatedDate(associateBank.getAssociateBankUpdatedDate())
                .mainMenus(mainMenuResponses)
                .build();
    }

    public MainMenuResponse mapToMainMenuResponse(MainMenu mainMenu) {
        // Fetch submenus associated with the given MainMenu
        List<SubMenu> subMenus = subMenuRepository.findByMainMenu_Id(mainMenu.getId());

        // Map each SubMenu to SubMenuResponse
        List<SubMenuResponse> subMenuResponses = subMenus.stream()
                .map(this::mapToSubMenuResponse)
                .collect(Collectors.toList());

        return MainMenuResponse.builder()
                .id(mainMenu.getId())
                .menuItemName(mainMenu.getMenuItemName())
                .menuItemDesc(mainMenu.getMenuItemDesc())
                .menuItemStatus(mainMenu.getMenuItemStatus())
                .icon(mainMenu.getIcon())
                .associatedBank(mainMenu.getBank().getAssociateBankName())
                .menuItemAddedDate(mainMenu.getMenuItemAddedDate())
                .menuItemUpdatedDate(mainMenu.getMenuItemUpdatedDate())
                .subMenus(subMenuResponses)
                .build();
    }

    public SubMenuResponse mapToSubMenuResponse(SubMenu subMenu) {
        // Fetch form elements associated with the given SubMenu
        List<FormElement> formElements = formElementRepository.findBySubMenu_Id(subMenu.getId());

        // Map each FormElement to FormElementResponse
        List<FormElementResponse> formElementResponses = formElements.stream()
                .map(this::mapToFormElementResponse)
                .collect(Collectors.toList());

        return SubMenuResponse.builder()
                .id(subMenu.getId())
                .subMenuItemName(subMenu.getSubMenuItemName())
                .subMenuItemDesc(subMenu.getSubMenuItemDesc())
                .subMenuItemStatus(subMenu.getSubMenuItemStatus())
                .icon(subMenu.getIcon())
                .mainMenuItemName(subMenu.getMainMenu().getMenuItemName())
                .subMenuCategory(subMenu.getSubMenuCategory())
                .subMenuItemAddedDate(subMenu.getSubMenuItemAddedDate())
                .subMenuItemUpdatedDate(subMenu.getSubMenuItemUpdatedDate())
                .formElements(formElementResponses)
                .build();
    }

    public FormElementResponse mapToFormElementResponse(FormElement formElement) {
        return FormElementResponse.builder()
                .id(formElement.getId())
                .formElementName(formElement.getFormElementName())
                .formElementDescription(formElement.getFormElementDescription())
                .formElementLabel(formElement.getFormElementLabel())
                .formElementValueType(formElement.getFormElementValueType())
                .formElementType(formElement.getFormElementType())
                .formElementPlaceHolder(formElement.getFormElementPlaceHolder())
                .formElementStatus(formElement.getFormElementStatus())
                .subMenuItemName(formElement.getSubMenu().getSubMenuItemName())
                .formElementAddedDate(formElement.getFormElementAddedDate())
                .formElementUpdatedDate(formElement.getFormElementUpdatedDate())
                .build();
    }



    public AssociateBankResponse mapToResponse(AssociateBank associateBank) {

        return AssociateBankResponse.builder()
                .id(associateBank.getId())
                .associateBankName(associateBank.getAssociateBankName())
                .associateBankName(associateBank.getAssociateBankName())
                .associateBankStatus(associateBank.getAssociateBankStatus())
                .associateBankAddedDate(associateBank.getAssociateBankAddedDate())
                .associateBankUpdatedDate(associateBank.getAssociateBankUpdatedDate())
                .build();
    }
}
