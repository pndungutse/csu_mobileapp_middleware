package com.dsu.hope_bank_app_middleware.repository;

import com.dsu.hope_bank_app_middleware.entity.navigations.FormElement;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FormElementRepository extends MongoRepository<FormElement, String> {
//    List<FormElement> findBySubMenu_Id(String subMenuItemId);
    List<FormElement> findBySubMenu_IdOrderByFormElementFieldNoAsc(String subMenuItemId);
}
