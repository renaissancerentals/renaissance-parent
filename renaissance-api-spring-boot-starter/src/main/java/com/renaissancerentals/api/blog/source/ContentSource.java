package com.renaissancerentals.api.blog.source;

import java.util.List;
import java.util.Optional;

/**
 * Where the blog's posts come from. The rest of the site depends on this, not on any one provider: the notes platform is
 * one adapter, and the Blogger arrangement it replaces was another.
 */
public interface ContentSource {

    /** What changed after {@code cursor} (null or empty for the beginning). */
    SourcePage changes(String cursor, int limit);

    /** Everything public right now. */
    List<SourceIndexEntry> index();

    /** Says that everything up to {@code cursor} is committed here. */
    void acknowledge(String cursor);

    /** An image or document of the posts, by its {@code bucket/id}; empty when it is not there or too large. */
    Optional<SourceMedia> media(String ref, long maxBytes);
}
