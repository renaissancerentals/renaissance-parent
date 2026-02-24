package com.renaissancerentals.api.template;

import org.springframework.stereotype.Component;

import com.renaissancerentals.api.domain.template.ContactAcknowledgementText;
import com.renaissancerentals.foundation.template.AbstractTemplate;

import freemarker.template.Configuration;

@Component
public class ContactAcknowledgementTextTemplate extends AbstractTemplate<ContactAcknowledgementText> {
    protected ContactAcknowledgementTextTemplate(Configuration freemarkerConfiguration) {
        super(freemarkerConfiguration);
    }

    @Override
    public String getTemplateName(){
        return "contact-acknowledgement-text.ftl";
    }

    @Override
    public Class<ContactAcknowledgementText> getModelType(){
        return ContactAcknowledgementText.class;
    }
}
