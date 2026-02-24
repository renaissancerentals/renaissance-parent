package com.renaissancerentals.foundation.text.external;

import java.io.IOException;

import com.renaissancerentals.foundation.text.config.TextConfigProperties;
import com.renaissancerentals.foundation.text.data.TextMessage;
import com.renaissancerentals.foundation.text.error.TextException;
import com.renaissancerentals.foundation.text.error.TextExtensionNotFoundException;
import com.renaissancerentals.foundation.text.service.TextService;
import com.ringcentral.RestClient;
import com.ringcentral.RestException;
import com.ringcentral.definitions.CreateSMSMessage;
import com.ringcentral.definitions.MessageStoreCallerInfoRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class RingCentralTextService implements TextService {
    private final TextConfigProperties textConfigProperties;

    @Override
    public void sendText(TextMessage textMessage){
        try {
            if (!textConfigProperties.extensions().containsKey(textMessage.from()))
                throw new TextExtensionNotFoundException(textMessage.from());

            var textExtension = textConfigProperties.extensions().get(textMessage.from());

            RestClient restClient = new RestClient(textExtension.clientId(), textExtension.clientSecret(),
                    textConfigProperties.serverUrl());

            restClient.authorize(textExtension.jwtToken());

            CreateSMSMessage requestBody = new CreateSMSMessage();
            requestBody.from = new MessageStoreCallerInfoRequest().phoneNumber(textExtension.phoneNumber());

            requestBody.to = new MessageStoreCallerInfoRequest[]{
                    new MessageStoreCallerInfoRequest().phoneNumber(textMessage.to())};

            requestBody.text = textMessage.message();
            var resp = restClient.restapi().account().extension().sms().post(requestBody);

            log.info("SMS Response for {}. Message id: {}",textExtension.name(),resp.id.toString());
        } catch (IOException | RestException e) {
            throw new TextException(e);
        }

    }
}
