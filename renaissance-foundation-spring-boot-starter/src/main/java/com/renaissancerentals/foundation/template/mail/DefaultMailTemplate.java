package com.renaissancerentals.foundation.template.mail;

import com.renaissancerentals.foundation.template.AbstractTemplate;
import com.renaissancerentals.foundation.template.mail.model.DefaultMessage;

import freemarker.template.Configuration;

public class DefaultMailTemplate extends AbstractTemplate<DefaultMessage> {
    public DefaultMailTemplate(Configuration freemarkerConfiguration) {
        super(freemarkerConfiguration);
    }

    @Override
    public String getTemplateName(){
        return "default-mail.ftl";
    }

    @Override
    public Class<DefaultMessage> getModelType(){
        return DefaultMessage.class;
    }
}
