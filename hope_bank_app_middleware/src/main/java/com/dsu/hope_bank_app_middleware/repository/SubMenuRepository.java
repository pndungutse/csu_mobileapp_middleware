package com.dsu.hope_bank_app_middleware.navigations.repository;

import com.dsu.hope_bank_app_middleware.navigations.entity.MainMenu;
import com.dsu.hope_bank_app_middleware.navigations.entity.SubMenu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SubMenuRepository extends MongoRepository<SubMenu, String> {
//    List<SubMenu> findByMainMenu_Id(String menuItemId);
    List<SubMenu> findByMainMenu_Id(String mainMenuId);
}
