package com.dgliger.marketplace.auth.service;


import com.dgliger.marketplace.auth.dto.AuthResponse;
import com.dgliger.marketplace.auth.dto.LoginRequest;
import com.dgliger.marketplace.auth.dto.RegisterRequest;
import com.dgliger.marketplace.auth.entity.Role;
import com.dgliger.marketplace.auth.entity.User;
import com.dgliger.marketplace.auth.enums.RoleType;
import com.dgliger.marketplace.auth.repository.RoleRepository;
import com.dgliger.marketplace.auth.repository.UserRepository;
import com.dgliger.marketplace.auth.security.JwtTokenProvider;
import com.dgliger.marketplace.common.exception.BusinessException;
import com.dgliger.marketplace.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setEnabled(true);

        // Assign role
        Set<Role> roles = new HashSet<>();
        try {
            RoleType roleType = RoleType.valueOf(request.getRole().toUpperCase());
            Role role = roleRepository.findByName(roleType)
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + request.getRole()));
            roles.add(role);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid role: " + request.getRole());
        }

        user.setRoles(roles);
        User savedUser = userRepository.save(user);

        String token = jwtTokenProvider.generateToken(savedUser);
        List<String> roleNames = savedUser.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList());

        return new AuthResponse(token, savedUser.getId(), savedUser.getEmail(),
                savedUser.getFullName(), roleNames);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("Invalid email or password");
        }

        if (!user.getEnabled()) {
            throw new BusinessException("Account is disabled");
        }

        String token = jwtTokenProvider.generateToken(user);
        List<String> roles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList());

        return new AuthResponse(token, user.getId(), user.getEmail(), user.getFullName(), roles);
    }
}