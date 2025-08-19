package com.renaissancerentals.persistence.dao;

import org.springframework.data.repository.CrudRepository;

import com.renaissancerentals.persistence.entity.RenewNoteEntity;

public interface RenewNoteDao extends CrudRepository<RenewNoteEntity, String> {

}
