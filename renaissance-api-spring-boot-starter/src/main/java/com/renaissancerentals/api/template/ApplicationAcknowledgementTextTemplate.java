package com.renaissancerentals.api.template;

import com.renaissancerentals.api.domain.template.ApplicationAcknowledgementText;
import com.renaissancerentals.foundation.template.AbstractTemplate;
import freemarker.template.Configuration;
import org.springframework.stereotype.Component;

@Component
public class ApplicationAcknowledgementTextTemplate extends AbstractTemplate<ApplicationAcknowledgementText> {
    protected ApplicationAcknowledgementTextTemplate(Configuration freemarkerConfiguration) {
        super(freemarkerConfiguration);
    }

    @Override
    public String getTemplateName() {
        return "application-acknowledgement-text.ftl";
    }

    @Override
    public Class<ApplicationAcknowledgementText> getModelType() {
        return ApplicationAcknowledgementText.class;
    }
}
