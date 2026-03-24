package com.dsu.hope_bank_app_middleware.security.service.serviceImpl;

import com.dsu.hope_bank_app_middleware.exception.CustomAPIException;
import com.dsu.hope_bank_app_middleware.security.JwtTokenProvider;
import com.dsu.hope_bank_app_middleware.security.TokenBlacklistService;
import com.dsu.hope_bank_app_middleware.security.entity.Role;
import com.dsu.hope_bank_app_middleware.security.entity.User;
import com.dsu.hope_bank_app_middleware.security.repository.RoleRepository;
import com.dsu.hope_bank_app_middleware.security.repository.UserRepository;
import com.dsu.hope_bank_app_middleware.security.request.ChangePasswordRequest;
import com.dsu.hope_bank_app_middleware.security.request.LoginRequest;
import com.dsu.hope_bank_app_middleware.security.request.RegisterRequest;
import com.dsu.hope_bank_app_middleware.security.response.JWTAuthResponse;
import com.dsu.hope_bank_app_middleware.security.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {
    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    private JwtTokenProvider jwtTokenProvider;
    private TokenBlacklistService tokenBlacklistService;

    public AuthServiceImpl(AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, TokenBlacklistService tokenBlacklistService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    public JWTAuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getUsernameOrEmail(),loginRequest.getPassword()
        ));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(authentication);
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new CustomAPIException(HttpStatus.BAD_REQUEST, "User not found"));

        JWTAuthResponse jwtAuthResponse = new JWTAuthResponse();
        jwtAuthResponse.setAccessToken(token);
        jwtAuthResponse.setMustChangePassword(user.isMustChangePassword());
        return jwtAuthResponse;
    }

    @Override
    public String register(RegisterRequest registerRequest) {

        // first check if username exists or not in database
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new CustomAPIException(HttpStatus.BAD_REQUEST, "Username is already exists!");
        }

        // first check if email exists or not in database
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

        return "Password changed successfully";
    }

    @Override
    public String logout(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new CustomAPIException(HttpStatus.BAD_REQUEST, "Missing or invalid Authorization header");
        }

        String token = authorizationHeader.substring(7);
        if (!jwtTokenProvider.validateToken(token)) {
            throw new CustomAPIException(HttpStatus.BAD_REQUEST, "Invalid token");
        }

        tokenBlacklistService.blacklist(token, jwtTokenProvider.getExpirationDate(token));
        return "Logged out successfully";
    }
}
