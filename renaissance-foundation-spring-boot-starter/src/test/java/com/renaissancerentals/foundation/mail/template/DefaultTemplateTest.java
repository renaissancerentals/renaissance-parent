package com.renaissancerentals.foundation.mail.template;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.renaissancerentals.foundation.mail.error.MailErrorCode;
import com.renaissancerentals.foundation.mail.error.MailServerException;
import com.renaissancerentals.foundation.template.mail.DefaultMailTemplate;
import com.renaissancerentals.foundation.template.mail.model.DefaultMessage;
import freemarker.template.Configuration;
import freemarker.template.Template;
import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

@ExtendWith(MockitoExtension.class)
class DefaultTemplateTest {

    @Mock
    Configuration freemarkerConfiguration;

    @Mock
    Template template;

    DefaultMailTemplate mailTemplate;

    @BeforeEach
    void setUp() {
        mailTemplate = new DefaultMailTemplate(freemarkerConfiguration);
    }

    @Test
    void shouldRenderTemplateSuccessfully() throws Exception {
        DefaultMessage data = new DefaultMessage("hello", "world"); // example values
        when(freemarkerConfiguration.getTemplate("default-mail.ftl")).thenReturn(template);

        try (MockedStatic<FreeMarkerTemplateUtils> utils = mockStatic(FreeMarkerTemplateUtils.class)) {
            utils.when(() -> FreeMarkerTemplateUtils.processTemplateIntoString(template, Map.of("data", data)))
                    .thenReturn("Rendered Output");

            String result = mailTemplate.render(data);

            assertEquals("Rendered Output", result);
        }
    }

    @Test
    void shouldThrowMailServerExceptionOnTemplateFailure() throws Exception {
        DefaultMessage data = new DefaultMessage("bad", "data");
        when(freemarkerConfiguration.getTemplate("default-mail.ftl")).thenThrow(new IOException("template not found"));

        MailServerException ex = assertThrows(MailServerException.class, () -> mailTemplate.render(data));

        assertEquals(MailErrorCode.MAIL_TEMPLATE_ERROR, ex.getErrorCode());
        assertInstanceOf(IOException.class, ex.getCause());
    }
}
