package com.renaissancerentals.api;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.renaissancerentals.foundation.text.data.TextMessage;
import com.renaissancerentals.foundation.text.service.TextService;

@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
@Disabled("Only enable to test the workings of text messages")
class SMSTest {

    @Autowired
    private TextService textService;

    @Test
    void sendVeronaParkSMS(){
        textService.sendText(TextMessage.builder().from("8123332280").to("+18124161456")
                .message("Hello World from Verona Park").build());

    }

    @Test
    void sendSummerHouseSMS(){
        textService.sendText(TextMessage.builder().from("8123322141").to("+18124161456")
                .message("Hello World from Summer House").build());

    }

    @Test
    void sendScholarsRockSMS(){
        textService.sendText(TextMessage.builder().from("8123301123").to("+18124161456")
                .message("Hello World from Scholars Rock").build());

    }

    @Test
    void sendCovenanterHillSMS(){
        textService.sendText(TextMessage.builder().from("8123238021").to("8124161456")
                .message("Hello World from Covenanter Hill").build());

    }

}
