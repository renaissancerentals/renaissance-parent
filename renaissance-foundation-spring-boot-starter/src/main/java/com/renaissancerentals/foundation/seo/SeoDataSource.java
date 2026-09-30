package com.renaissancerentals.foundation.seo;

import java.util.List;
import java.util.Optional;

/**
 * Where SEO reads listing data from. The main site reads its own database, every other site reads the public API of
 * a data host. An empty Optional means the record does not exist (or is deactivated); a
 * {@link SeoDataUnavailableException} means the answer is not known.
 */
public interface SeoDataSource {

    Optional<SeoData.Property> property(String propertyId);

    List<SeoData.Listing> listings();

    Optional<SeoData.Floorplan> floorplan(String floorplanId);

    Optional<SeoData.Unit> unit(String unitId);

    List<SeoData.Faq> propertyFaqs(String propertyId);

    List<SeoData.Faq> floorplanFaqs(String floorplanId);

    List<SeoData.Sublet> sublets();

    Optional<SeoData.Sublet> sublet(String assetKey);

    List<SeoData.Job> jobs();

    Optional<SeoData.Job> job(long id);
}
