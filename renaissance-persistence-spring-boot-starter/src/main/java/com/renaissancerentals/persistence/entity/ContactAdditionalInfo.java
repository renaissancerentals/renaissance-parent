package com.renaissancerentals.persistence.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactAdditionalInfo implements Serializable {
    private String amenities;
    private String bedrooms;
    private String floorPlan;
    private String hearAboutUs;
    private String lowerRent;
    private String upperRent;
    private String moveInDate;
    private String pets;
    private String communities;
}
