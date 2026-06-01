package com.renaissancerentals.foundation.template;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TemplateMessageFactory {

    private final TemplateRegistry templateRegistry;

    public <T> String createMessage(T data) {
        Template<T> template = templateRegistry.getTemplate(data);
        return template.render(data);
    }
}
