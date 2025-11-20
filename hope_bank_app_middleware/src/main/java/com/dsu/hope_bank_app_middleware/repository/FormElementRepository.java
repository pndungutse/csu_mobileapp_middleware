package com.dsu.hope_bank_app_middleware.navigations.repository;

import com.dsu.hope_bank_app_middleware.navigations.entity.FormElement;
import com.dsu.hope_bank_app_middleware.navigations.entity.SubMenu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FormElementRepository extends MongoRepository<FormElement, String> {
//    List<FormElement> findBySubMenu_Id(String subMenuItemId);
    List<FormElement> findBySubMenu_Id(String subMenuItemId);
}
