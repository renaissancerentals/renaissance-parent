package com.renaissancerentals.foundation.template.config;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.renaissancerentals.foundation.template.Template;
import com.renaissancerentals.foundation.template.TemplateMessageFactory;
import com.renaissancerentals.foundation.template.TemplateRegistry;
import com.renaissancerentals.foundation.template.mail.DefaultMailTemplate;

@Configuration
public class TemplateAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public DefaultMailTemplate defaultMailTemplate(freemarker.template.Configuration freemarkerConfiguration){
        return new DefaultMailTemplate(freemarkerConfiguration);
    }

    @Bean
    @ConditionalOnMissingBean
    public TemplateMessageFactory mailMessageFactory(TemplateRegistry templateRegistry){
        return new TemplateMessageFactory(templateRegistry);
    }

    @Bean
    @ConditionalOnMissingBean
    public TemplateRegistry mailTemplateRegistry(List<Template<?>> availableTemplates){
        return new TemplateRegistry(availableTemplates);
    }

}
