package com.renaissancerentals.api.blog.sync;

import com.renaissancerentals.api.blog.BlogPost;
import com.renaissancerentals.api.blog.BlogProperties;
import com.renaissancerentals.api.blog.BlogStore;
import com.renaissancerentals.api.blog.source.ContentSource;
import com.renaissancerentals.api.blog.source.SourceItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Keeps the blog's database copy current with the notes platform. Each page of changes is applied and its cursor stored in
 * one transaction, and only then acknowledged, so a crashed run simply repeats. A post older than the one held is ignored
 * (see {@link BlogStore#upsert}); a post that keeps failing is set aside after a few tries so it cannot stop the rest.
 * Nothing on a page's request path calls the notes server.
 */
@Slf4j
@Service
@ConditionalOnExpression("'${renaissancerentals.blog.source.base-url:}' != ''")
public class BlogSyncService {

    private final ContentSource source;
    private final BlogStore store;
    private final BlogContentProcessor processor;
    private final BlogProperties properties;
    private final TransactionTemplate transaction;
    private final AtomicBoolean running = new AtomicBoolean(false);
    /** Something asked for a run while one was going: it may have missed that change, so go round once more. */
    private final AtomicBoolean again = new AtomicBoolean(false);

    public BlogSyncService(
            ContentSource source,
            BlogStore store,
            BlogContentProcessor processor,
            BlogProperties properties,
            TransactionTemplate transaction) {
        this.source = source;
        this.store = store;
        this.processor = processor;
        this.properties = properties;
        this.transaction = transaction;
    }

    /** What a run did. */
    public record Result(boolean ran, int applied, int removed, int setAside) {
        static final Result SKIPPED = new Result(false, 0, 0, 0);
    }

    /** Catches up from the stored cursor. A run already going makes this one a no-op. */
    public Result sync() {
        return run(store.cursor().orElse(null));
    }

    /**
     * The nightly check: anything no longer public is taken down, and if anything public is missing or older here a pass
     * from the beginning fetches it (versions make that safe to repeat).
     */
    public Result reconcile() {
        if (!running.compareAndSet(false, true)) return Result.SKIPPED;
        try {
            var wanted = new HashMap<String, Integer>();
            source.index().forEach(e -> wanted.put(e.id(), e.version()));
            var held = store.liveIndex();

            int removed = 0;
            boolean missing = false;
            for (var entry : held) {
                var id = entry.id();
                if (!wanted.containsKey(id)) {
                    transaction.executeWithoutResult(s -> store.takeDown(nid(id)));
                    removed++;
                }
            }
            var heldVersions = new HashMap<String, Integer>();
            held.forEach(e -> heldVersions.put(e.id(), e.version()));
            for (var entry : wanted.entrySet()) {
                Integer have = heldVersions.get(entry.getKey());
                if (have == null || have < entry.getValue()) {
                    missing = true;
                    break;
                }
            }
            if (removed > 0) log.warn("Reconcile took down {} post(s) that are no longer public", removed);
            if (!missing) {
                store.recordSuccess();
                return new Result(true, 0, removed, 0);
            }
            log.warn("Reconcile found posts missing or out of date; fetching from the beginning");
            var result = runLocked(null);
            return new Result(true, result.applied(), removed + result.removed(), result.setAside());
        } catch (RuntimeException e) {
            store.recordError(describe(e));
            throw e;
        } finally {
            running.set(false);
        }
    }

    private Result run(String startCursor) {
        if (!running.compareAndSet(false, true)) {
            again.set(true);
            return Result.SKIPPED;
        }
        try {
            var result = runLocked(startCursor);
            while (again.getAndSet(false)) {
                var next = runLocked(store.cursor().orElse(null));
                result = new Result(
                        true,
                        result.applied() + next.applied(),
                        result.removed() + next.removed(),
                        result.setAside() + next.setAside());
            }
            return result;
        } catch (RuntimeException e) {
            store.recordError(describe(e));
            log.error("Blog sync failed; the copy keeps serving what it has", e);
            throw e;
        } finally {
            running.set(false);
        }
    }

    private Result runLocked(String startCursor) {
        String cursor = startCursor;
        int applied = 0;
        int removed = 0;
        int setAside = 0;
        boolean more = true;
        while (more) {
            var page = source.changes(cursor, properties.sync().batchSize());
            var prepared = new ArrayList<Prepared>();
            for (var item : page.items()) {
                var ready = prepare(item);
                if (ready == null) setAside++;
                else prepared.add(ready);
            }

            int[] counts = new int[2];
            transaction.executeWithoutResult(status -> {
                for (var ready : prepared) {
                    if (ready.post() == null) {
                        if (store.remove(ready.id(), ready.version())) counts[1]++;
                    } else {
                        for (var media : ready.media().entrySet()) store.putMedia(media.getKey(), media.getValue());
                        if (store.upsert(ready.post())) counts[0]++;
                    }
                    store.clearFailure(ready.id());
                }
                store.saveCursor(page.nextCursor());
            });
            applied += counts[0];
            removed += counts[1];
            cursor = page.nextCursor();
            more = page.hasMore();

            acknowledge(cursor);
        }
        store.recordSuccess();
        return new Result(true, applied, removed, setAside);
    }

    /** Everything that can fail for one item is done before the transaction, so a failure here costs only this item. */
    private Prepared prepare(SourceItem item) {
        String id = nid(item.id());
        try {
            if (item.removed()) return new Prepared(id, item.version(), null, Map.of());

            var media = new HashMap<String, com.renaissancerentals.api.blog.source.SourceMedia>();
            for (var ref : item.media()) {
                source.media(ref, properties.sync().maxMediaBytes()).ifPresent(m -> media.put(ref, m));
            }
            var post = new BlogPost(
                    id,
                    item.id().substring(0, item.id().indexOf('/')),
                    item.version(),
                    item.title() == null || item.title().isBlank() ? "Untitled" : item.title(),
                    item.excerpt(),
                    item.cover(),
                    item.category(),
                    processor.process(item.contentHtml()),
                    item.publishedAt(),
                    item.updatedAt());
            return new Prepared(id, item.version(), post, media);
        } catch (RuntimeException e) {
            int failures = store.recordFailure(id, item.version(), describe(e));
            if (failures >= properties.sync().maxFailures()) {
                store.quarantine(id);
                log.error(
                        "Blog post {} failed {} times at version {}; set aside so the rest can go on",
                        id,
                        failures,
                        item.version(),
                        e);
                return null;
            }
            throw e;
        }
    }

    private void acknowledge(String cursor) {
        try {
            source.acknowledge(cursor);
        } catch (RuntimeException e) {
            // the copy is correct either way; the provider just does not learn it yet, and the next run tells it
            log.warn("Could not acknowledge the blog cursor: {}", e.toString());
        }
    }

    /** {@code bucket/nid} to {@code nid}, the id the site uses in its URLs. */
    static String nid(String sourceId) {
        return sourceId.substring(sourceId.indexOf('/') + 1);
    }

    private static String describe(Throwable e) {
        return e.getClass().getSimpleName() + ": " + e.getMessage();
    }

    private record Prepared(
            String id,
            int version,
            BlogPost post,
            Map<String, com.renaissancerentals.api.blog.source.SourceMedia> media) {}

    List<String> quarantinedPosts() {
        return store.quarantined();
    }
}
