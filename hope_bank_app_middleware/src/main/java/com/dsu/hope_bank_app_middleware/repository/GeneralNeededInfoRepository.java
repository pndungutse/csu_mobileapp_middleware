package com.dsu.hope_bank_app_middleware.repository;

import com.dsu.hope_bank_app_middleware.entity.navigations.AssociateBank;
import com.dsu.hope_bank_app_middleware.entity.navigations.GeneralNeededInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface GeneralNeededInfoRepository extends MongoRepository<GeneralNeededInfo, String> {
    List<GeneralNeededInfo> findByBank_Id(String bankId);
    Optional<GeneralNeededInfo> findByBank(AssociateBank bank);
}
