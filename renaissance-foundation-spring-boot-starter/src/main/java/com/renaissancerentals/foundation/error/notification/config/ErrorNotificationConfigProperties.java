package com.renaissancerentals.foundation.error.notification.config;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.renaissancerentals.foundation.mail.error.MailServerException;

@ConfigurationProperties(prefix = "renaissancerentals.error.notification")
public record ErrorNotificationConfigProperties(boolean enabled, String emailTo, List<String> emailCc,
        String titleFormat, Set<String> excludedServerExceptions) {

    private static final Set<String> EXCLUDED_SERVER_EXCEPTIONS = Set.of(MailServerException.class.getName());

    public ErrorNotificationConfigProperties {
        if (emailCc == null) {
            emailCc = List.of();
        } else {
            emailCc = List.copyOf(emailCc); // defensive copy
        }
        if (excludedServerExceptions == null) {
            excludedServerExceptions = new HashSet<>();
        } else {
            excludedServerExceptions = new HashSet<>(excludedServerExceptions);
        }
        excludedServerExceptions.addAll(EXCLUDED_SERVER_EXCEPTIONS);
    }

    @Override
    public Set<String> excludedServerExceptions(){
        return Collections.unmodifiableSet(excludedServerExceptions);
    }

    @Override
    public List<String> emailCc(){
        return Collections.unmodifiableList(emailCc);
    }
}
