package com.group05.TC_LLM_Generator.infrastructure.external;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.group05.TC_LLM_Generator.application.dto.GoogleUserDto;
import com.group05.TC_LLM_Generator.application.port.out.VerifyGoogleTokenPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class GoogleVerifierAdapter implements VerifyGoogleTokenPort {

    @Value("${google.client-id}")
    private String GOOGLE_CLIENT_ID;

    @Override
    public GoogleUserDto verifyGoogleToken(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(),
                    new GsonFactory())
                    .setAudience(Collections.singletonList(GOOGLE_CLIENT_ID))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                return GoogleUserDto.builder()
                        .email(payload.getEmail())
                        .name((String) payload.get("name"))
                        .pictureUrl((String) payload.get("picture"))
                        .build();
            } else {
                throw new RuntimeException("Invalid Google Token");
            }
        } catch (Exception e) {
            throw new RuntimeException("Token verification failed: " + e.getMessage(), e);
        }
    }
}
