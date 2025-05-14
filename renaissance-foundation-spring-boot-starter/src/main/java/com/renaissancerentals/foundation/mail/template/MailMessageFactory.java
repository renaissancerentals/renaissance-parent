package com.renaissancerentals.foundation.mail.template;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MailMessageFactory {

    private final MailTemplateRegistry mailTemplateRegistry;

    @SuppressWarnings("unchecked")
    public <T> String createMessage(T data){
        MailTemplate<T> template = mailTemplateRegistry.getTemplate((Class<T>) data.getClass());
        return template.render(data);
    }
}
