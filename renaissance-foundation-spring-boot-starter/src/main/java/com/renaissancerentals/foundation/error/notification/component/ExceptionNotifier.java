package com.renaissancerentals.foundation.error.notification.component;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.Arrays;

import com.renaissancerentals.foundation.error.BaseException;
import com.renaissancerentals.foundation.error.notification.config.ErrorNotificationConfigProperties;
import com.renaissancerentals.foundation.error.notification.mail.model.ServerErrorMessage;
import com.renaissancerentals.foundation.mail.model.MailMessage;
import com.renaissancerentals.foundation.mail.service.MailService;
import com.renaissancerentals.foundation.ratelimiter.RateLimiter;
import com.renaissancerentals.foundation.template.TemplateMessageFactory;

import lombok.RequiredArgsConstructor;

/**
 * Email based exception notifier. Only works with exceptions that extends BaseException. User rate limiter, so only
 * notifies based on ratelimiter parameters
 *
 * @param <T>
 *            extends BaseException
 * @see com.renaissancerentals.foundation.ratelimiter.RateLimiter
 */
@RequiredArgsConstructor
public class ExceptionNotifier<T extends BaseException> {
    private final ErrorNotificationConfigProperties properties;
    private final TemplateMessageFactory templateMessageFactory;
    private final MailService mailService;
    private final RateLimiter rateLimiter;

    public void notifyException(final T notifyingException){
        final var isNotificationEnabled = properties.enabled();
        if (!isNotificationEnabled)
            return;
        if (isExcluded(notifyingException))
            return;

        if (!rateLimiter.shouldTrigger(notifyingException.getClass().getName()))
            return;

        var serverErrorMessage = messageFrom(notifyingException);
        final var messageBody = templateMessageFactory.createMessage(serverErrorMessage);
        final var title = MessageFormat.format(properties.titleFormat(),
                notifyingException.getErrorMessage().application());

        var mailMessage = MailMessage.builder().subject(title).to(properties.emailTo()).cc(properties.emailCc())
                .build();

        mailService.sendMail(mailMessage,messageBody);

    }

    private boolean isExcluded(final T notifyingException){
        return properties.excludedServerExceptions().contains(notifyingException.getClass().getName());
    }

    private ServerErrorMessage messageFrom(final T notifyingException){
        if (notifyingException == null) {
            return ServerErrorMessage.builder().message("Unknown error").timeStamp(Instant.now().toString())
                    .sourceName("UnknownException").exceptionClass("java.lang.NullPointerException")
                    .errorCode("UNKNOWN").stackTrace("Exception was null").build();
        }

        final var errorMessage = notifyingException.getErrorMessage();
        final var message = errorMessage != null ? errorMessage.message() : "No message";
        final var errorCode = errorMessage != null ? errorMessage.code() : "UNKNOWN";

        return ServerErrorMessage.builder().message(message != null ? message : "No message")
                .timeStamp(Instant.now().toString()).sourceName(notifyingException.getClass().getSimpleName())
                .exceptionClass(notifyingException.getClass().getName())
                .errorCode(errorCode != null ? errorCode : "UNKNOWN")
                .stackTrace(Arrays.toString(notifyingException.getStackTrace())).build();
    }
}
