package com.renaissancerentals.api.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import org.springframework.stereotype.Service;

import com.renaissancerentals.api.config.PropertyConfigProperties;
import com.renaissancerentals.api.domain.Faq;
import com.renaissancerentals.api.domain.TeamMember;
import com.renaissancerentals.api.domain.mapper.FaqMapper;
import com.renaissancerentals.api.domain.mapper.PropertyMapper;
import com.renaissancerentals.api.domain.projection.*;
import com.renaissancerentals.api.error.NotFoundException;
import com.renaissancerentals.api.repository.PropertyRepository;
import com.renaissancerentals.persistence.dao.PropertyDao;
import com.renaissancerentals.persistence.dao.PropertyFaqDao;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyDao propertyDao;
    private final PropertyMapper propertyMapper;
    private final PropertyRepository propertyRepository;
    private final PropertyFaqDao propertyFaqDao;

    private final PropertyConfigProperties propertyConfigProperties;
    private final FaqMapper faqMapper;
    private final ExecutorService virtualThreadExecutor;

    public PropertyContact getPropertyContact(String propertyId){

        return propertyDao.findById(propertyId).map(propertyMapper::toPropertyContact)
                .orElse(PropertyContact.builder().propertyName("Renaissance Rentals")
                        .email(propertyConfigProperties.defaultPropertyEmail())
                        .phone(propertyConfigProperties.defaultPropertyPhone()).build());
    }

    public TeamMember getPropertyManager(String propertyId){
        return propertyRepository.getPropertyManager(propertyId)
                .orElse(TeamMember.builder().email(propertyConfigProperties.defaultPropertyEmail())
                        .name(propertyConfigProperties.defaultPropertyManager()).build());
    }

    public String getPropertyUrl(String propertyId){
        return propertyConfigProperties.propertyUrls().getOrDefault(propertyId,
                propertyConfigProperties.defaultPropertyUrl());
    }

    public PropertyDetails getProperty(String propertyId){
        return propertyRepository.getProperty(propertyId)
                .orElseThrow(() -> new NotFoundException(String.format("Property with id: %s not found",propertyId)));
    }

    public List<PropertyListing> getPropertyListings(){
        return propertyRepository.getPropertyListings();
    }

    public List<FloorplanListing> getFloorplanListingsForProperty(String propertyId){
        return propertyRepository.getPropertyListing(propertyId).map(PropertyListing::getFloorplans).orElse(List.of());
    }

    public List<Faq> getPropertyFaqs(String propertyId){
        return propertyFaqDao.findAllByPropertyId(propertyId).stream().map(faqMapper::toFaq).toList();
    }

    public CompletableFuture<Optional<PropertySummary>> getPropertySummaryForFloorplanAsync(String floorplanId){
        return CompletableFuture.supplyAsync(() -> propertyRepository.getPropertySummaryForFloorplan(floorplanId),
                virtualThreadExecutor);
    }
}
