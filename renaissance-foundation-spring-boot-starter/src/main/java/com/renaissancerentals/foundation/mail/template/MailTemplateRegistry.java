package com.renaissancerentals.foundation.mail.template;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.core.GenericTypeResolver;

public class MailTemplateRegistry {
    private final Map<Class<?>, MailTemplate<?>> templates;

    public MailTemplateRegistry(List<MailTemplate<?>> availableTemplates) {
        this.templates = availableTemplates.stream().collect(Collectors.toMap(
                t -> GenericTypeResolver.resolveTypeArgument(t.getClass(),MailTemplate.class),Function.identity()));
    }

    @SuppressWarnings("unchecked")
    public <T> MailTemplate<T> getTemplate(Class<T> type){
        return (MailTemplate<T>) templates.get(type);
    }
}
