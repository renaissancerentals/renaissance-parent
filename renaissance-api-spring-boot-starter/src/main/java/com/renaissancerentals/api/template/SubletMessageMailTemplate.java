package com.renaissancerentals.api.template;

import org.springframework.stereotype.Component;

import com.renaissancerentals.api.domain.mail.SubletEmailMessage;
import com.renaissancerentals.foundation.mail.template.AbstractMailTemplate;

import freemarker.template.Configuration;

@Component
public class SubletMessageMailTemplate extends AbstractMailTemplate<SubletEmailMessage> {
    protected SubletMessageMailTemplate(Configuration freemarkerConfiguration) {
        super(freemarkerConfiguration);
    }

    @Override
    public String getTemplateName(){
        return "sublet-message-mail.ftl";
    }
}
