package com.JournalApp.journalApp.repository;

import com.JournalApp.journalApp.entity.ConfigJournalAppEntity;
import com.JournalApp.journalApp.entity.JournalEntry;
import com.JournalApp.journalApp.entity.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConfigJournalAppRepository extends MongoRepository<ConfigJournalAppEntity, ObjectId> {
}
