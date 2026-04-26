package com.JournalApp.journalApp.repository;

import com.JournalApp.journalApp.entity.JournalEntry;

import com.JournalApp.journalApp.entity.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, ObjectId> {
    User findByUserName(String userName);
    void deleteByUserName(String userName);
}
