package com.renaissancerentals.api.service;

import org.springframework.stereotype.Service;

import com.renaissancerentals.api.config.PropertyConfigProperties;
import com.renaissancerentals.api.domain.PropertyContact;
import com.renaissancerentals.api.domain.TeamMemberDetails;
import com.renaissancerentals.api.domain.mapper.PropertyMapper;
import com.renaissancerentals.api.repository.PropertyRepository;
import com.renaissancerentals.persistence.dao.PropertyDao;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyDao propertyDao;
    private final PropertyMapper propertyMapper;
    private final PropertyRepository propertyRepository;

    private final PropertyConfigProperties propertyConfigProperties;

    public PropertyContact getPropertyContact(String propertyId){

        return propertyDao.findById(propertyId).map(propertyMapper::toPropertyContact)
                .orElse(PropertyContact.builder().propertyName("Renaissance Rentals")
                        .email(propertyConfigProperties.defaultPropertyEmail())
                        .phone(propertyConfigProperties.defaultPropertyPhone()).build());
    }

    public TeamMemberDetails getPropertyManager(String propertyId){
        return propertyRepository.getPropertyManager(propertyId)
                .orElse(TeamMemberDetails.builder().email(propertyConfigProperties.defaultPropertyEmail())
                        .name(propertyConfigProperties.defaultPropertyManager()).build());
    }

    public String getPropertyUrl(String propertyId){
        return propertyConfigProperties.propertyUrls().getOrDefault(propertyId,
                propertyConfigProperties.defaultPropertyUrl());
    }
}
