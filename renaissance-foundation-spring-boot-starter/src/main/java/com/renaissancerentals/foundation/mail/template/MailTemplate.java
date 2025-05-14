package com.renaissancerentals.foundation.mail.template;

public interface MailTemplate<T> {
    String getTemplateName();

    String render(T model);
}
