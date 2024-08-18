package code_wave.todo.controller;

import code_wave.todo.service.WeatherAPI;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {
    @GetMapping
    public String getWeather() {
        try {
            WeatherAPI api = new WeatherAPI();
            String jsonResponse = api.getWeatherData();
            JSONObject result = api.parseWeatherData(jsonResponse);
            return result.toString(4); // 예쁘게 JSON 출력
        } catch (IOException e) {
            e.printStackTrace();
            return "{\"error\":\"Failed to fetch weather data\"}";
        }
    }
}
