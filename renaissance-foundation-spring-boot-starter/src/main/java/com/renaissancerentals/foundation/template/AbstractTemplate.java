package com.renaissancerentals.foundation.template;

import java.io.IOException;
import java.util.Map;

import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.renaissancerentals.foundation.mail.error.MailErrorCode;
import com.renaissancerentals.foundation.mail.error.MailServerException;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;

public abstract class AbstractTemplate<T> implements Template<T> {
    private final Configuration freemarkerConfiguration;

    protected AbstractTemplate(Configuration freemarkerConfiguration) {
        this.freemarkerConfiguration = freemarkerConfiguration;
    }

    @Override
    public String render(T data){
        try {
            freemarker.template.Template template = freemarkerConfiguration.getTemplate(getTemplateName());
            return FreeMarkerTemplateUtils.processTemplateIntoString(template,Map.of("data",data));
        } catch (IOException | TemplateException e) {
            throw new MailServerException(MailErrorCode.MAIL_TEMPLATE_ERROR, e);
        }
    }
}
