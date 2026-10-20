package com.renaissancerentals.foundation.web;

import com.contentmunch.foundation.logging.RequestResponseLoggingFilter;
import jakarta.servlet.http.HttpServletRequest;

/**
 * The request/response logging filter copies every {@code /api} response into memory before sending it, which is
 * wasted work for photos (the log line only says the body was skipped) and, with several photos at once, enough to
 * run a small dyno out of heap. This one leaves the endpoints that send files alone.
 */
public class BinaryResponseLoggingFilter extends RequestResponseLoggingFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return super.shouldNotFilter(request) || sendsFile(request.getRequestURI());
    }

    static boolean sendsFile(String uri) {
        return (uri.startsWith("/api/assets/") && uri.endsWith("/download")) || uri.startsWith("/api/blog/media/");
    }
}
