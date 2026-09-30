package com.renaissancerentals.foundation.seo;

import java.util.Optional;

/** Resolves the page metadata for a route. The first contributor (by {@code @Order}) that answers wins. */
public interface SeoPageContributor {

    /** @param path normalised request path, no trailing slash except for the root */
    Optional<PageMeta> resolve(String path);
}
