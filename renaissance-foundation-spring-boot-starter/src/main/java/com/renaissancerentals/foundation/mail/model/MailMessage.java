package com.renaissancerentals.foundation.mail.model;

import lombok.Builder;

@Builder
public record MailMessage(String to, String replyTo, String cc, String subject) {
}
