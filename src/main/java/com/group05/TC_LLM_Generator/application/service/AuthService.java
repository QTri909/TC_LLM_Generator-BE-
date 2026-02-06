package com.group05.TC_LLM_Generator.application.service;

import com.group05.TC_LLM_Generator.application.dto.AuthResponse;
import com.group05.TC_LLM_Generator.application.dto.GoogleUserDto;
import com.group05.TC_LLM_Generator.application.port.in.LoginUseCase;
import com.group05.TC_LLM_Generator.application.port.out.VerifyGoogleTokenPort;
import com.group05.TC_LLM_Generator.domain.model.entity.User;
import com.group05.TC_LLM_Generator.domain.repository.UserRepo;
import com.group05.TC_LLM_Generator.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService implements LoginUseCase {

    private final UserRepo userRepo;
    private final JwtTokenProvider jwtTokenProvider;
    private final VerifyGoogleTokenPort verifyGoogleTokenPort;

    @Override
    @Transactional
    public AuthResponse loginWithGoogle(String idTokenString) {
        // 1. Verify Google Token via Output Port
        GoogleUserDto googleUser = verifyGoogleTokenPort.verifyGoogleToken(idTokenString);

        // 2. Find User or Create
        User user = userRepo.findByEmail(googleUser.getEmail())
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(googleUser.getEmail())
                            .name(googleUser.getName())
                            .provider("GOOGLE")
                            // You might want to default status to ACTIVE
                            .status("ACTIVE")
                            .build();
                    return userRepo.save(newUser);
                });

        Map<String, String> data = new HashMap<>();
        data.put("email", user.getEmail());
        data.put("name", user.getName());
        // 3. Generate Tokens
        String accessToken = jwtTokenProvider.generateAccessToken(data);
        String refreshToken = jwtTokenProvider.generateRefreshToken(data);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
