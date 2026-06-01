package com.renaissancerentals.api.template;

import com.renaissancerentals.api.messaging.ContactMessageRequest;
import com.renaissancerentals.foundation.template.AbstractTemplate;
import freemarker.template.Configuration;
import org.springframework.stereotype.Component;

@Component
public class ContactMessageMailTemplate extends AbstractTemplate<ContactMessageRequest> {
    protected ContactMessageMailTemplate(Configuration freemarkerConfiguration) {
        super(freemarkerConfiguration);
    }

    @Override
    public String getTemplateName() {
        return "contact-message-mail.ftl";
    }

    @Override
    public Class<ContactMessageRequest> getModelType() {
        return ContactMessageRequest.class;
    }
}
