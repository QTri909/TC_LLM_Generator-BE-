package com.group05.TC_LLM_Generator.application.port.out.authen;

public interface EmailSenderPort {
    void sendVerificationEmail(String toEmail, String fullName, String verificationUrl);

    void sendWorkspaceInvitationEmail(String toEmail, String inviterName, String workspaceName, String acceptUrl);

    void sendPasswordResetEmail(String toEmail, String fullName, String resetUrl);
}
