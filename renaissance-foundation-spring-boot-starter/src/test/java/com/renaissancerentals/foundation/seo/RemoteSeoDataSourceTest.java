package com.renaissancerentals.foundation.seo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Clock;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class RemoteSeoDataSourceTest {

    private MockRestServiceServer server;
    private RemoteSeoDataSource source;

    @BeforeEach
    void setUp() {
        var builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        source = new RemoteSeoDataSource(
                builder.build(),
                List.of("https://a.example", "https://b.example"),
                new ObjectMapper().registerModule(new JavaTimeModule()),
                Clock.systemUTC());
    }

    @Test
    void readsWhereARenamedFloorplanMovedTo() {
        server.expect(requestTo("https://a.example/api/floorplanRedirects/view-big"))
                .andRespond(withSuccess("{\"from\":\"view-big\",\"to\":\"view\"}", MediaType.APPLICATION_JSON));

        assertThat(source.floorplanRedirect("view-big")).contains("view");
        server.verify();
    }

    @Test
    void anAddressNobodyRenamedHasNoRedirect() {
        server.expect(requestTo("https://a.example/api/floorplanRedirects/nope"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThat(source.floorplanRedirect("nope")).isEmpty();
    }

    @Test
    void asksTheNextHostWhenTheFirstIsDown() {
        server.expect(requestTo("https://a.example/api/floorplanRedirects/view-big"))
                .andRespond(withServerError());
        server.expect(requestTo("https://b.example/api/floorplanRedirects/view-big"))
                .andRespond(withSuccess("{\"from\":\"view-big\",\"to\":\"view\"}", MediaType.APPLICATION_JSON));

        assertThat(source.floorplanRedirect("view-big")).contains("view");
    }

    @Test
    void reportsThatTheAnswerIsUnknownWhenNoHostAnswers() {
        server.expect(requestTo("https://a.example/api/floorplanRedirects/x")).andRespond(withServerError());
        server.expect(requestTo("https://b.example/api/floorplanRedirects/x")).andRespond(withServerError());

        assertThatThrownBy(() -> source.floorplanRedirect("x")).isInstanceOf(SeoDataUnavailableException.class);
    }

    @Test
    void anEmptyTargetIsNotARedirect() {
        server.expect(requestTo("https://a.example/api/floorplanRedirects/x"))
                .andRespond(withSuccess("{\"from\":\"x\",\"to\":\" \"}", MediaType.APPLICATION_JSON));

        assertThat(source.floorplanRedirect("x")).isEmpty();
    }
}
