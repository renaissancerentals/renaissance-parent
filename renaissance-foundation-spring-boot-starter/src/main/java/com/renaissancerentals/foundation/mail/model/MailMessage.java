package com.renaissancerentals.foundation.mail.model;

import java.util.List;
import lombok.Builder;

@Builder
public record MailMessage(String to, String replyTo, List<String> cc, String subject) {
    public MailMessage {
        if (cc != null) {
            cc = List.copyOf(cc); // defensive copy
        }
    }

    @Override
    public List<String> cc() {
        return cc == null ? List.of() : List.copyOf(cc);
    }
}
