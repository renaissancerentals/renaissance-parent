package com.renaissancerentals.api.template;

import com.renaissancerentals.api.messaging.ApplicationRequest;
import com.renaissancerentals.foundation.template.AbstractTemplate;
import freemarker.template.Configuration;
import org.springframework.stereotype.Component;

@Component
public class ApplicationRequestMailTemplate extends AbstractTemplate<ApplicationRequest> {
    protected ApplicationRequestMailTemplate(Configuration freemarkerConfiguration) {
        super(freemarkerConfiguration);
    }

    @Override
    public String getTemplateName() {
        return "application-request-mail.ftl";
    }

    @Override
    public Class<ApplicationRequest> getModelType() {
        return ApplicationRequest.class;
    }
}
