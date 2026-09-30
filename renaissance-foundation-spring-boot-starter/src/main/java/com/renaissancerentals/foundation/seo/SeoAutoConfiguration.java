package com.renaissancerentals.foundation.seo;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Clock;
import java.time.Duration;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * Head metadata, canonical URLs, structured data, sitemap.xml, robots.txt and llms.txt. Switched on per site by
 * setting {@code renaissancerentals.seo.canonical-origin}. Sites that have a database provide their own
 * {@link SeoDataSource}; the others read the public API of a data host.
 */
@AutoConfiguration(afterName = "com.renaissancerentals.api.seo.ApiSeoAutoConfiguration")
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(prefix = "renaissancerentals.seo", name = "canonical-origin")
@EnableConfigurationProperties(SeoProperties.class)
public class SeoAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SiteLinks seoSiteLinks(SeoProperties properties) {
        return new SiteLinks(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public SeoJsonLd seoJsonLd(SeoProperties properties, SiteLinks links) {
        return new SeoJsonLd(properties, links);
    }

    @Bean
    @ConditionalOnMissingBean(SeoDataSource.class)
    public SeoDataSource seoRemoteDataSource(SeoProperties properties, ObjectMapper mapper) {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) Duration.ofSeconds(2).toMillis());
        factory.setReadTimeout((int) Duration.ofSeconds(5).toMillis());
        var client = RestClient.builder().requestFactory(factory).build();
        return new RemoteSeoDataSource(client, properties.dataBaseUrls(), mapper, Clock.systemUTC());
    }

    @Bean
    @ConditionalOnMissingBean
    public SnapshotBuilder seoSnapshotBuilder(SiteLinks links, SeoDataSource data) {
        return new SnapshotBuilder(links, data);
    }

    @Bean
    @Order(0)
    public StaticPageContributor seoStaticPages(SeoProperties properties, SiteLinks links, SnapshotBuilder snapshots) {
        return new StaticPageContributor(properties, links, snapshots);
    }

    @Bean
    @Order(10)
    public EntityPageContributor seoEntityPages(
            SeoProperties properties, SiteLinks links, SeoJsonLd jsonLd, SeoDataSource data) {
        return new EntityPageContributor(properties, links, jsonLd, data);
    }

    @Bean
    @Order(20)
    public HomePageContributor seoHomePage(
            SeoProperties properties, SiteLinks links, SeoJsonLd jsonLd, SnapshotBuilder snapshots) {
        return new HomePageContributor(properties, links, jsonLd, snapshots);
    }

    @Bean
    @ConditionalOnMissingBean
    public SeoService seoService(
            SeoProperties properties,
            SiteLinks links,
            ObjectProvider<SeoPageContributor> pageContributors,
            ObjectProvider<SitemapContributor> sitemapContributors) {
        return new SeoService(
                properties,
                links,
                pageContributors.orderedStream().toList(),
                sitemapContributors.orderedStream().toList(),
                Clock.systemUTC());
    }

    @Bean
    @ConditionalOnMissingBean
    public HeadRenderer seoHeadRenderer(ObjectMapper mapper, SeoProperties properties) {
        return new HeadRenderer(mapper, properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public SeoController seoController(SeoService seo) {
        return new SeoController(seo);
    }

    @Bean
    public FilterRegistrationBean<SeoShellFilter> seoShellFilter(SeoService seo, HeadRenderer renderer) {
        var registration = new FilterRegistrationBean<>(new SeoShellFilter(seo, renderer));
        registration.setOrder(Ordered.LOWEST_PRECEDENCE - 100);
        registration.addUrlPatterns("/*");
        return registration;
    }
}
