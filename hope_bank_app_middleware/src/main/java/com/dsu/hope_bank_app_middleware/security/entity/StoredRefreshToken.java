package com.dsu.hope_bank_app_middleware.security.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "refresh_tokens")
public class StoredRefreshToken {

    @Id
    private String id;

    private String userId;

    @Indexed
    private String username;

    @Indexed(unique = true)
    private String token;

    @Field("refresh_passcode")
    private String refreshPasscode;

    private Date issuedAt;

    private Date expiresAt;

    private boolean revoked;
}
