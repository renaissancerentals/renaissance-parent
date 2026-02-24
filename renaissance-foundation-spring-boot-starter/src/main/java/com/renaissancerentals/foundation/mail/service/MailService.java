package com.renaissancerentals.foundation.mail.service;

import com.renaissancerentals.foundation.mail.model.MailMessage;

public interface MailService {
    // Create a separate service [as composition] to send mail. Have separate config for cc, to etc.
    void sendMail(final MailMessage mailMessage,final String messageBody);
    void sendHtmlMail(final MailMessage mailMessage,final String messageBody);
}
