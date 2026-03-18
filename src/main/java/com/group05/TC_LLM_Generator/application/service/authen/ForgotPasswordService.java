package com.group05.TC_LLM_Generator.application.service.authen;

import com.group05.TC_LLM_Generator.application.port.out.UserRepositoryPort;
import com.group05.TC_LLM_Generator.application.port.out.authen.EmailSenderPort;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ForgotPasswordService {

    private static final String REDIS_KEY_PREFIX = "pwd-reset:";
    private static final long TOKEN_TTL_MINUTES = 15;

    private final UserRepositoryPort userRepository;
    private final EmailSenderPort emailSender;
    private final RedisTemplate<String, String> redisTemplate;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public ForgotPasswordService(
            UserRepositoryPort userRepository,
            EmailSenderPort emailSender,
            @Qualifier("redisStringTemplate") RedisTemplate<String, String> redisTemplate,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.emailSender = emailSender;
        this.redisTemplate = redisTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Request a password reset. Always returns success (security: don't leak email existence).
     */
    public void requestPasswordReset(String email) {
        Optional<UserEntity> optUser = userRepository.findByEmail(email);
        if (optUser.isEmpty()) {
            log.info("Password reset requested for non-existent email: {}", email);
            return; // don't reveal email doesn't exist
        }

        UserEntity user = optUser.get();

        // Google OAuth users can't reset password
        if ("GOOGLE".equalsIgnoreCase(user.getAuthProvider())) {
            log.info("Password reset requested for Google OAuth user: {}", email);
            return;
        }

        String token = UUID.randomUUID().toString();
        String redisKey = REDIS_KEY_PREFIX + token;
        redisTemplate.opsForValue().set(redisKey, email, TOKEN_TTL_MINUTES, TimeUnit.MINUTES);

        String resetUrl = frontendUrl + "/reset-password?token=" + token + "&email=" + email;
        emailSender.sendPasswordResetEmail(email, user.getFullName(), resetUrl);

        log.info("Password reset email sent to {}", email);
    }

    /**
     * Reset password using a valid token.
     */
    public void resetPassword(String token, String email, String newPassword) {
        String redisKey = REDIS_KEY_PREFIX + token;
        String storedEmail = redisTemplate.opsForValue().get(redisKey);

        if (storedEmail == null) {
            throw new IllegalArgumentException("Invalid or expired reset token");
        }

        if (!storedEmail.equals(email)) {
            throw new IllegalArgumentException("Token does not match the provided email");
        }

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Remove used token
        redisTemplate.delete(redisKey);

        log.info("Password successfully reset for {}", email);
    }
}
