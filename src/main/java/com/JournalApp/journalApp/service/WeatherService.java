package com.JournalApp.journalApp.service;

import com.JournalApp.journalApp.cache.AppCache;

import lombok.extern.slf4j.Slf4j;

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

    public record WeatherCacheResult(WeatherResponse weatherResponse, boolean cacheHit, long timeMs, long savedMs) {}
    
    @Value("${weather.api.key}")
    public String apiKey;

    @Autowired
    private AppCache appCache;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisService redisService;

    private long totalApiFetchTimeMs = 0;
    private long apiFetchCount = 0;
    private long cacheHits = 0;
    private long totalEstimatedSavedTimeMs = 0;

    public WeatherResponse getWeather(String city) {
        return getWeatherWithStats(city).weatherResponse();
    }

    public WeatherCacheResult getWeatherWithStats(String city) {
        long start = System.currentTimeMillis();
        WeatherResponse weatherResponse = redisService.get("Weather_of_" + city, WeatherResponse.class);
        long elapsedMs;

        if (weatherResponse != null) {
            elapsedMs = System.currentTimeMillis() - start;
            cacheHits++;
            long averageApiMs = apiFetchCount > 0 ? totalApiFetchTimeMs / apiFetchCount : 0;
            long savedMs = averageApiMs > elapsedMs ? averageApiMs - elapsedMs : 0;
            totalEstimatedSavedTimeMs += savedMs;
            log.info("Redis hit for {}: read={}ms, saved={}ms", city, elapsedMs, savedMs);
            return new WeatherCacheResult(weatherResponse, true, elapsedMs, savedMs);
        }

        String finalApi = appCache.APP_CACHE.get("weather_api").replace("<CITY>", city).replace("<API_KEY>", apiKey);
        long apiStart = System.currentTimeMillis();
        ResponseEntity<WeatherResponse> response = restTemplate.exchange(finalApi, HttpMethod.GET, null, WeatherResponse.class);
        long apiElapsedMs = System.currentTimeMillis() - apiStart;
        apiFetchCount++;
        totalApiFetchTimeMs += apiElapsedMs;

        WeatherResponse body = response.getBody();
        if (body != null) {
            redisService.set("Weather_of_" + city, body, 300L);
        }
        log.info("Weather API miss for {}: apiTime={}ms", city, apiElapsedMs);
        return new WeatherCacheResult(body, false, apiElapsedMs, 0);
    }
}
