package com.renaissancerentals.persistence.dao;

import org.springframework.data.repository.CrudRepository;

import com.renaissancerentals.persistence.entity.PendingNoteEntity;

public interface PendingNoteDao extends CrudRepository<PendingNoteEntity, String> {
}
