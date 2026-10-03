package com.renaissancerentals.api.cache;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.MethodParameter;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * Lets browsers (and any proxy) keep successful responses of the read-only API for a short time, so a visitor
 * moving between pages does not ask the servers for the same listings again. Only 200 responses to GET requests
 * for the configured path prefixes are touched, and an endpoint that sets its own Cache-Control is left alone.
 */
@RestControllerAdvice
@ConditionalOnProperty(prefix = "renaissancerentals.api.cache", name = "enabled", matchIfMissing = true)
public final class ApiCacheAdvice implements ResponseBodyAdvice<Object> {

    private final ApiCacheProperties properties;

    /** The properties bean is optional so that slice tests (which do not load the auto-configuration) still work. */
    public ApiCacheAdvice(ObjectProvider<ApiCacheProperties> properties) {
        this.properties = properties.getIfAvailable(() -> new ApiCacheProperties(null, null, null, null));
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return properties.enabled();
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response) {
        if (request instanceof ServletServerHttpRequest servletRequest
                && response instanceof ServletServerHttpResponse servletResponse
                && cacheable(servletRequest.getServletRequest(), servletResponse.getServletResponse())
                && !response.getHeaders().containsKey(HttpHeaders.CACHE_CONTROL)) {
            response.getHeaders()
                    .setCacheControl(CacheControl.maxAge(properties.maxAge())
                            .cachePublic()
                            .staleWhileRevalidate(properties.staleWhileRevalidate()));
        }
        return body;
    }

    private boolean cacheable(HttpServletRequest request, HttpServletResponse response) {
        if (!"GET".equals(request.getMethod()) || response.getStatus() != 200) {
            return false;
        }
        var path = request.getRequestURI().substring(request.getContextPath().length());
        return properties.paths().stream().anyMatch(prefix -> path.equals(prefix) || path.startsWith(prefix + "/"));
    }
}
