package com.JournalApp.journalApp.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.JournalApp.journalApp.entity.ConfigJournalAppEntity;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.JournalApp.journalApp.repository.ConfigJournalAppRepository;

@Component
public class AppCache {
    
    @Autowired
    private ConfigJournalAppRepository configJournalAppRepository;

    public Map<String, String> APP_CACHE = new HashMap<>();

    @PostConstruct
    public void init(){
        List<ConfigJournalAppEntity> all = configJournalAppRepository.findAll();
        for(ConfigJournalAppEntity configJournalAppEntity : all){
            APP_CACHE.put(configJournalAppEntity.getKey(), configJournalAppEntity.getValue());
        }
    }
}
