package com.renaissancerentals.mail.external;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Optional;
import java.util.Properties;

import org.springframework.mail.javamail.MimeMessageHelper;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.renaissancerentals.mail.config.MailConfigProperties;
import com.renaissancerentals.mail.error.MailBusinessException;
import com.renaissancerentals.mail.error.MailErrorCode;
import com.renaissancerentals.mail.error.MailServerException;
import com.renaissancerentals.mail.model.MailMessage;
import com.renaissancerentals.mail.service.MailService;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GmailAdapter implements MailService {

    private static final String USER_ID = "me";

    private final MailConfigProperties config;
    private final Gmail gmail;

    public GmailAdapter(MailConfigProperties config, GmailFactory gmailFactory) {
        this.config = config;
        this.gmail = gmailFactory.create();
    }

    @Override
    public void sendMail(MailMessage mailMessage,String messageBody){
        try {
            log.debug("Sending email via Gmail: {}",mailMessage);

            MimeMessage mimeMessage = buildMimeMessage(mailMessage,messageBody);
            Message message = encodeMimeMessage(mimeMessage);

            gmail.users().messages().send(USER_ID,message).execute();
        } catch (IOException | MessagingException e) {
            throw new MailServerException(MailErrorCode.MAIL_SEND_ERROR, e);
        }
    }

    private MimeMessage buildMimeMessage(MailMessage mail,String body)
            throws MessagingException, UnsupportedEncodingException{
        MimeMessage message = new MimeMessage(Session.getDefaultInstance(new Properties(),null));
        MimeMessageHelper helper = new MimeMessageHelper(message, false);

        helper.setTo(mail.to());

        Optional.ofNullable(mail.cc()).or(() -> Optional.ofNullable(config.cc())).ifPresent(cc -> {
            try {
                helper.setCc(cc);
            } catch (MessagingException e) {
                throw new MailBusinessException(MailErrorCode.INVALID_EMAIL_ADDRESS, e);
            }
        });

        if (config.fromName() != null) {
            helper.setFrom(config.from(),config.fromName());
        } else {
            helper.setFrom(config.from());
        }

        helper.setSubject(mail.subject());
        helper.setText(body);
        Optional.ofNullable(mail.replyTo()).ifPresent(replyTo -> {
            try {
                helper.setReplyTo(replyTo);
            } catch (MessagingException e) {
                throw new MailBusinessException(MailErrorCode.INVALID_EMAIL_ADDRESS, e);
            }
        });

        return helper.getMimeMessage();
    }

    private Message encodeMimeMessage(MimeMessage emailContent) throws IOException, MessagingException{
        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            emailContent.writeTo(buffer);
            String encodedEmail = Base64.getUrlEncoder().encodeToString(buffer.toByteArray());
            Message message = new Message();
            message.setRaw(encodedEmail);
            return message;
        }
    }
}
