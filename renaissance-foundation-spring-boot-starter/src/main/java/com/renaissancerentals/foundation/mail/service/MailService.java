package com.renaissancerentals.foundation.mail.service;

import com.renaissancerentals.foundation.mail.model.MailMessage;

public interface MailService {
    void sendMail(final MailMessage mailMessage,final String messageBody);
}
