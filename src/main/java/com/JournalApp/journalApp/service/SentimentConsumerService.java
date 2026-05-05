package com.JournalApp.journalApp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.JournalApp.journalApp.model.SentimentData;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SentimentConsumerService {
    
    @Autowired
    private EmailService emailService;

    @KafkaListener(topics = "weekly_sentiments", groupId = "weekly-sentiment-group")
    public void consume(SentimentData sentimentData){
        log.info("Received sentiment data for email: {}", sentimentData.getEmail());
        sendEmail(sentimentData);
    }

    private void sendEmail(SentimentData sentimentData){
        emailService.sendEmail(sentimentData.getEmail(), "Sentiment for previous week", sentimentData.getSentiment());
    }
}
