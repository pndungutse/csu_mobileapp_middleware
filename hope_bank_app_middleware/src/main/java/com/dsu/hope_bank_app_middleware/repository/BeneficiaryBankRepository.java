package com.dsu.hope_bank_app_middleware.repository;

import com.dsu.hope_bank_app_middleware.entity.BeneficiaryBank;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface BeneficiaryBankRepository extends MongoRepository<BeneficiaryBank, String> {
    Optional<BeneficiaryBank> findByBeneficiaryCode(String bankCode);
}
