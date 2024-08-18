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
import java.time.format.DateTimeFormatter;

public class WeatherAPI {

    public String getWeatherData() throws IOException {
        // 오늘 날짜에서 하루를 뺀 어제 날짜 계산
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String formattedDate = yesterday.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/AsosHourlyInfoService/getWthrDataList"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=OpEXOLcXTcmAqlBCX1VIsKNPuLTGODfsP5ej0%2Ft6gJY5zG4c6tbGru2wum6dv7cDuSRSi94cuF3sSsq%2Fx3oDFQ%3D%3D"); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호 Default : 10*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); /*한 페이지 결과 수 Default : 1*/
        urlBuilder.append("&" + URLEncoder.encode("dataType", "UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8")); /*요청자료형식(XML/JSON) Default : XML*/
        urlBuilder.append("&" + URLEncoder.encode("dataCd", "UTF-8") + "=" + URLEncoder.encode("ASOS", "UTF-8")); /*자료 분류 코드(ASOS)*/
        urlBuilder.append("&" + URLEncoder.encode("dateCd", "UTF-8") + "=" + URLEncoder.encode("HR", "UTF-8")); /*날짜 분류 코드(HR)*/
        urlBuilder.append("&" + URLEncoder.encode("stnIds","UTF-8") + "=" + URLEncoder.encode("232", "UTF-8")); /*종관기상관측 지점 번호 (활용가이드 하단 첨부 참조)*/

        // 어제 날짜를 조회 기간 시작일과 종료일로 설정
        urlBuilder.append("&" + URLEncoder.encode("startDt", "UTF-8") + "=" + URLEncoder.encode(formattedDate, "UTF-8")); /*조회 기간 시작일(YYYYMMDD)*/
        urlBuilder.append("&" + URLEncoder.encode("startHh", "UTF-8") + "=" + URLEncoder.encode("01", "UTF-8")); /*조회 기간 시작시(HH)*/
        urlBuilder.append("&" + URLEncoder.encode("endDt", "UTF-8") + "=" + URLEncoder.encode(formattedDate, "UTF-8")); /*조회 기간 종료일(YYYYMMDD) (전일(D-1) 까지 제공)*/
        urlBuilder.append("&" + URLEncoder.encode("endHh", "UTF-8") + "=" + URLEncoder.encode("23", "UTF-8")); /*조회 기간 종료시(HH)*/

        // URL 출력
        System.out.println("Generated URL: " + urlBuilder.toString());

        URL url = new URL(urlBuilder.toString());
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

        return sb.toString();
    }

    public JSONObject parseWeatherData(String jsonResponse) {
        JSONObject resultJson = new JSONObject();

        // 전체 JSON 응답을 파싱
        JSONObject response = new JSONObject(jsonResponse).getJSONObject("response");
        JSONObject header = response.getJSONObject("header");

        // 헤더에서 resultCode 추출
        String resultCode = header.getString("resultCode");
        resultJson.put("resultCode", resultCode);

        // 바디에서 아이템들 추출
        if (response.has("body")) {
            JSONObject body = response.getJSONObject("body");
            if (body.has("items")) {
                JSONArray itemsArray = new JSONArray();
                JSONArray itemArray = body.getJSONObject("items").getJSONArray("item");

                // 필요한 항목만 선택적으로 추출
                for (int i = 0; i < itemArray.length(); i++) {
                    JSONObject item = itemArray.getJSONObject(i);

                    JSONObject parsedItem = new JSONObject();
                    parsedItem.put("tm", item.getString("tm"));
                    parsedItem.put("stnId", item.getString("stnId"));
                    parsedItem.put("stnNm", item.getString("stnNm"));
                    parsedItem.put("ta", item.getString("ta"));
                    parsedItem.put("rn", item.getString("rn"));
                    parsedItem.put("hm", item.getString("hm"));
                    parsedItem.put("ts", item.getString("ts"));

                    itemsArray.put(parsedItem);
                }

                resultJson.put("items", itemsArray);
            }
        } else {
            System.err.println("응답 JSON에서 'body' 키를 찾을 수 없습니다.");
        }

        return resultJson;
    }

    public static void main(String[] args) {
        try {
            WeatherAPI api = new WeatherAPI();
            String jsonResponse = api.getWeatherData();
            JSONObject result = api.parseWeatherData(jsonResponse);
            System.out.println(result.toString(4)); // 예쁘게 JSON 출력
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
