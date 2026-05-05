package com.JournalApp.journalApp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.kafka.core.KafkaTemplate;

import com.JournalApp.journalApp.apiResponse.WeatherResponse;
import com.JournalApp.journalApp.entity.User;
import com.JournalApp.journalApp.model.SentimentData;
import com.JournalApp.journalApp.repository.UserRepository;
import com.JournalApp.journalApp.service.EmailService;
import com.JournalApp.journalApp.service.UserService;
import com.JournalApp.journalApp.service.WeatherService;

//Controller--->Service--->Repository


@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private KafkaTemplate<String, SentimentData> kafkaTemplate;
    
    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody User user){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username=authentication.getName();
        User userInDb = userService.findByUserName(username);
        if(userInDb != null){
            userInDb.setUserName(user.getUserName());
            userInDb.setPassword(user.getPassword());
            userService.saveNewUser(userInDb);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUserById(){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        userRepository.deleteByUserName(authentication.getName());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<?> greetings(){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String greeting="";
        WeatherService.WeatherCacheResult result = weatherService.getWeatherWithStats("Mumbai");
        if(result.weatherResponse()!=null){
            greeting=", Weather feels like "+result.weatherResponse().getCurrent().getFeelslike();
        }
        String cacheMessage;
        if(result.cacheHit()){
            cacheMessage = String.format(
                " CACHE HIT, time taken to get output: %d ms, time saved : %d ms",
                result.timeMs(), result.savedMs());
        } else {
            cacheMessage = String.format(
                " CACHE MISS : Time taken to get the output: %d ms",
                result.timeMs());
        }
        return new ResponseEntity<>("Hi "+authentication.getName()+greeting+cacheMessage, HttpStatus.OK);
    }

    @GetMapping("/test-email")
    public ResponseEntity<String> testEmail(){
        emailService.sendEmail("doodleankit@gmail.com", "Test Email from Journal App", "This is a test email to verify the email service is working.");
        return new ResponseEntity<>("Test email sent to doodleankit@gmail.com", HttpStatus.OK);
    }

    @GetMapping("/test-kafka")
    public ResponseEntity<String> testKafka(){
        SentimentData testData = SentimentData.builder()
            .email("doodleankit@gmail.com")
            .sentiment("Test Sentiment: This is a test message to verify Kafka is working!")
            .build();
        kafkaTemplate.send("weekly_sentiments", testData.getEmail(), testData);
        return new ResponseEntity<>("Test message sent to Kafka topic 'weekly_sentiments'. Check logs for consumer processing.", HttpStatus.OK);
    }

}
