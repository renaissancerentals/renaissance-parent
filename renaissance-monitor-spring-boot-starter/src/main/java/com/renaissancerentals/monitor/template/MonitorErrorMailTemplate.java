package com.renaissancerentals.monitor.template;

import com.renaissancerentals.foundation.template.AbstractTemplate;
import com.renaissancerentals.monitor.template.model.MonitorErrorMessage;
import freemarker.template.Configuration;
import org.springframework.stereotype.Component;

@Component
public class MonitorErrorMailTemplate extends AbstractTemplate<MonitorErrorMessage> {
    protected MonitorErrorMailTemplate(Configuration freemarkerConfiguration) {
        super(freemarkerConfiguration);
    }

    @Override
    public String getTemplateName() {
        return "monitor-error-mail.ftl";
    }

    @Override
    public Class<MonitorErrorMessage> getModelType() {
        return MonitorErrorMessage.class;
    }
}
