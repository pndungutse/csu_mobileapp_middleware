package com.dsu.hope_bank_app_middleware.security.repository;

import com.dsu.hope_bank_app_middleware.security.entity.StoredAccessToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface StoredAccessTokenRepository extends MongoRepository<StoredAccessToken, String> {

    Optional<StoredAccessToken> findByToken(String token);

    List<StoredAccessToken> findByUsernameAndRevokedFalse(String username);
}
