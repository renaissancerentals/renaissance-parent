package com.renaissancerentals.assets.external;

import static com.google.api.client.googleapis.javanet.GoogleNetHttpTransport.newTrustedTransport;
import static com.google.api.client.json.gson.GsonFactory.getDefaultInstance;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.services.drive.Drive;
import com.renaissancerentals.assets.config.AssetsConfigProperties;
import com.renaissancerentals.assets.error.AssetsBusinessException;
import com.renaissancerentals.assets.error.AssetsClientException;
import com.renaissancerentals.assets.error.AssetsErrorCode;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.security.GeneralSecurityException;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class GoogleDriveFactory {

    private Credential credential;
    private final AssetsConfigProperties config;

    public GoogleDriveFactory(AssetsConfigProperties config) {
        this.config = config;
    }

    @PostConstruct
    private void init() {
        try {
            var tokenResponse = new TokenResponse();
            tokenResponse.setRefreshToken(config.refreshToken());

            this.credential = new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
                    .setTransport(newTrustedTransport())
                    .setJsonFactory(getDefaultInstance())
                    .setTokenServerUrl(new GenericUrl(config.tokenServer()))
                    .setClientAuthentication(new BasicAuthentication(config.clientId(), config.clientSecret()))
                    .build()
                    .setFromTokenResponse(tokenResponse);
        } catch (GeneralSecurityException e) {
            throw new AssetsClientException(AssetsErrorCode.UNAUTHORIZED_ACCESS_ERROR, e);
        } catch (IOException e) {
            throw new AssetsBusinessException(AssetsErrorCode.ASSET_INPUT_OUTPUT_ERROR, e);
        }
    }

    public HttpRequestFactory createHttpRequestFactory() {
        return credential.getTransport().createRequestFactory();
    }

    public Drive createDrive() {
        try {
            return new Drive.Builder(newTrustedTransport(), getDefaultInstance(), credential)
                    .setApplicationName("Renaissance Rentals assets")
                    .build();
        } catch (GeneralSecurityException e) {
            throw new AssetsClientException(AssetsErrorCode.UNAUTHORIZED_ACCESS_ERROR, e);
        } catch (IOException e) {
            throw new AssetsBusinessException(AssetsErrorCode.ASSET_INPUT_OUTPUT_ERROR, e);
        }
    }
}
