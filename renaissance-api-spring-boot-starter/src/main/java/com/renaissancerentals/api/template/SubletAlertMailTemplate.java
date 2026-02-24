package com.renaissancerentals.api.template;

import org.springframework.stereotype.Component;

import com.renaissancerentals.api.domain.Sublet;
import com.renaissancerentals.foundation.template.AbstractTemplate;

import freemarker.template.Configuration;

@Component
public class SubletAlertMailTemplate extends AbstractTemplate<Sublet> {
    protected SubletAlertMailTemplate(Configuration freemarkerConfiguration) {
        super(freemarkerConfiguration);
    }

    @Override
    public String getTemplateName(){
        return "sublet-alert-mail.ftl";
    }

    @Override
    public Class<Sublet> getModelType(){
        return Sublet.class;
    }
}
