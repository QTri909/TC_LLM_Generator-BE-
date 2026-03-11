package com.group05.TC_LLM_Generator.application.port.in.authen;

public interface VerifySignupUseCase {
    String execute(String token, String email);
}
