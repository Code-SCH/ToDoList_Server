package code_wave.todo.controller;

import code_wave.todo.service.ShortTermWeatherAPI;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    @GetMapping
    public JSONObject getWeather() {
        try {
            ShortTermWeatherAPI weatherAPI = new ShortTermWeatherAPI();
            String jsonResponse = weatherAPI.getWeatherData(); // 현재 위치의 실시간 날씨 데이터 가져오기
            double[] latLon = weatherAPI.getCurrentLocation();
            JSONObject locationData = weatherAPI.getLocationData(latLon[0], latLon[1]); // 위치 정보 가져오기
            JSONObject parsedData = weatherAPI.parseWeatherData(jsonResponse, locationData);

            return parsedData; // JSON 데이터를 반환
        } catch (IOException e) {
            e.printStackTrace();
            // 에러 발생 시, 에러 메시지를 JSON 형태로 반환
            JSONObject errorJson = new JSONObject();
            errorJson.put("error", "Unable to retrieve weather data.");
            return errorJson;
        }
    }
}
