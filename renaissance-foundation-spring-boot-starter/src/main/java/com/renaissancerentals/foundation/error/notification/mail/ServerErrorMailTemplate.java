package com.renaissancerentals.foundation.error.notification.mail;

import org.springframework.stereotype.Component;

import com.renaissancerentals.foundation.error.notification.mail.model.ServerErrorMessage;
import com.renaissancerentals.foundation.mail.template.AbstractMailTemplate;

import freemarker.template.Configuration;

@Component
public class ServerErrorMailTemplate extends AbstractMailTemplate<ServerErrorMessage> {
    public ServerErrorMailTemplate(Configuration freemarkerConfiguration) {
        super(freemarkerConfiguration);
    }

    @Override
    public String getTemplateName(){
        return "server-error-mail.ftl";
    }
}
