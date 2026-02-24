package com.renaissancerentals.api.template;

import org.springframework.stereotype.Component;

import com.renaissancerentals.api.domain.template.ContactAcknowledgementMail;
import com.renaissancerentals.foundation.template.AbstractTemplate;

import freemarker.template.Configuration;

@Component
public class ContactAcknowledgementMailTemplate extends AbstractTemplate<ContactAcknowledgementMail> {
    protected ContactAcknowledgementMailTemplate(Configuration freemarkerConfiguration) {
        super(freemarkerConfiguration);
    }

    @Override
    public String getTemplateName(){
        return "contact-acknowledgement-mail.ftl";
    }

    @Override
    public Class<ContactAcknowledgementMail> getModelType(){
        return ContactAcknowledgementMail.class;
    }
}
