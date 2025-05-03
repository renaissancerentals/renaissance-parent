package com.renaissancerentals.mail.model;

import lombok.Builder;

@Builder
public record MailMessage(String to, String replyTo, String cc, String subject) {
}
