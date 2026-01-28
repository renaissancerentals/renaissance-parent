package com.renaissancerentals.persistence.dao;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.renaissancerentals.persistence.entity.ContactEmailEntity;

public interface ContactEmailDao extends CrudRepository<ContactEmailEntity, UUID> {

}
