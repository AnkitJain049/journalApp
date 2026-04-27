package com.JournalApp.journalApp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.JournalApp.journalApp.entity.JournalEntry;
import com.JournalApp.journalApp.entity.User;
import com.JournalApp.journalApp.repository.JournalEntryRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JournalEntryService {

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private UserService userService;

    public void saveEntry(JournalEntry journalEntry){
        journalEntryRepository.save(journalEntry);
    }
    @Transactional
    public void saveEntry(JournalEntry journalEntry, String username){
        try{
            User user= userService  .findByUserName(username);
            journalEntry.setDate(LocalDateTime.now());
            JournalEntry saved = journalEntryRepository.save(journalEntry);
            user.getJournalEntries().add(saved);
            userService.saveUser(user);
        } catch (Exception e) {
            log.error("Exception",e);
            throw new RuntimeException("An error has occurred"+e);
        }
    }

    public List<JournalEntry> getAll(){
        return journalEntryRepository.findAll();
    }

    public Optional<JournalEntry> findByID(ObjectId id){
        return journalEntryRepository.findById(id);
    }
    @Transactional
    public boolean deleteById(ObjectId id, String username){
        boolean removed=false;
        try{
            User user= userService.findByUserName(username);
            removed = user.getJournalEntries().removeIf(x->x.getId().equals(id));
            if(removed){
                userService.saveUser(user);
                journalEntryRepository.deleteById(id);
            }
        }catch (Exception e) {
            log.error("Exception",e);
            throw new RuntimeException("An error has occurred while deleting entry: "+e);
        }
        return removed;
    }

}
