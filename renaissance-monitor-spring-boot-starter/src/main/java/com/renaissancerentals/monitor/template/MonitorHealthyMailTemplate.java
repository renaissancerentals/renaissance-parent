package com.renaissancerentals.monitor.template;

import com.renaissancerentals.foundation.template.AbstractTemplate;
import com.renaissancerentals.monitor.template.model.MonitorHealthyMessage;
import freemarker.template.Configuration;
import org.springframework.stereotype.Component;

@Component
public class MonitorHealthyMailTemplate extends AbstractTemplate<MonitorHealthyMessage> {
    protected MonitorHealthyMailTemplate(Configuration freemarkerConfiguration) {
        super(freemarkerConfiguration);
    }

    @Override
    public String getTemplateName() {
        return "monitor-healthy-mail.ftl";
    }

    @Override
    public Class<MonitorHealthyMessage> getModelType() {
        return MonitorHealthyMessage.class;
    }
}
