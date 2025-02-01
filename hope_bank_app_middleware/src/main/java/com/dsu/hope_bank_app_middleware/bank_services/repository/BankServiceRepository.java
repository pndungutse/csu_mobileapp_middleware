package com.dsu.hope_bank_app_middleware.bank_services.repository;

import com.dsu.hope_bank_app_middleware.bank_services.entity.BankService;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BankServiceRepository extends MongoRepository<BankService, String> {

}
