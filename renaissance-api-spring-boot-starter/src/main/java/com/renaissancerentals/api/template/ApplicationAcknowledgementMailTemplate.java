package com.renaissancerentals.api.template;

import com.renaissancerentals.api.domain.template.ApplicationAcknowledgementMail;
import com.renaissancerentals.foundation.template.AbstractTemplate;
import freemarker.template.Configuration;
import org.springframework.stereotype.Component;

@Component
public class ApplicationAcknowledgementMailTemplate extends AbstractTemplate<ApplicationAcknowledgementMail> {
    protected ApplicationAcknowledgementMailTemplate(Configuration freemarkerConfiguration) {
        super(freemarkerConfiguration);
    }

    @Override
    public String getTemplateName() {
        return "application-acknowledgement-mail.ftl";
    }

    @Override
    public Class<ApplicationAcknowledgementMail> getModelType() {
        return ApplicationAcknowledgementMail.class;
    }
}
