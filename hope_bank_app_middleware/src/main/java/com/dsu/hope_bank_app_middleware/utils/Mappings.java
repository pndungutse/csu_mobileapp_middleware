package com.dsu.hope_bank_app_middleware.utils;

import com.dsu.hope_bank_app_middleware.entity.navigations.*;
import com.dsu.hope_bank_app_middleware.enumeration.Status;
import com.dsu.hope_bank_app_middleware.repository.*;
import com.dsu.hope_bank_app_middleware.response.navigations.*;
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

        return MainMenuResponse.builder()
                .id(mainMenu.getId())
                .menuItemName(mainMenu.getMenuItemName())
                .menuItemDesc(mainMenu.getMenuItemDesc())
                .menuItemStatus(mainMenu.getMenuItemStatus())
                .icon(mainMenu.getIcon())
                .associatedBank(mainMenu.getBank().getAssociateBankName())
                .menuItemAddedDate(mainMenu.getMenuItemAddedDate())
                .menuItemUpdatedDate(mainMenu.getMenuItemUpdatedDate())
                .build();
    }

    public SubMenuResponse mapToSubMenuResponse(SubMenu subMenu) {
        // Fetch form elements associated with the given SubMenu
        List<FormElement> formElements = formElementRepository.findBySubMenu_IdOrderByFormElementFieldNoAsc(subMenu.getId());

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
                .subMenuActionUrl(subMenu.getSubMenuActionUrl())
                .subMenuAssociateBank(subMenu.getBank().getAssociateBankName())
                .subMenuCategory(subMenu.getSubMenuCategory())
                .subMenuBelongToMenu(subMenu.getSubMenuBelongToMenu())
                .subMenuDisplayOrder(subMenu.getSubMenuDisplayOrder())
                .subMenuItemAddedDate(subMenu.getSubMenuItemAddedDate())
                .subMenuItemUpdatedDate(subMenu.getSubMenuItemUpdatedDate())
                .formElements(formElementResponses)
                .build();
    }

    public FormElementResponse mapToFormElementResponse(FormElement formElement) {
        return FormElementResponse.builder()
                .id(formElement.getId())
                .formElementFieldNo(formElement.getFormElementFieldNo())
                .formElementName(formElement.getFormElementName())
                .formElementDescription(formElement.getFormElementDescription())
                .formElementLabel(formElement.getFormElementLabel())
                .formElementValueType(formElement.getFormElementValueType())
                .formElementType(formElement.getFormElementType())
                .formElementPlaceHolder(formElement.getFormElementPlaceHolder())
                .formElementSelectItem(formElement.getFormElementSelectItem())
                .formElementStatus(formElement.getFormElementStatus())
                .formElementDefaultValue(formElement.getFormElementDefaultValue())
                .formElementFetchDetail(formElement.getFormElementFetchDetail())
                .formElementFetchInfoUrl(formElement.getFormElementFetchInfoUrl())
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

    public IconResponse mapToIconResponse(IconItem iconItem) {

        return IconResponse.builder()
                .id(iconItem.getId())
                .icon(iconItem.getIcon())
                .iconName(iconItem.getIconName())
                .formElementAddedDate(iconItem.getIconAddedDate())
                .formElementUpdatedDate(iconItem.getIconUpdatedDate())
                .build();
    }

    public SubMenuResponse mapToSubmenuResponse(SubMenu subMenu) {
        List<FormElement> formElements = formElementRepository.findBySubMenu_IdOrderByFormElementFieldNoAsc(subMenu.getId());

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
                .subMenuActionUrl(subMenu.getSubMenuActionUrl())
                .subMenuAssociateBank(subMenu.getBank().getAssociateBankName())
                .subMenuCategory(subMenu.getSubMenuCategory())
                .subMenuBelongToMenu(subMenu.getSubMenuBelongToMenu())
                .subMenuDisplayOrder(subMenu.getSubMenuDisplayOrder())
                .subMenuItemAddedDate(subMenu.getSubMenuItemAddedDate())
                .subMenuItemUpdatedDate(subMenu.getSubMenuItemUpdatedDate())
                .formElements(formElementResponses)
                .build();
    }

    public MainMenuResponse mapToMainmenuResponse(MainMenu mainMenu) {
        return MainMenuResponse.builder()
                .id(mainMenu.getId())
                .menuItemName(mainMenu.getMenuItemName())
                .menuItemDesc(mainMenu.getMenuItemDesc())
                .menuItemStatus(mainMenu.getMenuItemStatus())
                .icon(mainMenu.getIcon())
                .associatedBank(mainMenu.getBank().getAssociateBankName())
                .menuItemAddedDate(mainMenu.getMenuItemAddedDate())
                .menuItemUpdatedDate(mainMenu.getMenuItemUpdatedDate())
                .build();
    }
}
