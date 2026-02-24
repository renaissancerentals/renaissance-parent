package com.renaissancerentals.foundation.mail.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.renaissancerentals.foundation.mail.external.GmailAdapter;
import com.renaissancerentals.foundation.mail.external.GmailFactory;
import com.renaissancerentals.foundation.mail.service.MailService;

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
}
