package com.renaissancerentals.persistence.dao;

import com.renaissancerentals.persistence.entity.PendingNoteEntity;
import org.springframework.data.repository.CrudRepository;

public interface PendingNoteDao extends CrudRepository<PendingNoteEntity, String> {}
