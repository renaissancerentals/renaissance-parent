package com.renaissancerentals.monitor.template;

import org.springframework.stereotype.Component;

import com.renaissancerentals.mail.template.AbstractMailTemplate;
import com.renaissancerentals.monitor.template.model.MonitorHealthyMessage;

import freemarker.template.Configuration;

@Component
public class MonitorHealthyMailTemplate extends AbstractMailTemplate<MonitorHealthyMessage> {
    protected MonitorHealthyMailTemplate(Configuration freemarkerConfiguration) {
        super(freemarkerConfiguration);
    }

    @Override
    public String getTemplateName(){
        return "monitor-healthy-mail.ftl";
    }
}
