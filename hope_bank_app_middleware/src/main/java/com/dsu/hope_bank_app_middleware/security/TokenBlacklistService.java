package com.dsu.hope_bank_app_middleware.security;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {
    private final Map<String, Long> blacklistedTokens = new ConcurrentHashMap<>();

    public void blacklist(String token, Date expiresAt) {
        blacklistedTokens.put(token, expiresAt.getTime());
    }

    public boolean isBlacklisted(String token) {
        Long expiryTime = blacklistedTokens.get(token);
        if (expiryTime == null) {
            return false;
        }
        if (expiryTime < System.currentTimeMillis()) {
            blacklistedTokens.remove(token);
            return false;
        }
        return true;
    }
}
