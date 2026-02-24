package com.renaissancerentals.api.domain.template;

import lombok.Builder;

@Builder
public record SubletEmailMessage(String ownerName, String subletTitle, String messenger, String messengerEmail,
        String message) {
}
