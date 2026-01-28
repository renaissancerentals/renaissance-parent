package com.renaissancerentals.persistence.dao;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.renaissancerentals.persistence.entity.ApplicationEmailEntity;

public interface ApplicationEmailDao extends CrudRepository<ApplicationEmailEntity, UUID> {

}
