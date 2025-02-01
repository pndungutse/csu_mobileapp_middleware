package com.dsu.hope_bank_app_middleware.navigations.repository;

import com.dsu.hope_bank_app_middleware.navigations.entity.AssociateBank;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AssociateBankRepository extends MongoRepository<AssociateBank, String> {
    Optional<AssociateBank> findByAssociateBankName(String bankName);
//    Optional<AssociateBank> findById(String associateBankId);

}
