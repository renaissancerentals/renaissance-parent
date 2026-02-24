package com.renaissancerentals.foundation.template;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TemplateMessageFactory {

    private final TemplateRegistry templateRegistry;

    public <T> String createMessage(T data){
        Template<T> template = templateRegistry.getTemplate(data);
        return template.render(data);
    }
}
