package com.renaissancerentals.foundation.error.notification.mail;

import com.renaissancerentals.foundation.error.notification.mail.model.ServerErrorMessage;
import com.renaissancerentals.foundation.template.AbstractTemplate;
import freemarker.template.Configuration;
import org.springframework.stereotype.Component;

@Component
public class ServerErrorMailTemplate extends AbstractTemplate<ServerErrorMessage> {
    public ServerErrorMailTemplate(Configuration freemarkerConfiguration) {
        super(freemarkerConfiguration);
    }

    @Override
    public String getTemplateName() {
        return "server-error-mail.ftl";
    }

    @Override
    public Class<ServerErrorMessage> getModelType() {
        return ServerErrorMessage.class;
    }
}
