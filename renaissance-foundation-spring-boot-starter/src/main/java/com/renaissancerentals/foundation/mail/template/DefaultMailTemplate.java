package com.renaissancerentals.foundation.mail.template;

import com.renaissancerentals.foundation.mail.template.model.DefaultMessage;

import freemarker.template.Configuration;

public class DefaultMailTemplate extends AbstractMailTemplate<DefaultMessage> {
    public DefaultMailTemplate(Configuration freemarkerConfiguration) {
        super(freemarkerConfiguration);
    }

    @Override
    public String getTemplateName(){
        return "default-mail.ftl";
    }
}
