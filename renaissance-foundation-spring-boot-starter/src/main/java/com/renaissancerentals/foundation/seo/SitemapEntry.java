package com.renaissancerentals.foundation.seo;

import java.time.LocalDate;

/** One URL of sitemap.xml. {@code loc} is absolute. {@code title} is used for llms.txt. */
public record SitemapEntry(String loc, LocalDate lastmod, String changefreq, Double priority, String title) {}
