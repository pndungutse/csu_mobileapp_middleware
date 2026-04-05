package com.dsu.hope_bank_app_middleware.security.repository;

import com.dsu.hope_bank_app_middleware.security.entity.StoredRefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface StoredRefreshTokenRepository extends MongoRepository<StoredRefreshToken, String> {

    Optional<StoredRefreshToken> findByToken(String token);

    List<StoredRefreshToken> findByUsernameAndRevokedFalse(String username);
}
