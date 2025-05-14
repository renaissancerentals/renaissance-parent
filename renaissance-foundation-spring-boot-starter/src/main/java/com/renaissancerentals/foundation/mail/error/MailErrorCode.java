package com.renaissancerentals.foundation.mail.error;

public enum MailErrorCode {
    INVALID_EMAIL_ADDRESS("Email Address is invalid"), GMAIL_INITIALIZATION_ERROR(
            "Gmail Initialization Error"), MAIL_SEND_ERROR(
                    "Mail Send Error"), MAIL_TEMPLATE_ERROR("Mail Template Error");

    private final String message;

    MailErrorCode(String message) {
        this.message = message;
    }

    public String message(){
        return message;
    }
}
