package com.dgliger.marketplace.auth.controller;


import com.dgliger.marketplace.auth.dto.AuthResponse;
import com.dgliger.marketplace.auth.dto.LoginRequest;
import com.dgliger.marketplace.auth.dto.RegisterRequest;
import com.dgliger.marketplace.auth.dto.UserDto;
import com.dgliger.marketplace.auth.security.UserPrincipal;
import com.dgliger.marketplace.auth.service.AuthService;
import com.dgliger.marketplace.auth.service.UserService;
import com.dgliger.marketplace.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("Registration successful", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser(@AuthenticationPrincipal UserPrincipal principal) {
        UserDto user = userService.getUserByEmail(principal.getEmail());
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable Long id) {
        UserDto user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
}
