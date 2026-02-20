package com.renaissancerentals.api;

import com.renaissancerentals.api.config.TextConfigProperties;
import com.ringcentral.RestClient;
import com.ringcentral.RestException;
import com.ringcentral.definitions.CreateSMSMessage;
import com.ringcentral.definitions.MessageStoreCallerInfoRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;

@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
class SMSTest {

    @Autowired
    private TextConfigProperties textConfigProperties;

    @Test
    void sendSMS() throws RestException, IOException {
        RestClient restClient = new RestClient(textConfigProperties.clientId(), textConfigProperties.clientSecret(), textConfigProperties.serverUrl());
        restClient.authorize(textConfigProperties.jwtToken());

        CreateSMSMessage requestBody = new CreateSMSMessage();
        requestBody.from = new MessageStoreCallerInfoRequest().phoneNumber("+18123332280");

        requestBody.to = new MessageStoreCallerInfoRequest[]{
                new MessageStoreCallerInfoRequest().phoneNumber("+18124161456")
        };

        // To send group messaging, add more (max 10 recipients) 'phoneNumber' object. E.g.
        /*
        requestBody.to = new MessageStoreCallerInfoRequest[] {
          new MessageStoreCallerInfoRequest().phoneNumber("Recipient_1_Number"),
          new MessageStoreCallerInfoRequest().phoneNumber("Recipient_2_Number")
        };
        */
        requestBody.text = "Hello World";
//302536019/phone-number/254901018
        var resp = restClient.restapi().account().extension("209189018").sms().post(requestBody);
        System.out.println("SMS sent. Message id: " + resp.id.toString());
//        check_sms_message_status(resp.id.toString());

//        var resp =  restClient.restapi().account().extension().phoneNumber().get();
//
//        if (resp.records.length == 0) {
//            System.out.println("This user does not own a phone number!");
//        } else {
//            System.out.println("None of this user's phone number(s) has the SMS capability!");
//        }

    }

    @Test
    void findSMSNumber() throws RestException, IOException {
        RestClient restClient = new RestClient(textConfigProperties.clientId(), textConfigProperties.clientSecret(), textConfigProperties.serverUrl());
        restClient.authorize(textConfigProperties.jwtToken());


        var resp = restClient.restapi().account("302536019").extension("254068018").phoneNumber().get();

        if (resp.records.length == 0) {
            System.out.println("This user does not own a phone number!");
        } else {
            System.out.println("None of this user's phone number(s) has the SMS capability!");
        }

    }

    @Test
    void findAccount() throws RestException, IOException {
        RestClient restClient = new RestClient(textConfigProperties.clientId(), textConfigProperties.clientSecret(), textConfigProperties.serverUrl());
        restClient.authorize(textConfigProperties.jwtToken());


        var resp = restClient.restapi().account().extension().get();

        System.out.println("SMS account: " + resp.id.toString());

    }

}
