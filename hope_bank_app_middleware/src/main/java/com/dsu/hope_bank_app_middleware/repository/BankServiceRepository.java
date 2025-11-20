package com.dsu.hope_bank_app_middleware.repository;

import com.dsu.hope_bank_app_middleware.entity.navigations.BankService;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BankServiceRepository extends MongoRepository<BankService, String> {

}
