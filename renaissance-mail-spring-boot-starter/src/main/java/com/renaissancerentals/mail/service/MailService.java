package com.renaissancerentals.mail.service;

import com.renaissancerentals.mail.model.MailMessage;

public interface MailService {
    void sendMail(final MailMessage mailMessage,final String messageBody);
}
