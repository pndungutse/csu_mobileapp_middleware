package com.dsu.hope_bank_app_middleware.security.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Persists issued JWT access tokens for revocation and auditing.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "access_tokens")
public class StoredAccessToken {

    @Id
    private String id;

    private String userId;

    @Indexed
    private String username;

    /**
     * Full JWT string (matches Authorization Bearer value).
     * Do not add a MongoDB index on this field: JWTs often exceed the 1024-byte index key limit and inserts will fail.
     */
    private String token;

    private Date issuedAt;

    private Date expiresAt;

    private boolean revoked;
}
