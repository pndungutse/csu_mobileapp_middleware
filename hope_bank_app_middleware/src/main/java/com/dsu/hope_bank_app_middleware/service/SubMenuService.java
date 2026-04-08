package com.dsu.hope_bank_app_middleware.service;

import com.dsu.hope_bank_app_middleware.request.navigations.SubMenuRequest;
import com.dsu.hope_bank_app_middleware.response.navigations.SubMenuResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SubMenuService {
//    SubMenu createSubMenu(String menuItemId, SubMenu subMenu);
//    SubMenu updateSubMenu(String subMenuItemId, SubMenu subMenu);
//    void deleteSubMenu(String subMenuItemId);
//    SubMenu getSubMenuById(String subMenuItemId);
//    List<SubMenu> getAllSubMenusByMenuId(String menuItemId);

    ResponseEntity<SubMenuResponse> createSubMenu(SubMenuRequest request);
    ResponseEntity<SubMenuResponse> getSubMenuById(String id);
//    ResponseEntity<List<SubMenuResponse>> getAllSubMenusByMenuId(String menuId);
    ResponseEntity<SubMenuResponse> updateSubMenu(String id, SubMenuRequest request);

    ResponseEntity<List<SubMenuResponse>> getAllSubMenusByBankId(String id);
    ResponseEntity<List<SubMenuResponse>> getAllSubMenusByBankName(String bankName);

    ResponseEntity<List<SubMenuResponse>> getAllSubMenus();

    ResponseEntity<SubMenuResponse> deleteSubmenu(String id);
}
