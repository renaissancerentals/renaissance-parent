package com.renaissancerentals.mail;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.renaissancerentals.mail.model.MailMessage;
import com.renaissancerentals.mail.service.MailService;
import com.renaissancerentals.mail.template.MailMessageFactory;
import com.renaissancerentals.mail.template.model.DefaultMessage;

@SpringBootTest
class MailApplicationTests {
    @Autowired
    private MailService mailService;
    @Autowired
    private MailMessageFactory mailMessageFactory;

    @Test
    @Disabled("Only enable to test the working on email")
    void contextLoads(){
        var defaultMessage = new DefaultMessage("Muncher", "Test mail");

        var message = mailMessageFactory.createMessage(defaultMessage);
        mailService.sendMail(MailMessage.builder().subject("Hello World!").cc("admin@contentmunch.com")
                .to("asikpradhan@gmail.com").build(),message);
    }

}
