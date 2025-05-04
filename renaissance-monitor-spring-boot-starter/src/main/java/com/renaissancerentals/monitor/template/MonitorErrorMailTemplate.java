package com.renaissancerentals.monitor.template;

import org.springframework.stereotype.Component;

import com.renaissancerentals.mail.template.AbstractMailTemplate;
import com.renaissancerentals.monitor.template.model.MonitorErrorMessage;

import freemarker.template.Configuration;

@Component
public class MonitorErrorMailTemplate extends AbstractMailTemplate<MonitorErrorMessage> {
    protected MonitorErrorMailTemplate(Configuration freemarkerConfiguration) {
        super(freemarkerConfiguration);
    }

    @Override
    public String getTemplateName(){
        return "monitor-error-mail.ftl";
    }
}
