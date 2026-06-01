package com.renaissancerentals.persistence.dao;

import com.renaissancerentals.persistence.entity.RenewNoteEntity;
import org.springframework.data.repository.CrudRepository;

public interface RenewNoteDao extends CrudRepository<RenewNoteEntity, String> {}
