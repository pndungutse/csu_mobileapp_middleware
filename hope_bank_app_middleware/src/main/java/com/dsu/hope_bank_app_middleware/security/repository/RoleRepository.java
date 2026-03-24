package com.dsu.hope_bank_app_middleware.security.repository;

import com.dsu.hope_bank_app_middleware.security.entity.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, String> {
    Optional<Role> findByName(String name);
}
