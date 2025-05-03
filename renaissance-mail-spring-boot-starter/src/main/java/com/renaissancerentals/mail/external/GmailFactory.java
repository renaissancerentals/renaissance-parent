package com.renaissancerentals.mail.external;

import static com.google.api.client.googleapis.javanet.GoogleNetHttpTransport.newTrustedTransport;

import java.io.IOException;
import java.security.GeneralSecurityException;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.renaissancerentals.mail.config.MailConfigProperties;
import com.renaissancerentals.mail.error.MailErrorCode;
import com.renaissancerentals.mail.error.MailServerException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GmailFactory {

    private final MailConfigProperties config;

    public Gmail create(){
        try {
            TokenResponse tokenResponse = new TokenResponse().setRefreshToken(config.refreshToken());
            Credential credential = new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
                    .setTransport(newTrustedTransport()).setJsonFactory(GsonFactory.getDefaultInstance())
                    .setTokenServerUrl(new GenericUrl(config.tokenServer()))
                    .setClientAuthentication(new BasicAuthentication(config.clientId(), config.clientSecret())).build()
                    .setFromTokenResponse(tokenResponse);

            return new Gmail.Builder(newTrustedTransport(), GsonFactory.getDefaultInstance(), credential)
                    .setApplicationName("renaissance-mail").build();
        } catch (GeneralSecurityException | IOException e) {
            throw new MailServerException(MailErrorCode.GMAIL_INITIALIZATION_ERROR, e);
        }
    }
}
