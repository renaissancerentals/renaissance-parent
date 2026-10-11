package com.renaissancerentals.api.blog.sync;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/** Runs the sync on a timer and the nightly check. Only on the instance that has {@code blog.sync.enabled} set. */
@Slf4j
@Configuration
@EnableScheduling
@ConditionalOnExpression("'${renaissancerentals.blog.source.base-url:}' != ''")
@ConditionalOnProperty(prefix = "renaissancerentals.blog.sync", name = "enabled", havingValue = "true")
public class BlogSyncScheduler {

    private final BlogSyncService service;

    public BlogSyncScheduler(BlogSyncService service) {
        this.service = service;
    }

    @Scheduled(
            initialDelayString = "${renaissancerentals.blog.sync.initial-delay:PT30S}",
            fixedDelayString = "${renaissancerentals.blog.sync.interval:PT10M}")
    public void sync() {
        try {
            service.sync();
        } catch (RuntimeException e) {
            // already recorded and logged; the next run tries again with backoff of the interval
        }
    }

    @Scheduled(cron = "${renaissancerentals.blog.sync.reconcile-cron:0 30 3 * * *}")
    public void reconcile() {
        try {
            service.reconcile();
        } catch (RuntimeException e) {
            // already recorded and logged
        }
    }
}
