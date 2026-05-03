package com.JournalApp.journalApp.service;

import com.JournalApp.journalApp.cache.AppCache;

import lombok.extern.slf4j.Slf4j;

import org.apache.juli.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.JournalApp.journalApp.apiResponse.WeatherResponse;
@Component
@Slf4j
public class WeatherService {
    
    @Value("${weather.api.key}")
    public String apiKey;

    @Autowired
    private AppCache appCache;

    @Autowired
    private RestTemplate restTemplate;
    
    public WeatherResponse getWeather(String city){

        String finalApi = appCache.APP_CACHE.get("weather_api").replace("<CITY>", city).replace("<API_KEY>", apiKey);

        
        ResponseEntity<WeatherResponse> response = restTemplate.exchange(finalApi, HttpMethod.GET, null, WeatherResponse.class);
        WeatherResponse body = response.getBody();
        return body;
    }
    
}
