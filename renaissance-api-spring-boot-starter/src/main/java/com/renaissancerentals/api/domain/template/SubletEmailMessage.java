package com.renaissancerentals.api.domain.template;

import com.renaissancerentals.api.util.StringUtils;

import lombok.Builder;

@Builder
public record SubletEmailMessage(String ownerName, String subletTitle, String messenger, String messengerEmail,
        String message) {
    @Override
    public String ownerName(){
        return StringUtils.capitalizeWords(ownerName);
    }

    @Override
    public String subletTitle(){
        return StringUtils.capitalizeWords(subletTitle);
    }

    @Override
    public String messenger(){
        return StringUtils.capitalizeWords(messenger);
    }
}
