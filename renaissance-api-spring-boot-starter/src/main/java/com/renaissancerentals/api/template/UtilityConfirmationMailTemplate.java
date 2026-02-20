package com.renaissancerentals.api.template;

import org.springframework.stereotype.Component;

import com.renaissancerentals.api.messaging.UtilityConfirmationRequest;
import com.renaissancerentals.foundation.mail.template.AbstractMailTemplate;

import freemarker.template.Configuration;

@Component
public class UtilityConfirmationMailTemplate extends AbstractMailTemplate<UtilityConfirmationRequest> {
    protected UtilityConfirmationMailTemplate(Configuration freemarkerConfiguration) {
        super(freemarkerConfiguration);
    }

    @Override
    public String getTemplateName(){
        return "utility-confirmation-mail.ftl";
    }

    @Override
    public Class<UtilityConfirmationRequest> getModelType() {
        return UtilityConfirmationRequest.class;
    }
}
