package com.renaissancerentals.api.blog.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.renaissancerentals.api.blog.BlogPost;
import com.renaissancerentals.api.blog.BlogStore;
import com.renaissancerentals.api.blog.sync.BlogContentProcessor;
import com.renaissancerentals.foundation.error.GlobalExceptionHandler;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class BlogControllerTest {

    private static final OffsetDateTime AT = OffsetDateTime.parse("2026-10-09T10:00:00Z");

    private BlogStore store;
    private BlogContentProcessor processor;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        store = mock(BlogStore.class);
        processor = new BlogContentProcessor();
        mvc = MockMvcBuilders.standaloneSetup(new BlogController(store, processor))
                .setControllerAdvice(new GlobalExceptionHandler(null, null))
                .build();
    }

    private static BlogPost post(String id, String body) {
        return new BlogPost(id, "ab", 3, "Title " + id, "Excerpt", "ab/img1", "Guides", body, AT, AT);
    }

    @Test
    void theListIsInTheShapeThePagesAlreadyRead() throws Exception {
        when(store.list(any(), any(), eq(13), eq(0))).thenReturn(List.of(post("abcd", "<p>x</p>")));

        mvc.perform(get("/api/blog/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.blogs[0].id").value("abcd"))
                .andExpect(jsonPath("$.blogs[0].img").value("/api/blog/media/ab/img1"))
                .andExpect(jsonPath("$.blogs[0].url").value("/blogs/abcd"))
                .andExpect(jsonPath("$.blogs[0].content").doesNotExist())
                .andExpect(jsonPath("$.nextPageToken").value(""));
    }

    @Test
    void aFullPageGivesATokenForTheNext() throws Exception {
        var many = new java.util.ArrayList<BlogPost>();
        for (int i = 0; i < 3; i++) many.add(post("p" + i, "<p>x</p>"));
        when(store.list(any(), any(), eq(3), eq(0))).thenReturn(many);

        mvc.perform(get("/api/blog/posts").param("size", "2"))
                .andExpect(jsonPath("$.blogs.length()").value(2))
                .andExpect(jsonPath("$.nextPageToken").value("2"));
    }

    @Test
    void theSizeIsHeldAndABadTokenStartsAtTheBeginning() throws Exception {
        when(store.list(any(), any(), anyInt(), anyInt())).thenReturn(List.of());

        mvc.perform(get("/api/blog/posts").param("size", "9999").param("pageToken", "junk"))
                .andExpect(status().isOk());

        org.mockito.Mockito.verify(store).list(any(), any(), eq(51), eq(0));
    }

    @Test
    void neverAsksABrowserToKeepAListWithoutChecking() throws Exception {
        when(store.list(any(), any(), anyInt(), anyInt())).thenReturn(List.of());

        mvc.perform(get("/api/blog/posts")).andExpect(header().string("Cache-Control", "no-cache, public"));
    }

    @Test
    void aPostLinksOnlyToPostsThatAreHere() throws Exception {
        var body = processor.process(
                "<a data-title-link-src=\"true\" href=\"/ab/abcd\">Here</a> <a data-title-link-src=\"true\" href=\"/ab/wxyz\">gone</a>");
        when(store.find("main")).thenReturn(Optional.of(post("main", body)));
        when(store.liveAmong(any())).thenReturn(Set.of("abcd"));

        mvc.perform(get("/api/blog/posts/main"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(org.hamcrest.Matchers.containsString("href=\"/blogs/abcd\"")))
                .andExpect(jsonPath("$.content")
                        .value(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("wxyz"))));
    }

    @Test
    void aPostThatIsNotHereIsNotFound() throws Exception {
        when(store.find("nope")).thenReturn(Optional.empty());

        mvc.perform(get("/api/blog/posts/nope")).andExpect(status().isNotFound());
    }

    @Test
    void mediaIsServedWithItsTypeAndMayBeKept() throws Exception {
        when(store.media("ab/img1"))
                .thenReturn(Optional.of(new BlogStore.StoredMedia("image/png", new byte[] {7, 8}, AT)));

        mvc.perform(get("/api/blog/media/ab/img1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/png"))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(content().bytes(new byte[] {7, 8}));
    }

    @Test
    void mediaThatIsNotHereIsNotFound() throws Exception {
        when(store.media("ab/none")).thenReturn(Optional.empty());

        mvc.perform(get("/api/blog/media/ab/none")).andExpect(status().isNotFound());
    }
}
