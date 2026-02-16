package com.renaissancerentals.api.template;

import org.springframework.stereotype.Component;

import com.renaissancerentals.api.messaging.ContactMessageRequest;
import com.renaissancerentals.foundation.mail.template.AbstractMailTemplate;

import freemarker.template.Configuration;

@Component
public class ContactMessageMailTemplate extends AbstractMailTemplate<ContactMessageRequest> {
    protected ContactMessageMailTemplate(Configuration freemarkerConfiguration) {
        super(freemarkerConfiguration);
    }

    @Override
    public String getTemplateName(){
        return "contact-message-mail.ftl";
    }
}
