package com.dsu.hope_bank_app_middleware.navigations.repository;

import com.dsu.hope_bank_app_middleware.navigations.entity.MainMenu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MainMenuRepository extends MongoRepository<MainMenu, String> {
//    List<MainMenu> findByBank_Id(String associateBankId);
    List<MainMenu> findByBank_Id(String associateBankId);
}
