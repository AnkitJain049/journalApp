package com.JournalApp.journalApp.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.JournalApp.journalApp.entity.User;

public class UserRepositoryImpl {
    
    @Autowired
    private MongoTemplate mongoTemplate;

    public List<User> getUserForSA() {
        Query query = new Query();
        // Added double backslashes for Java string escaping and a semicolon
        query.addCriteria(Criteria.where("email").regex("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"));
        query.addCriteria(Criteria.where("sentimentAnalysis").is(true));
        
        return mongoTemplate.find(query, User.class);
    }
}
