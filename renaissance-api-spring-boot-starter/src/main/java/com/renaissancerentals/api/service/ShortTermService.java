package com.renaissancerentals.api.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.renaissancerentals.api.domain.ShortTermFloorplan;
import com.renaissancerentals.api.domain.projection.FloorplanDetails;
import com.renaissancerentals.api.domain.projection.PropertySummary;
import com.renaissancerentals.api.error.NotFoundException;
import com.renaissancerentals.api.repository.FloorplanRepository;
import com.renaissancerentals.api.repository.PropertyRepository;
import com.renaissancerentals.persistence.dao.ShortTermFloorplanDao;
import com.renaissancerentals.persistence.entity.ShortTermFloorplanEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShortTermService {
    private final FloorplanRepository floorplanRepository;

    private final PropertyRepository propertyRepository;

    private final ShortTermFloorplanDao shortTermFloorplanDao;

    private final ExecutorService virtualThreadExecutor;

    public List<ShortTermFloorplan> getShortTermFloorplansForProperty(String propertyId){

        var propertySummaryFuture = getPropertySummaryAsync(propertyId);
        var floorplanDetailsFuture = getFloorplanDetailsForPropertyAsync(propertyId);
        var shortTermEntityFuture = getShortTermsForPropertyAsync(propertyId);

        var propertySummary = propertySummaryFuture.join()
                .orElseThrow(() -> new NotFoundException(String.format("Property with id %s not found",propertyId)));
        CompletableFuture.allOf(propertySummaryFuture,floorplanDetailsFuture,shortTermEntityFuture).join();

        Map<String, FloorplanDetails> floorplanDetailsMap = floorplanDetailsFuture.join().stream()
                .collect(Collectors.toMap(FloorplanDetails::getId,Function.identity()));
        return shortTermEntityFuture.join().stream().map(shortTermEntity -> {
            var floorplanDetails = floorplanDetailsMap.get(shortTermEntity.getFloorplanId());
            return ShortTermFloorplan.builder().id(shortTermEntity.getFloorplanId()).name(floorplanDetails.getName())
                    .bedroom(floorplanDetails.getBedroom()).bathroom(floorplanDetails.getBathroom())
                    .style(floorplanDetails.getStyle()).address(floorplanDetails.getAddress())
                    .zipcode(floorplanDetails.getZipcode()).videoTourLink(floorplanDetails.getVideoTourLink())
                    .threeSixtyVideoTourLink(floorplanDetails.getThreeSixtyVideoTourLink())
                    .virtualTourLink(floorplanDetails.getVirtualTourLink()).photo(floorplanDetails.getPhoto())
                    .coverImage(floorplanDetails.getCoverImage())
                    .floorPlanFolderId(floorplanDetails.getFloorPlanFolderId())
                    .photosFolderId(floorplanDetails.getPhotosFolderId()).photosCount(floorplanDetails.getPhotosCount())
                    .description(floorplanDetails.getDescription()).vanityLink(floorplanDetails.getVanityLink())
                    .htmlTitle(floorplanDetails.getHtmlTitle()).metaDescription(floorplanDetails.getMetaDescription())
                    .conversionTrackingId1(floorplanDetails.getConversionTrackingId1())
                    .conversionTrackingId2(floorplanDetails.getConversionTrackingId2())
                    .customCode(floorplanDetails.getCustomCode()).highlights(floorplanDetails.getHighlights())
                    .property(propertySummary).amenities(floorplanDetails.getAmenities())
                    .priceFor2To4Days(shortTermEntity.getPriceFor2To4Days())
                    .priceFor5To13Days(shortTermEntity.getPriceFor5To13Days())
                    .priceFor14To29Days(shortTermEntity.getPriceFor14To29Days())
                    .priceFor1To4Months(shortTermEntity.getPriceFor1To4Months())
                    .priceFor4andMoreMonths(shortTermEntity.getPriceFor4andMoreMonths())
                    .squareFoot(shortTermEntity.getSquareFoot()).build();
        }

        ).toList();
    }

    public ShortTermFloorplan getShortTermFloorplan(String floorplanId){

        var propertySummaryFuture = getPropertySummaryForFloorplanAsync(floorplanId);
        var floorplanDetailsFuture = getFloorplanDetailsAsync(floorplanId);
        var shortTermEntityFuture = getShortTermsForAsync(floorplanId);

        var propertySummary = propertySummaryFuture.join().orElseThrow(
                () -> new NotFoundException(String.format("Property for floorplan: %s not found",floorplanId)));
        CompletableFuture.allOf(propertySummaryFuture,floorplanDetailsFuture,shortTermEntityFuture).join();

        var floorplanDetails = floorplanDetailsFuture.join()
                .orElseThrow(() -> new NotFoundException(String.format("Floorplan for Id: %s not found",floorplanId)));
        var shortTermEntity = shortTermEntityFuture.join()
                .orElseThrow(() -> new NotFoundException(String.format("ShortTerm for Id: %s not found",floorplanId)));

        return ShortTermFloorplan.builder().id(shortTermEntity.getFloorplanId()).name(floorplanDetails.getName())
                .bedroom(floorplanDetails.getBedroom()).bathroom(floorplanDetails.getBathroom())
                .style(floorplanDetails.getStyle()).address(floorplanDetails.getAddress())
                .zipcode(floorplanDetails.getZipcode()).videoTourLink(floorplanDetails.getVideoTourLink())
                .threeSixtyVideoTourLink(floorplanDetails.getThreeSixtyVideoTourLink())
                .virtualTourLink(floorplanDetails.getVirtualTourLink()).photo(floorplanDetails.getPhoto())
                .coverImage(floorplanDetails.getCoverImage()).floorPlanFolderId(floorplanDetails.getFloorPlanFolderId())
                .photosFolderId(floorplanDetails.getPhotosFolderId()).photosCount(floorplanDetails.getPhotosCount())
                .description(floorplanDetails.getDescription()).vanityLink(floorplanDetails.getVanityLink())
                .htmlTitle(floorplanDetails.getHtmlTitle()).metaDescription(floorplanDetails.getMetaDescription())
                .conversionTrackingId1(floorplanDetails.getConversionTrackingId1())
                .conversionTrackingId2(floorplanDetails.getConversionTrackingId2())
                .customCode(floorplanDetails.getCustomCode()).highlights(floorplanDetails.getHighlights())
                .property(propertySummary).amenities(floorplanDetails.getAmenities())
                .priceFor2To4Days(shortTermEntity.getPriceFor2To4Days())
                .priceFor5To13Days(shortTermEntity.getPriceFor5To13Days())
                .priceFor14To29Days(shortTermEntity.getPriceFor14To29Days())
                .priceFor1To4Months(shortTermEntity.getPriceFor1To4Months())
                .priceFor4andMoreMonths(shortTermEntity.getPriceFor4andMoreMonths())
                .squareFoot(shortTermEntity.getSquareFoot()).build();
    }

    private CompletableFuture<Optional<PropertySummary>> getPropertySummaryAsync(String propertyId){
        return CompletableFuture.supplyAsync(() -> propertyRepository.getPropertySummaryForProperty(propertyId),
                virtualThreadExecutor);
    }

    private CompletableFuture<List<FloorplanDetails>> getFloorplanDetailsForPropertyAsync(String propertyId){
        return CompletableFuture.supplyAsync(() -> floorplanRepository.getFloorplanDetailsForProperty(propertyId),
                virtualThreadExecutor);
    }

    private CompletableFuture<List<ShortTermFloorplanEntity>> getShortTermsForPropertyAsync(String propertyId){
        return CompletableFuture.supplyAsync(() -> shortTermFloorplanDao.findByPropertyId(propertyId),
                virtualThreadExecutor);
    }

    private CompletableFuture<Optional<PropertySummary>> getPropertySummaryForFloorplanAsync(String floorplanId){
        return CompletableFuture.supplyAsync(() -> propertyRepository.getPropertySummaryForFloorplan(floorplanId),
                virtualThreadExecutor);
    }

    private CompletableFuture<Optional<FloorplanDetails>> getFloorplanDetailsAsync(String floorplanId){
        return CompletableFuture.supplyAsync(() -> floorplanRepository.getFloorplanDetails(floorplanId),
                virtualThreadExecutor);
    }

    private CompletableFuture<Optional<ShortTermFloorplanEntity>> getShortTermsForAsync(String floorplanId){
        return CompletableFuture.supplyAsync(() -> shortTermFloorplanDao.findOneByFloorplanId(floorplanId),
                virtualThreadExecutor);
    }
}
