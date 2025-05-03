package com.renaissancerentals.mail.config;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.renaissancerentals.mail.external.GmailAdapter;
import com.renaissancerentals.mail.external.GmailFactory;
import com.renaissancerentals.mail.service.MailService;
import com.renaissancerentals.mail.template.DefaultMailTemplate;
import com.renaissancerentals.mail.template.MailMessageFactory;
import com.renaissancerentals.mail.template.MailTemplate;
import com.renaissancerentals.mail.template.MailTemplateRegistry;

@Configuration
@EnableConfigurationProperties(MailConfigProperties.class)
@ConditionalOnProperty(name = "renaissancerentals.mail.enabled", havingValue = "true", matchIfMissing = true)
public class MailAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public GmailFactory gmailFactory(MailConfigProperties config){
        return new GmailFactory(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public MailService mailService(MailConfigProperties config,GmailFactory gmailFactory){
        return new GmailAdapter(config, gmailFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultMailTemplate defaultMailTemplate(freemarker.template.Configuration freemarkerConfiguration){
        return new DefaultMailTemplate(freemarkerConfiguration);
    }

    @Bean
    @ConditionalOnMissingBean
    public MailMessageFactory mailMessageFactory(MailTemplateRegistry mailTemplateRegistry){
        return new MailMessageFactory(mailTemplateRegistry);
    }

    @Bean
    @ConditionalOnMissingBean
    public MailTemplateRegistry mailTemplateRegistry(List<MailTemplate<?>> availableTemplates){
        return new MailTemplateRegistry(availableTemplates);
    }

}
