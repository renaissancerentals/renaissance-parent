package com.renaissancerentals.api.service;

import org.springframework.stereotype.Service;

import com.renaissancerentals.api.domain.PropertyContact;
import com.renaissancerentals.api.domain.mapper.PropertyMapper;
import com.renaissancerentals.persistence.dao.PropertyDao;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyDao propertyRepository;
    private final PropertyMapper propertyMapper;

    public PropertyContact getPropertyContact(String propertyId){

        return propertyRepository.findById(propertyId).map(propertyMapper::toPropertyContact)
                .orElse(PropertyContact.builder().propertyName("Renaissance Rentals").build());
    }
}
