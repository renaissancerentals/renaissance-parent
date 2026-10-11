package com.renaissancerentals.api.blog.web;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.renaissancerentals.api.blog.BlogProperties;
import com.renaissancerentals.api.blog.BlogStore;
import com.renaissancerentals.api.blog.sync.BlogSyncService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class BlogSyncWebhookControllerTest {

    private final BlogSyncService service = mock(BlogSyncService.class);
    private final BlogStore store = mock(BlogStore.class);

    private MockMvc with(String secret) {
        var properties = new BlogProperties(null, new BlogProperties.Sync(true, null, null, null, secret, null));
        return MockMvcBuilders.standaloneSetup(
                        new BlogSyncWebhookController(service, store, properties, Executors.newSingleThreadExecutor()))
                .build();
    }

    @Test
    void theRightSecretStartsASyncAndAnswersAtOnce() throws Exception {
        with("s3cret")
                .perform(post("/api/blog/sync/webhook").header("X-Sync-Secret", "s3cret"))
                .andExpect(status().isAccepted());

        verify(service, timeout(2000)).sync();
    }

    @Test
    void aWrongOrMissingSecretIsRefusedAndStartsNothing() throws Exception {
        var mvc = with("s3cret");

        mvc.perform(post("/api/blog/sync/webhook").header("X-Sync-Secret", "nope"))
                .andExpect(status().isUnauthorized());
        mvc.perform(post("/api/blog/sync/webhook")).andExpect(status().isUnauthorized());

        verify(service, never()).sync();
    }

    @Test
    void withNoSecretConfiguredEveryCallIsRefused() throws Exception {
        with("").perform(post("/api/blog/sync/webhook").header("X-Sync-Secret", ""))
                .andExpect(status().isUnauthorized());

        verify(service, never()).sync();
    }

    @Test
    void theStatusNeedsTheSecretToo() throws Exception {
        with("s3cret").perform(get("/api/blog/sync/status")).andExpect(status().isUnauthorized());
    }
}
