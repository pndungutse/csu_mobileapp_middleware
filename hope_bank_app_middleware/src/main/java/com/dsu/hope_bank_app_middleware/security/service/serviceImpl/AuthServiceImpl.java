package com.dsu.hope_bank_app_middleware.security.service.serviceImpl;

import com.dsu.hope_bank_app_middleware.exception.CustomAPIException;
import com.dsu.hope_bank_app_middleware.security.JwtTokenProvider;
import com.dsu.hope_bank_app_middleware.security.TokenBlacklistService;
import com.dsu.hope_bank_app_middleware.security.entity.Role;
import com.dsu.hope_bank_app_middleware.security.entity.StoredAccessToken;
import com.dsu.hope_bank_app_middleware.security.entity.StoredRefreshToken;
import com.dsu.hope_bank_app_middleware.security.entity.User;
import com.dsu.hope_bank_app_middleware.security.repository.RoleRepository;
import com.dsu.hope_bank_app_middleware.security.repository.StoredAccessTokenRepository;
import com.dsu.hope_bank_app_middleware.security.repository.StoredRefreshTokenRepository;
import com.dsu.hope_bank_app_middleware.security.repository.UserRepository;
import com.dsu.hope_bank_app_middleware.security.request.ChangePasswordRequest;
import com.dsu.hope_bank_app_middleware.security.request.LoginRequest;
import com.dsu.hope_bank_app_middleware.security.request.LogoutRequest;
import com.dsu.hope_bank_app_middleware.security.request.PasscodeValidationRequest;
import com.dsu.hope_bank_app_middleware.security.request.RefreshTokenRequest;
import com.dsu.hope_bank_app_middleware.security.request.RegisterRequest;
import com.dsu.hope_bank_app_middleware.security.response.JWTAuthResponse;
import com.dsu.hope_bank_app_middleware.security.service.AuthService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = Logger.getLogger(AuthServiceImpl.class.getName());

    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    private JwtTokenProvider jwtTokenProvider;
    private TokenBlacklistService tokenBlacklistService;
    private StoredAccessTokenRepository storedAccessTokenRepository;
    private StoredRefreshTokenRepository storedRefreshTokenRepository;

    @Value("${app.jwt.refresh-token-expiration-ms:604800000}")
    private long refreshTokenExpirationMs;

    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider,
            TokenBlacklistService tokenBlacklistService,
            StoredAccessTokenRepository storedAccessTokenRepository,
            StoredRefreshTokenRepository storedRefreshTokenRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenBlacklistService = tokenBlacklistService;
        this.storedAccessTokenRepository = storedAccessTokenRepository;
        this.storedRefreshTokenRepository = storedRefreshTokenRepository;
    }

    @Override
    public JWTAuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getUsernameOrEmail(),loginRequest.getPassword()
        ));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new CustomAPIException(HttpStatus.BAD_REQUEST, "User not found"));

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        persistAccessToken(user, accessToken);

        String refreshTokenValue = UUID.randomUUID().toString();
        Date now = new Date();
        Date refreshExpires = new Date(now.getTime() + refreshTokenExpirationMs);
        StoredRefreshToken refreshRow = StoredRefreshToken.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .token(refreshTokenValue)
                .refreshPasscode(user.getPassword())
                .issuedAt(now)
                .expiresAt(refreshExpires)
                .revoked(false)
                .build();
        try {
            storedRefreshTokenRepository.save(refreshRow);
        } catch (DataAccessException e) {
            logger.log(Level.SEVERE, "Failed to save refresh token to MongoDB", e);
            throw new CustomAPIException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Could not store refresh token. Check MongoDB connection and spring.data.mongodb.uri: " + e.getMessage());
        }

        JWTAuthResponse jwtAuthResponse = new JWTAuthResponse();
        jwtAuthResponse.setAccessToken(accessToken);
        jwtAuthResponse.setRefreshToken(refreshTokenValue);
        jwtAuthResponse.setExpiresIn(jwtTokenProvider.getAccessTokenExpirationSeconds());
        jwtAuthResponse.setMustChangePassword(user.isMustChangePassword());
        return jwtAuthResponse;
    }

    @Override
    public JWTAuthResponse refreshAccessToken(RefreshTokenRequest refreshTokenRequest) {
        if (refreshTokenRequest == null || !StringUtils.hasText(refreshTokenRequest.getRefreshToken())) {
            throw new CustomAPIException(HttpStatus.BAD_REQUEST, "refresh_token is required");
        }
        String raw = refreshTokenRequest.getRefreshToken().trim();
        StoredRefreshToken storedRefresh = storedRefreshTokenRepository.findByToken(raw)
                .orElseThrow(() -> new CustomAPIException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

        Date now = new Date();
        if (storedRefresh.isRevoked() || storedRefresh.getExpiresAt() == null || storedRefresh.getExpiresAt().before(now)) {
            throw new CustomAPIException(HttpStatus.UNAUTHORIZED, "Refresh token expired or revoked");
        }

        storedRefresh.setRevoked(true);
        storedRefreshTokenRepository.save(storedRefresh);

        User user = userRepository.findById(storedRefresh.getUserId())
                .orElseThrow(() -> new CustomAPIException(HttpStatus.UNAUTHORIZED, "User no longer exists"));

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        persistAccessToken(user, accessToken);

        String newRefresh = UUID.randomUUID().toString();
        Date refreshExpires = new Date(now.getTime() + refreshTokenExpirationMs);
        StoredRefreshToken newRow = StoredRefreshToken.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .token(newRefresh)
                .refreshPasscode(user.getPassword())
                .issuedAt(now)
                .expiresAt(refreshExpires)
                .revoked(false)
                .build();
        try {
            storedRefreshTokenRepository.save(newRow);
        } catch (DataAccessException e) {
            logger.log(Level.SEVERE, "Failed to save rotated refresh token", e);
            throw new CustomAPIException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Could not store refresh token: " + e.getMessage());
        }

        JWTAuthResponse response = new JWTAuthResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(newRefresh);
        response.setExpiresIn(jwtTokenProvider.getAccessTokenExpirationSeconds());
        response.setMustChangePassword(user.isMustChangePassword());
        return response;
    }

    @Override
    public String register(RegisterRequest registerRequest) {

        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new CustomAPIException(HttpStatus.BAD_REQUEST, "Username is already exists!");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new CustomAPIException(HttpStatus.BAD_REQUEST, "Email is already exists!");
        }

        User user = new User();
        user.setFirst_name(registerRequest.getFirst_name());
        user.setLast_name(registerRequest.getLast_name());
        user.setCustomer_number(registerRequest.getCustomer_number());
        user.setEmail(registerRequest.getEmail());
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEnv(registerRequest.getEnv());
        user.setMustChangePassword(true);

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName("ROLE_USER").get();
        roles.add(userRole);
        user.setRoles(roles);

        userRepository.save(user);
        syncRefreshPasscode(user, true);
        return "User registered successfully!";
    }

    @Override
    public String changePassword(ChangePasswordRequest changePasswordRequest) {
        if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmPassword())) {
            throw new CustomAPIException(HttpStatus.BAD_REQUEST, "New password and confirm password do not match");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new CustomAPIException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new CustomAPIException(HttpStatus.BAD_REQUEST, "User not found"));

        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new CustomAPIException(HttpStatus.BAD_REQUEST, "Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        user.setMustChangePassword(false);
        userRepository.save(user);
        syncRefreshPasscode(user, false);

        revokeAllActiveTokensForUser(user.getUsername());

        return "Password changed successfully";
    }

    @Override
    public String logout(String authorizationHeader, LogoutRequest logoutRequest) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new CustomAPIException(HttpStatus.BAD_REQUEST, "Missing or invalid Authorization header");
        }

        String token = authorizationHeader.substring(7).trim();

        Optional<Claims> accessClaims = jwtTokenProvider.parseAccessTokenClaimsAllowingExpired(token);
        if (accessClaims.isPresent()) {
            String username = accessClaims.get().getSubject();
            Date exp = accessClaims.get().getExpiration();
            tokenBlacklistService.blacklist(token, exp != null ? exp : new Date());
            blacklistAllActiveAccessTokensForUser(username);
            revokeAllActiveTokensForUser(username);
        } else {
            Optional<StoredRefreshToken> bearerRefresh = storedRefreshTokenRepository.findByToken(token);
            if (bearerRefresh.isPresent()) {
                String username = bearerRefresh.get().getUsername();
                blacklistAllActiveAccessTokensForUser(username);
                revokeAllActiveTokensForUser(username);
            } else {
                throw new CustomAPIException(HttpStatus.BAD_REQUEST,
                        "Invalid token: send access JWT or refresh token in Authorization, or use refresh_token in body");
            }
        }

        if (logoutRequest != null && StringUtils.hasText(logoutRequest.getRefreshToken())) {
            String rt = logoutRequest.getRefreshToken().trim();
            storedRefreshTokenRepository.findByToken(rt).ifPresent(r -> {
                if (!r.isRevoked()) {
                    r.setRevoked(true);
                    storedRefreshTokenRepository.save(r);
                }
            });
        }

        return "Logged out successfully";
    }

    @Override
    public boolean isPasscodeValid(PasscodeValidationRequest passcodeValidationRequest) {
        System.out.println("Username: "+passcodeValidationRequest+" Password: "+passcodeValidationRequest.getPassword());
        if (passcodeValidationRequest == null
                || !StringUtils.hasText(passcodeValidationRequest.getUsername())
                || !StringUtils.hasText(passcodeValidationRequest.getPassword())) {
            System.out.println("Returning false, no username"+passcodeValidationRequest.getUsername()+" No password: "+passcodeValidationRequest.getPassword());
            return false;
        }

        String username = passcodeValidationRequest.getUsername().trim();
        List<StoredRefreshToken> refreshRows = storedRefreshTokenRepository.findByUsername(username);
        if (refreshRows.isEmpty()) {
            System.out.println("Returning false, no stored refresh token");
            return false;
        }

        String rawPassword = passcodeValidationRequest.getPassword();
        for (StoredRefreshToken row : refreshRows) {
            if (StringUtils.hasText(row.getRefreshPasscode())
                    && passwordEncoder.matches(rawPassword, row.getRefreshPasscode())) {
                System.out.println("Returning True, password matches");
                return true;
            }
        }
        System.out.println("Returning false");
        return false;
    }

    private void persistAccessToken(User user, String accessJwt) {
        Date now = new Date();
        StoredAccessToken stored = StoredAccessToken.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .token(accessJwt)
                .issuedAt(now)
                .expiresAt(jwtTokenProvider.getExpirationDate(accessJwt))
                .revoked(false)
                .build();
        try {
            StoredAccessToken saved = storedAccessTokenRepository.save(stored);
            logger.log(Level.INFO, "Persisted access token id={0} username={1} collection access_tokens",
                    new Object[]{saved.getId(), saved.getUsername()});
        } catch (DataAccessException e) {
            logger.log(Level.SEVERE, "Failed to save access token to MongoDB", e);
            throw new CustomAPIException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Could not store session token. Check MongoDB connection and spring.data.mongodb.uri: " + e.getMessage());
        }
    }

    private void revokeAllActiveTokensForUser(String username) {
        List<StoredAccessToken> active = storedAccessTokenRepository.findByUsernameAndRevokedFalse(username);
        for (StoredAccessToken st : active) {
            st.setRevoked(true);
            storedAccessTokenRepository.save(st);
        }
        List<StoredRefreshToken> refreshActive = storedRefreshTokenRepository.findByUsernameAndRevokedFalse(username);
        for (StoredRefreshToken rt : refreshActive) {
            rt.setRevoked(true);
            storedRefreshTokenRepository.save(rt);
        }
    }

    /** In-memory blacklist for each stored access JWT so rejection works even if DB read lags (matches filter order). */
    private void blacklistAllActiveAccessTokensForUser(String username) {
        List<StoredAccessToken> active = storedAccessTokenRepository.findByUsernameAndRevokedFalse(username);
        Date fallbackExp = new Date(System.currentTimeMillis() + jwtTokenProvider.getAccessTokenExpirationSeconds() * 1000L);
        for (StoredAccessToken st : active) {
            Date exp = st.getExpiresAt() != null ? st.getExpiresAt() : fallbackExp;
            tokenBlacklistService.blacklist(st.getToken(), exp);
        }
    }

    private void syncRefreshPasscode(User user, boolean createIfMissing) {
        List<StoredRefreshToken> refreshRows = storedRefreshTokenRepository.findByUsername(user.getUsername());
        if (refreshRows.isEmpty() && createIfMissing) {
            StoredRefreshToken bootstrapRow = StoredRefreshToken.builder()
                    .userId(user.getId())
                    .username(user.getUsername())
                    .token(UUID.randomUUID().toString())
                    .refreshPasscode(user.getPassword())
                    .issuedAt(new Date())
                    .expiresAt(new Date())
                    .revoked(true)
                    .build();
            storedRefreshTokenRepository.save(bootstrapRow);
            return;
        }

        for (StoredRefreshToken row : refreshRows) {
            row.setRefreshPasscode(user.getPassword());
            storedRefreshTokenRepository.save(row);
        }
    }
}
