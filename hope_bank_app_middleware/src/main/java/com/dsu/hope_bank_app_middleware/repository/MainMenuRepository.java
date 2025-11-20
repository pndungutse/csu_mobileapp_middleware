package com.dsu.hope_bank_app_middleware.repository;

import com.dsu.hope_bank_app_middleware.entity.navigations.MainMenu;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MainMenuRepository extends MongoRepository<MainMenu, String> {
//    List<MainMenu> findByBank_Id(String associateBankId);
    List<MainMenu> findByBank_Id(String associateBankId);
}
