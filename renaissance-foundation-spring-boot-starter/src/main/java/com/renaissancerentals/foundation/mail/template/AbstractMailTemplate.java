package com.renaissancerentals.foundation.mail.template;

import java.io.IOException;
import java.util.Map;

import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.renaissancerentals.foundation.mail.error.MailErrorCode;
import com.renaissancerentals.foundation.mail.error.MailServerException;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public abstract class AbstractMailTemplate<T> implements MailTemplate<T> {
    private final Configuration freemarkerConfiguration;

    protected AbstractMailTemplate(Configuration freemarkerConfiguration) {
        this.freemarkerConfiguration = freemarkerConfiguration;
    }

    @Override
    public String render(T data){
        try {
            Template template = freemarkerConfiguration.getTemplate(getTemplateName());
            return FreeMarkerTemplateUtils.processTemplateIntoString(template,Map.of("data",data));
        } catch (IOException | TemplateException e) {
            throw new MailServerException(MailErrorCode.MAIL_TEMPLATE_ERROR, e);
        }
    }
}
