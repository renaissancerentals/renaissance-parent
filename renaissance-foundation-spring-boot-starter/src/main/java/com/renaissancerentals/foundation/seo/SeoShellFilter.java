package com.renaissancerentals.foundation.seo;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Set;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

/**
 * Serves the single page app shell with real metadata. Works for every site without touching its controllers:
 * whatever the controller answers for a page route (a forward to index.html or the file itself) is captured, the
 * head is rewritten for the requested path, and the result is sent.
 */
public class SeoShellFilter extends OncePerRequestFilter {

    private static final Set<String> CONDITIONAL = Set.of("if-modified-since", "if-none-match", "range", "if-range");
    private static final Set<String> STALE_VALIDATORS = Set.of("last-modified", "etag");

    private final SeoService seo;
    private final HeadRenderer renderer;

    public SeoShellFilter(SeoService seo, HeadRenderer renderer) {
        this.seo = seo;
        this.renderer = renderer;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        var path = request.getRequestURI().substring(request.getContextPath().length());

        if (path.startsWith("/api/") && !path.startsWith("/api/assets/")) {
            // Data JSON must never compete with the pages in search results. Not blocked in robots.txt on purpose.
            response.setHeader("X-Robots-Tag", "noindex, nofollow");
        }
        if (!isPage(request, path)) {
            chain.doFilter(request, response);
            return;
        }

        var wrapped = new ShellResponse(response);
        chain.doFilter(new NoConditionalRequest(request), wrapped);

        var body = wrapped.getContentAsByteArray();
        var type = wrapped.getContentType();
        var status = wrapped.getStatus();
        if (body.length == 0 || type == null || !type.startsWith("text/html") || status >= 500 || status == 304) {
            wrapped.copyBodyToResponse();
            return;
        }
        var html = new String(body, StandardCharsets.UTF_8);
        if (!html.contains("id=\"root\"")) {
            wrapped.copyBodyToResponse();
            return;
        }

        var meta = seo.resolve(path);
        var rendered = renderer.render(html, meta).getBytes(StandardCharsets.UTF_8);
        if (status < 400 && meta.status() >= 400) {
            response.setStatus(meta.status());
        }
        if (!meta.indexable()) {
            response.setHeader("X-Robots-Tag", "noindex, follow");
        }
        response.setContentLength(rendered.length);
        response.getOutputStream().write(rendered);
    }

    private static boolean isPage(HttpServletRequest request, String path) {
        var method = request.getMethod();
        if (!"GET".equals(method) && !"HEAD".equals(method)) {
            return false;
        }
        if (path.startsWith("/api/") || path.startsWith("/actuator")) {
            return false;
        }
        var lastSegment = path.substring(path.lastIndexOf('/') + 1);
        return !lastSegment.contains(".");
    }

    /** Buffers the body and drops validators, because the body is rewritten after the resource handler set them. */
    private static final class ShellResponse extends ContentCachingResponseWrapper {

        ShellResponse(HttpServletResponse response) {
            super(response);
        }

        @Override
        public void setHeader(String name, String value) {
            if (!STALE_VALIDATORS.contains(name.toLowerCase(java.util.Locale.ROOT))) {
                super.setHeader(name, value);
            }
        }

        @Override
        public void addHeader(String name, String value) {
            if (!STALE_VALIDATORS.contains(name.toLowerCase(java.util.Locale.ROOT))) {
                super.addHeader(name, value);
            }
        }

        @Override
        public void setDateHeader(String name, long date) {
            if (!STALE_VALIDATORS.contains(name.toLowerCase(java.util.Locale.ROOT))) {
                super.setDateHeader(name, date);
            }
        }

        @Override
        public void addDateHeader(String name, long date) {
            if (!STALE_VALIDATORS.contains(name.toLowerCase(java.util.Locale.ROOT))) {
                super.addDateHeader(name, date);
            }
        }
    }

    private static final class NoConditionalRequest extends HttpServletRequestWrapper {

        NoConditionalRequest(HttpServletRequest request) {
            super(request);
        }

        @Override
        public String getHeader(String name) {
            return CONDITIONAL.contains(name.toLowerCase(java.util.Locale.ROOT)) ? null : super.getHeader(name);
        }

        @Override
        public long getDateHeader(String name) {
            return CONDITIONAL.contains(name.toLowerCase(java.util.Locale.ROOT)) ? -1 : super.getDateHeader(name);
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            return CONDITIONAL.contains(name.toLowerCase(java.util.Locale.ROOT))
                    ? Collections.emptyEnumeration()
                    : super.getHeaders(name);
        }
    }
}
