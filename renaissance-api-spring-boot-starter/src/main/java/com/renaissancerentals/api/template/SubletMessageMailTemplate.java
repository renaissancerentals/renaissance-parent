package com.renaissancerentals.api.template;

import com.renaissancerentals.api.domain.template.SubletEmailMessage;
import com.renaissancerentals.foundation.template.AbstractTemplate;
import freemarker.template.Configuration;
import org.springframework.stereotype.Component;

@Component
public class SubletMessageMailTemplate extends AbstractTemplate<SubletEmailMessage> {
    protected SubletMessageMailTemplate(Configuration freemarkerConfiguration) {
        super(freemarkerConfiguration);
    }

    @Override
    public String getTemplateName() {
        return "sublet-message-mail.ftl";
    }

    @Override
    public Class<SubletEmailMessage> getModelType() {
        return SubletEmailMessage.class;
    }
}
