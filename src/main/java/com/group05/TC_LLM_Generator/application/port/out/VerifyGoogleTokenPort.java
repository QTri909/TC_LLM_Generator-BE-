package com.group05.TC_LLM_Generator.application.port.out;

import com.group05.TC_LLM_Generator.application.dto.GoogleUserDto;

public interface VerifyGoogleTokenPort {
    GoogleUserDto verifyGoogleToken(String idTokenString);
}
