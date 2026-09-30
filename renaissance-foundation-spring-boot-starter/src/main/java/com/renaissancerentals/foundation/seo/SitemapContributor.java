package com.renaissancerentals.foundation.seo;

import java.util.List;

/** Adds URLs to sitemap.xml. Only list URLs that are canonical to this site. */
public interface SitemapContributor {

    List<SitemapEntry> entries();
}
