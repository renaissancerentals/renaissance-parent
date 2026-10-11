package com.renaissancerentals.api.blog.source;

import com.renaissancerentals.api.blog.BlogProperties;
import java.net.http.HttpClient;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/** The notes platform's feed: this site's API account reads changes, acknowledges them, and copies images. */
@Component
@ConditionalOnExpression("'${renaissancerentals.blog.source.base-url:}' != ''")
public class NotesContentSource implements ContentSource {

    private final RestClient client;

    public NotesContentSource(BlogProperties properties) {
        var source = properties.source();
        var factory = new JdkClientHttpRequestFactory(
                HttpClient.newBuilder().connectTimeout(source.timeout()).build());
        factory.setReadTimeout(source.timeout());
        this.client = RestClient.builder()
                .baseUrl(source.baseUrl().replaceAll("/+$", ""))
                .defaultHeader("Authorization", "Bearer " + source.token())
                .requestFactory(factory)
                .build();
    }

    @Override
    public SourcePage changes(String cursor, int limit) {
        return client.get()
                .uri(uri -> {
                    var builder = uri.path("/api/feed/changes").queryParam("limit", limit);
                    if (cursor != null && !cursor.isBlank()) builder.queryParam("cursor", cursor);
                    return builder.build();
                })
                .retrieve()
                .body(SourcePage.class);
    }

    @Override
    public List<SourceIndexEntry> index() {
        return client.get()
                .uri("/api/feed/index")
                .retrieve()
                .body(new ParameterizedTypeReference<List<SourceIndexEntry>>() {});
    }

    @Override
    public void acknowledge(String cursor) {
        client.post()
                .uri("/api/feed/ack")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("cursor", cursor))
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public Optional<SourceMedia> media(String ref, long maxBytes) {
        // a ref is "bucket/id": two plain segments, nothing that could steer the request elsewhere
        if (!ref.matches("[A-Za-z0-9]{1,8}/[A-Za-z0-9]{1,16}")) return Optional.empty();
        return client.get().uri("/api/media/" + ref + "/content").exchange((request, response) -> {
            HttpStatusCode status = response.getStatusCode();
            if (status.value() == 404) return Optional.<SourceMedia>empty();
            if (!status.is2xxSuccessful()) {
                throw new IllegalStateException("Media " + ref + " answered " + status.value());
            }
            long declared = response.getHeaders().getContentLength();
            if (declared > maxBytes) return Optional.<SourceMedia>empty();
            try (var in = response.getBody()) {
                byte[] bytes = in.readNBytes((int) maxBytes + 1);
                if (bytes.length > maxBytes) return Optional.<SourceMedia>empty();
                var type = response.getHeaders().getContentType();
                return Optional.of(new SourceMedia(type == null ? "application/octet-stream" : type.toString(), bytes));
            }
        });
    }
}
