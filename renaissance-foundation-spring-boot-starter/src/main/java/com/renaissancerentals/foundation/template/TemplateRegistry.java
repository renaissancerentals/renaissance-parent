package com.renaissancerentals.foundation.template;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateRegistry {
    private final Map<Class<?>, Template<?>> templates = new HashMap<>();

    public TemplateRegistry(List<Template<?>> availableTemplates) {
        for (Template<?> t : availableTemplates) {
            templates.put(t.getModelType(),t);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> Template<T> getTemplate(T model){
        return (Template<T>) templates.get(model.getClass());
    }
}
