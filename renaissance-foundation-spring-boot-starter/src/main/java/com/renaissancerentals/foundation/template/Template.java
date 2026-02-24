package com.renaissancerentals.foundation.template;

public interface Template<T> {
    String getTemplateName();

    String render(T model);

    Class<T> getModelType();
}
