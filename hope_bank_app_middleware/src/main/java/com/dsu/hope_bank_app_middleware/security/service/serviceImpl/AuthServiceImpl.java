package com.dsu.hope_bank_app_middleware.security.service.serviceImpl;

import com.dsu.hope_bank_app_middleware.exception.CustomAPIException;
import com.dsu.hope_bank_app_middleware.security.JwtTokenProvider;
import com.dsu.hope_bank_app_middleware.security.entity.Role;
import com.dsu.hope_bank_app_middleware.security.entity.User;
import com.dsu.hope_bank_app_middleware.security.repository.RoleRepository;
import com.dsu.hope_bank_app_middleware.security.repository.UserRepository;
import com.dsu.hope_bank_app_middleware.security.request.LoginRequest;
import com.dsu.hope_bank_app_middleware.security.request.RegisterRequest;
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

    public AuthServiceImpl(AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public String login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getUsernameOrEmail(),loginRequest.getPassword()
        ));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(authentication);

        return token;
//        return "User logged in successfully!";
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

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName("ROLE_USER").get();
        roles.add(userRole);
        user.setRoles(roles);

        userRepository.save(user);
        return "User registered successfully!";
    }
}
