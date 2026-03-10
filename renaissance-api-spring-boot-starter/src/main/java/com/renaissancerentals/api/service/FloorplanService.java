package com.renaissancerentals.api.service;

import com.renaissancerentals.api.domain.*;
import com.renaissancerentals.api.domain.mapper.FaqMapper;
import com.renaissancerentals.api.error.NotFoundException;
import com.renaissancerentals.persistence.dao.*;
import com.renaissancerentals.persistence.entity.FloorplanEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
public class FloorplanService {

    private final FloorplanDao floorplanDao;
    private final SimilarFloorplanDao similarFloorplanDao;
    private final FloorplanVariationDao floorplanVariationDao;
    private final TestimonialDao testimonialDao;
    private final FloorplanFaqDao floorplanFaqDao;
    private final FaqMapper faqMapper;

    private final ExecutorService virtualThreadExecutor;

    private final UnitService unitService;
    private final PropertyService propertyService;
    private final WebSpecialService webSpecialService;
    private final AmenityService amenityService;
    private final UtilityService utilityService;

    public Floorplan getFloorplan(String floorplanId) {

        var floorplanFuture = getFloorplanAsync(floorplanId);
        var propertyFuture = propertyService.getPropertySummaryForFloorplanAsync(floorplanId);
        var unitsFuture = unitService.getUnitsForFloorplanAsync(floorplanId);
        var amenitiesFuture = amenityService.getAmenitiesForFloorplanAsync(floorplanId);
        var utilitiesFuture = utilityService.getUtilitiesForFloorplanAsync(floorplanId);
        var webSpecialsFuture = webSpecialService.getWebSpecialForFloorplanAsync(floorplanId);

        var floorplanEntity = floorplanFuture.join().orElseThrow(() -> new NotFoundException(
                String.format("Floorplan with id %s not found", floorplanId)));


        CompletableFuture.allOf(floorplanFuture, propertyFuture, unitsFuture, amenitiesFuture, webSpecialsFuture).join();

        return Floorplan.builder()
                .id(floorplanEntity.getId())
                .name(floorplanEntity.getName())
                .bedroom(floorplanEntity.getBedroom())
                .bathroom(floorplanEntity.getBathroom())
                .style(floorplanEntity.getStyle())
                .allowedPet(floorplanEntity.getAllowedPet())
                .petPolicy(floorplanEntity.getPetPolicy())
                .featured(floorplanEntity.getFeatured())
                .featuredOnMain(floorplanEntity.getFeaturedOnMain())
                .patioIncluded(floorplanEntity.getPatioIncluded())
                .greenCertified(floorplanEntity.getGreenCertified())
                .address(floorplanEntity.getAddress())
                .zipcode(floorplanEntity.getZipcode())
                .videoTourLink(floorplanEntity.getVideoTourLink())
                .threeSixtyVideoTourLink(floorplanEntity.getThreeSixtyVideoTourLink())
                .virtualTourLink(floorplanEntity.getVirtualTourLink())
                .photo(floorplanEntity.getPhoto())
                .coverImage(floorplanEntity.getCoverImage())
                .floorPlanFolderId(floorplanEntity.getFloorPlanFolderId())
                .photosFolderId(floorplanEntity.getPhotosFolderId())
                .photosCount(floorplanEntity.getPhotosCount())
                .description(floorplanEntity.getDescription())
                .vanityLink(floorplanEntity.getVanityLink())
                .htmlTitle(floorplanEntity.getHtmlTitle())
                .metaDescription(floorplanEntity.getMetaDescription())
                .conversionTrackingId1(floorplanEntity.getConversionTrackingId1())
                .conversionTrackingId2(floorplanEntity.getConversionTrackingId2())
                .customCode(floorplanEntity.getCustomCode())
                .highlights(floorplanEntity.getHighlights())
                .specialRent(floorplanEntity.getSpecialRent())
                .specialRentStartDate(floorplanEntity.getSpecialRentStartDate())
                .specialRentEndDate(floorplanEntity.getSpecialRentEndDate())
                .property(propertyFuture.join().orElseThrow(() -> new NotFoundException(
                        String.format("Property for floorplan %s not found", floorplanId))))
                .units(unitsFuture.join())
                .webSpecials(webSpecialsFuture.join())
                .amenities(amenitiesFuture.join())
                .utilities(utilitiesFuture.join())
                .build();

    }

    public List<SimilarFloorplan> findSimilarFloorplans(String floorplanId) {
        return similarFloorplanDao.findAllByFloorplanId(floorplanId).stream()
                .map(entity -> new SimilarFloorplan(entity.getSimilarFloorplanId()))
                .toList();
    }

    public List<FloorplanVariation> findFloorplanVariations(String floorplanId) {
        return floorplanVariationDao.findAllByFloorplanId(floorplanId).stream()
                .map(entity -> new FloorplanVariation(entity.getVariation()))
                .toList();
    }

    public List<Testimonial> findFloorplanTestimonials(String floorplanId) {
        return testimonialDao.findAllByFloorplanId(floorplanId).stream()
                .map(entity -> Testimonial.builder()
                        .tenant(entity.getTenant())
                        .testimonial(entity.getTestimonial()).build())
                .toList();
    }

    public List<WebSpecial> findFloorplanWebSpecials(String floorplanId) {
        return webSpecialService.getWebSpecialForFloorplanAsync(floorplanId).join();
    }

    public List<Faq> findFloorplanFaqs(String floorplanId) {
        return floorplanFaqDao.findAllByFloorplanId(floorplanId).stream()
                .map(faqMapper::toFaq)
                .toList();
    }

    private CompletableFuture<Optional<FloorplanEntity>> getFloorplanAsync(String floorplanId) {
        return CompletableFuture.supplyAsync(() ->
                        floorplanDao.findById(floorplanId)
                , virtualThreadExecutor);
    }
}
