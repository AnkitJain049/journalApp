package com.JournalApp.journalApp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.JournalApp.journalApp.apiResponse.WeatherResponse;
@Component
public class WeatherService {
    
    private static final String apiKey = "0468b2819bb83b2213c859532a0d25af";

    @Autowired
    private RestTemplate restTemplate;
    
    private static final String url = "http://api.weatherstack.com/current?access_key=API_KEY&query=CITY";
    
    public WeatherResponse getWeather(String city){
        String finalApi=url.replace("CITY", city).replace("API_KEY",apiKey);
        ResponseEntity<WeatherResponse> response = restTemplate.exchange(finalApi, HttpMethod.GET, null, WeatherResponse.class);
        WeatherResponse body= response.getBody();
        return body;
    }
    
}
