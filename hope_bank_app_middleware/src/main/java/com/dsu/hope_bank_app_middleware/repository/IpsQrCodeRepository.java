package com.dsu.hope_bank_app_middleware.repository;

import com.dsu.hope_bank_app_middleware.entity.IpsQrCode;
import com.dsu.hope_bank_app_middleware.enumeration.Status;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface IpsQrCodeRepository extends MongoRepository<IpsQrCode, String> {
    Optional<IpsQrCode> findByUetr(String uetr);

    Optional<IpsQrCode> findByQrHeaderUUID(String qrHeaderUUID);

    List<IpsQrCode> findByCreatedByAccount(String createdByAccount);

    List<IpsQrCode> findByCreatedByCustomerNumber(String createdByCustomerNumber);

    List<IpsQrCode> findByCreatedByCustomerNumberOrderByCreatedDateDesc(String createdByCustomerNumber);

    List<IpsQrCode> findByCreatedByCustomerNumberAndStatusOrderByCreatedDateDesc(
            String createdByCustomerNumber,
            Status status
    );
}
