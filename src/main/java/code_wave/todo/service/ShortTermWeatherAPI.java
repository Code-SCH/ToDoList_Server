package code_wave.todo.service;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ShortTermWeatherAPI {

    public String getWeatherData() throws IOException {
        // 현재 위치의 위도와 경도를 가져옴
        double[] latLon = getCurrentLocation();
        double latitude = latLon[0];
        double longitude = latLon[1];

        // 위도와 경도 정보를 출력
        //System.out.println("현재 위치의 위도: " + latitude + ", 경도: " + longitude);

        // 현재 날짜를 yyyyMMdd 형식으로 포맷
        LocalDate currentDate = LocalDate.now();
        String formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // 현재 시간을 기반으로 정시 시간 계산 (현재 시각 그대로 사용)
        LocalDateTime currentDateTime = LocalDateTime.now();
        int hour = currentDateTime.getHour();
        String baseTime;
        if (hour < 10) {
            baseTime = "0" + hour + "00"; // 10시 이전이면 앞에 0을 추가
        } else {
            baseTime = hour + "00"; // 10시 이후는 그대로 사용
        }

        // 위도와 경도를 기상청 X, Y 좌표로 변환
        int[] xy = convertLatLonToXY(latitude, longitude);
        String nx = String.valueOf(xy[0]);
        String ny = String.valueOf(xy[1]);

        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=OpEXOLcXTcmAqlBCX1VIsKNPuLTGODfsP5ej0%2Ft6gJY5zG4c6tbGru2wum6dv7cDuSRSi94cuF3sSsq%2Fx3oDFQ%3D%3D"); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("1000", "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("dataType", "UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8")); /*요청자료형식(XML/JSON) Default: XML*/
        urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "=" + URLEncoder.encode(formattedDate, "UTF-8")); /*오늘 날짜*/
        urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "=" + URLEncoder.encode(baseTime, "UTF-8")); /*현재 정시 시간*/
        urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode(nx, "UTF-8")); /*예보지점의 X 좌표값*/
        urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode(ny, "UTF-8")); /*예보지점의 Y 좌표값*/

        // 생성된 URL을 출력
        String finalUrl = urlBuilder.toString();
        //System.out.println("API 요청 URL: " + finalUrl);

        URL url = new URL(finalUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        System.out.println("Response code: " + conn.getResponseCode());

        BufferedReader rd;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();

        // 응답 데이터를 출력
        //System.out.println("API 응답: " + sb.toString());

        return sb.toString();
    }

    // 응답 데이터를 파싱하여 변수로 변환하는 메서드
    public JSONObject parseWeatherData(String jsonResponse) {
        JSONObject resultJson = new JSONObject();
        JSONObject response = new JSONObject(jsonResponse).getJSONObject("response");
        JSONObject body = response.getJSONObject("body");
        JSONArray items = body.getJSONObject("items").getJSONArray("item");

        // 각 항목에 맞게 데이터 변환
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            String category = item.getString("category");
            double obsrValue = item.getDouble("obsrValue");

            switch (category) {
                case "T1H":
                    resultJson.put("Temperature", obsrValue + " °C");
                    break;
                case "RN1":
                    resultJson.put("1HourRainfall", obsrValue + " mm");
                    break;
                case "UUU":
                    resultJson.put("EastWestWindComponent", obsrValue + " m/s");
                    break;
                case "VVV":
                    resultJson.put("NorthSouthWindComponent", obsrValue + " m/s");
                    break;
                case "REH":
                    resultJson.put("Humidity", obsrValue + " %");
                    break;
                case "PTY":
                    String precipitationType = decodePrecipitationType((int) obsrValue);
                    resultJson.put("PrecipitationType", precipitationType);
                    break;
                case "VEC":
                    resultJson.put("WindDirection", obsrValue + " deg");
                    break;
                case "WSD":
                    resultJson.put("WindSpeed", obsrValue + " m/s");
                    break;
                default:
                    System.out.println("알 수 없는 카테고리: " + category);
            }
        }
        return resultJson;
    }

    // PTY 코드 값을 해석하는 메서드
    private String decodePrecipitationType(int code) {
        switch (code) {
            case 0:
                return "없음";
            case 1:
                return "비";
            case 2:
                return "비/눈";
            case 3:
                return "눈";
            case 5:
                return "빗방울";
            case 6:
                return "빗방울눈날림";
            case 7:
                return "눈날림";
            default:
                return "알 수 없음";
        }
    }

    // 현재 위치의 위도와 경도를 가져오는 메서드
    private double[] getCurrentLocation() throws IOException {
        String apiUrl = "http://ip-api.com/json";
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        JSONObject json = new JSONObject(response.toString());
        double latitude = json.getDouble("lat");
        double longitude = json.getDouble("lon");

        return new double[]{latitude, longitude};
    }

    // 위도, 경도를 기상청 X, Y 좌표로 변환하는 메서드
    private int[] convertLatLonToXY(double lat, double lon) {
        double RE = 6371.00877; // 지구 반경(km)
        double GRID = 5.0; // 격자 간격(km)
        double SLAT1 = 30.0; // 표준 위도 1(도)
        double SLAT2 = 60.0; // 표준 위도 2(도)
        double OLON = 126.0; // 기준점 경도(도)
        double OLAT = 38.0; // 기준점 위도(도)
        double XO = 210 / GRID; // 기준점 X좌표(GRID)
        double YO = 675 / GRID; // 기준점 Y좌표(GRID)

        double DEGRAD = Math.PI / 180.0;
        double RADDEG = 180.0 / Math.PI;

        double re = RE / GRID;
        double slat1 = SLAT1 * DEGRAD;
        double slat2 = SLAT2 * DEGRAD;
        double olon = OLON * DEGRAD;
        double olat = OLAT * DEGRAD;

        double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
        double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
        double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);

        double ra = Math.tan(Math.PI * 0.25 + lat * DEGRAD * 0.5);
        ra = re * sf / Math.pow(ra, sn);
        double theta = lon * DEGRAD - olon;
        if (theta > Math.PI) theta -= 2.0 * Math.PI;
        if (theta < -Math.PI) theta += 2.0 * Math.PI;
        theta *= sn;

        int x = (int) (ra * Math.sin(theta) + XO + 0.5);
        int y = (int) (ro - ra * Math.cos(theta) + YO + 0.5);

        return new int[]{x, y};
    }

    public static void main(String[] args) {
        try {
            ShortTermWeatherAPI weatherAPI = new ShortTermWeatherAPI();
            String jsonResponse = weatherAPI.getWeatherData(); // 현재 위치의 실시간 날씨 데이터 가져오기
            JSONObject parsedData = weatherAPI.parseWeatherData(jsonResponse);
            System.out.println(parsedData.toString(4)); // 파싱된 JSON 데이터를 예쁘게 출력
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
