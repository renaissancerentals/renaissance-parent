package com.renaissancerentals.api.template;

import org.springframework.stereotype.Component;

import com.renaissancerentals.api.domain.Sublet;
import com.renaissancerentals.foundation.mail.template.AbstractMailTemplate;

import freemarker.template.Configuration;

@Component
public class SubletAlertMailTemplate extends AbstractMailTemplate<Sublet> {
    protected SubletAlertMailTemplate(Configuration freemarkerConfiguration) {
        super(freemarkerConfiguration);
    }

    @Override
    public String getTemplateName(){
        return "sublet-alert-mail.ftl";
    }
}
