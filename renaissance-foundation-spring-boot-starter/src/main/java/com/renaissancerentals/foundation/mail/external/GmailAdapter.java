package com.renaissancerentals.foundation.mail.external;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import java.util.Properties;

import org.springframework.mail.javamail.MimeMessageHelper;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.renaissancerentals.foundation.mail.config.MailConfigProperties;
import com.renaissancerentals.foundation.mail.error.MailBusinessException;
import com.renaissancerentals.foundation.mail.error.MailErrorCode;
import com.renaissancerentals.foundation.mail.error.MailServerException;
import com.renaissancerentals.foundation.mail.model.MailMessage;
import com.renaissancerentals.foundation.mail.service.MailService;

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
    public void sendMail(MailMessage mailMessage,String body){
        send(body,mailMessage,false);
    }

    @Override
    public void sendHtmlMail(MailMessage mailMessage,String body){
        send(body,mailMessage,true);
    }

    private void send(String body,MailMessage mail,boolean isHtml){
        try {
            MimeMessage mimeMessage = buildMimeMessage(mail,body,isHtml);
            Message message = encodeMimeMessage(mimeMessage);
            gmail.users().messages().send(USER_ID,message).execute();
        } catch (IOException | MessagingException e) {
            throw new MailServerException(MailErrorCode.MAIL_SEND_ERROR, e);
        }
    }

    private MimeMessage buildMimeMessage(MailMessage mail,String body,boolean isHtml)
            throws MessagingException, UnsupportedEncodingException{
        MimeMessage message = new MimeMessage(Session.getDefaultInstance(new Properties(),null));
        MimeMessageHelper helper = isHtml
                ? new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name())
                : new MimeMessageHelper(message, false);

        helper.setTo(mail.to());

        Optional.ofNullable(mail.cc()).ifPresent(cc -> {
            try {
                helper.setCc(cc.toArray(String[]::new));
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
        helper.setText(body,isHtml);
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
