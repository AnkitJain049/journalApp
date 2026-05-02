package com.JournalApp.journalApp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.JournalApp.journalApp.apiResponse.WeatherResponse;
import com.JournalApp.journalApp.entity.User;
import com.JournalApp.journalApp.repository.UserRepository;
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
        WeatherResponse weatherResponse=weatherService.getWeather("Delhi");
        if(weatherResponse!=null){
            greeting=", Weather feels like"+weatherResponse.getCurrent().getFeelslike();
        }
        return new ResponseEntity<>("Hi "+authentication.getName()+greeting, HttpStatus.OK);
    }

}
