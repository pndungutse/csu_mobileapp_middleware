package com.dsu.hope_bank_app_middleware.repository;

import com.dsu.hope_bank_app_middleware.entity.navigations.IconItem;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IconRepository extends MongoRepository<IconItem, String> {
}
