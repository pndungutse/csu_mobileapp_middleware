package com.dsu.hope_bank_app_middleware.repository;

import com.dsu.hope_bank_app_middleware.entity.navigations.AssociateBank;
import com.dsu.hope_bank_app_middleware.entity.navigations.SubMenu;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SubMenuRepository extends MongoRepository<SubMenu, String> {
//    List<SubMenu> findByMainMenu_Id(String menuItemId);
//    List<SubMenu> findByMainMenu_Id(String mainMenuId);
//    List<SubMenu> findByMainMenu_Id(String mainMenuId);
    
    // Find all submenus by associate bank
    List<SubMenu> findByBank(AssociateBank bank);
    
    // Find all submenus by bank ID
    List<SubMenu> findByBank_Id(String bankId);
}
