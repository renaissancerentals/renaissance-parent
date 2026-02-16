package com.renaissancerentals.api.domain.mail;

import lombok.Builder;

@Builder
public record SubletEmailMessage(String ownerName, String subletTitle, String messenger, String messengerEmail,
        String message) {
}
