package com.group05.TC_LLM_Generator.presentation.controller;

import com.group05.TC_LLM_Generator.application.port.in.authen.LoginUseCase;
import com.group05.TC_LLM_Generator.application.port.in.authen.LoginWithPasswordUseCase;
import com.group05.TC_LLM_Generator.application.port.in.authen.LogoutUseCase;
import com.group05.TC_LLM_Generator.application.port.in.authen.RefreshTokenUseCase;
import com.group05.TC_LLM_Generator.application.port.in.authen.SignupUseCase;
import com.group05.TC_LLM_Generator.application.port.in.authen.VerifySignupUseCase;
import com.group05.TC_LLM_Generator.application.port.in.authen.dto.request.LoginRequest;
import com.group05.TC_LLM_Generator.application.port.in.authen.dto.request.SignupRequest;
import com.group05.TC_LLM_Generator.application.port.in.authen.dto.result.AuthResponse;
import com.group05.TC_LLM_Generator.application.port.in.authen.dto.result.SignupResponse;
import com.group05.TC_LLM_Generator.application.service.authen.ForgotPasswordService;
import com.group05.TC_LLM_Generator.presentation.dto.common.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenController {

    private final LoginUseCase loginUseCase;
    private final LoginWithPasswordUseCase loginWithPasswordUseCase;
    private final SignupUseCase signupUseCase;
    private final VerifySignupUseCase verifySignupUseCase;
    private final LogoutUseCase logoutUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final ForgotPasswordService forgotPasswordService;

    @PostMapping("/login-google")
    public ResponseEntity<AuthResponse> loginWithGoogle(@RequestBody Map<String, String> request) {
        String idToken = request.get("idToken");
        AuthResponse response = loginUseCase.execute(idToken);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = loginWithPasswordUseCase.execute(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        SignupResponse response = signupUseCase.execute(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/verify")
    public ResponseEntity<Void> verify(
            @RequestParam("token") String token,
            @RequestParam("email") String email) {
        String redirectUrl = verifySignupUseCase.execute(token, email);
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, redirectUrl)
                .build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        AuthResponse response = refreshTokenUseCase.execute(refreshToken);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            logoutUseCase.execute(token);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Email is required"));
        }
        forgotPasswordService.requestPasswordReset(email.trim().toLowerCase());
        return ResponseEntity.ok(ApiResponse.success(null, "If this email exists, a reset link has been sent"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String email = request.get("email");
        String newPassword = request.get("newPassword");

        if (token == null || email == null || newPassword == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Token, email, and newPassword are required"));
        }

        if (newPassword.length() < 8) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Password must be at least 8 characters"));
        }

        try {
            forgotPasswordService.resetPassword(token, email.trim().toLowerCase(), newPassword);
            return ResponseEntity.ok(ApiResponse.success(null, "Password has been reset successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}

