package com.renaissancerentals.foundation.seo;

import java.time.Duration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * sitemap.xml, robots.txt and llms.txt. Take precedence over static files of the same name.
 *
 * <p>Sites component-scan {@code com.renaissancerentals}, so this class can be picked up by the scan as well as by
 * the auto-configuration. It is therefore conditional on the same property that switches SEO on, and the
 * auto-configuration only registers it when the scan did not.
 */
@Controller
@ResponseBody
@ConditionalOnProperty(prefix = "renaissancerentals.seo", name = "canonical-origin")
public class SeoController {

    private static final MediaType TEXT = new MediaType("text", "plain", java.nio.charset.StandardCharsets.UTF_8);
    private static final MediaType XML = new MediaType("application", "xml", java.nio.charset.StandardCharsets.UTF_8);

    private final SeoService seo;

    public SeoController(SeoService seo) {
        this.seo = seo;
    }

    private static ResponseEntity<String> ok(MediaType type, String body) {
        return ResponseEntity.ok()
                .contentType(type)
                .cacheControl(CacheControl.maxAge(Duration.ofMinutes(10)).cachePublic())
                .body(body);
    }

    @GetMapping("/sitemap.xml")
    public ResponseEntity<String> sitemap() {
        return ok(XML, seo.sitemapXml());
    }

    @GetMapping("/robots.txt")
    public ResponseEntity<String> robots() {
        return ok(TEXT, seo.robotsTxt());
    }

    @GetMapping("/llms.txt")
    public ResponseEntity<String> llms() {
        return ok(TEXT, seo.llmsTxt());
    }
}
