package edu.android.dustdrug;

import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class AirQulity_API {
    public static final String TAG = "edu.android";

    /**
     * 공공데이터 포털 API (https://www.data.go.kr)
     *
     * 측정소정보 조회 서비스 인증키 (운영)
     *
     * 일일 트래픽 : 10000
     *
     * 사용기간 : 2018-05-17 ~ 2020-05-17
     * 1차 연장 : 2020-05-17 ~ 2022-05-17
     */
    String AuthenticationKey = "2WjM1G6ETI%2F3HKoHrAC9MhjgY3PufrijH35VWAgVnh3A5ZrEkBkXovDVizsiQoKm7FDHO2AmW4LG%2FA2oiF8new%3D%3D";

    private Gson gson = new Gson();

    public ArrayList<GetAPIGsonTM.List> getChangeTm(String original) { // 동이름이 있을 시 동을 tm 주소로 바꿔주는 메서드  검색 시 원하는 위차를 tm 좌표로 받아오는 메서드
        for (int i = 0; i <= 9; i++) {// 동 이름에 숫자가 있을때 바꿔줌
            original = original.replace(String.valueOf(i), "");
        }

        String uri = "http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getTMStdrCrdnt?umdName=" + original
                + "&pageNo=1&numOfRows=1000&ServiceKey=" + AuthenticationKey + "&_returnType=json";

        HttpURLConnection connection = null;
        BufferedReader reader = null;
        String json = null;

        try {
            URL url = new URL(uri);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream is = url.openStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuffer buffer = new StringBuffer();

            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            json = buffer.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if(json == null){
            Log.i(TAG,"API(json) null");
        }

        GetAPIGsonTM getAPIGsonTM = gson.fromJson(json, GetAPIGsonTM.class);


        if(getAPIGsonTM == null){
            Log.i(TAG,"좌표 정보 없음.");
        }

        return getAPIGsonTM.list;
    }

    class GetAPIGsonTM { // Gson 에서 가져올 class

        ArrayList<List> list;

        class List {
            String sggName;//구
            String sidoName;//시도
            String umdName;// 동읍
            double tmX;
            double tmY;

            public String getUmdName() {
                return umdName;
            }

            public String getSggName() {
                return sggName;
            }

            public String getSidoName() {
                return sidoName;
            }

            public double getTmX() {
                return tmX;
            }

            public double getTmY() {
                return tmY;
            }
        }
    }

    // tm좌표 받아오기
    public ArrayList<GetAPIGsonMeasuringStation.List> getMeasuringStation (double tmX , double tmY){    // tm 으로 가까운 측정소명 받아오기

        String uri = "http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getNearbyMsrstnList?tmX=" + tmX + "&tmY=" + tmY
                + "&pageNo=1&numOfRows=100&ServiceKey=" + AuthenticationKey + "&_returnType=json";

        HttpURLConnection connection = null;
        BufferedReader reader = null;
        String json = null;

        try {
            URL url = new URL(uri);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream is = url.openStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            StringBuffer buffer = new StringBuffer();

            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            json = buffer.toString();

        } catch (Exception e) {

            e.printStackTrace();
        }

        GetAPIGsonMeasuringStation getAPIGsonMeasuringStation = gson.fromJson(json,GetAPIGsonMeasuringStation.class);
        return getAPIGsonMeasuringStation.list;

    }

    class GetAPIGsonMeasuringStation{
        ArrayList<List> list;
        class List{
            String stationName;

            public String getStationName() {
                return stationName;
            }
        }
    }

    public ArrayList<GetAPIGsonMainData.List> getDataclass(String stationName){

        String uri = "http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getMsrstn" +
                "AcctoRltmMesureDnsty?stationName=" + stationName +
                "&dataTerm=month&pageNo=1&numOfRows=24&ServiceKey=" + AuthenticationKey +
                "&ver=1.3&_returnType=json";

        HttpURLConnection connection = null;
        BufferedReader reader = null;
        String json = null;

        try {
            URL url = new URL(uri);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream is = url.openStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            StringBuffer buffer = new StringBuffer();

            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            json = buffer.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }

        json=json.replace("-","-1");

        GetAPIGsonMainData getAPIGsonMainData = gson.fromJson(json,GetAPIGsonMainData.class);
        return getAPIGsonMainData.list;
    }

    class GetAPIGsonMainData {

        ArrayList<List> list;

        class List {

            String coGrade;             // 일산화 등급
            String coValue;             // 일산화 량
            String dataTime;            // 시간
            String khaiGrade;           // 통합대기환경 지수
            String khaiValue;           // 통합대기환경 수치
            String mangName;            // 측정망
            String no2Grade;            // 이산화 질소 등급
            String no2Value;            // 이산화 질소량
            String o3Grade;             // 오존 등급
            String o3Value;             // 오존 량
            String pm10Gradel;          // 미세먼지 등급
            String pm10Grade1h;         // 미세먼지 1시간 등급
            String pm10Value;           // 미세먼지 측정치
            String pm10Value24;         // 미세먼지 24 시간 등급
            String pm25Grade;           // 초미세먼지 등급
            String pm25Grade1h;         // 초미세먼지 1시간 등급
            String pm25Value;           // 초미세먼지 값
            String pm25Value24;         // 초미세먼지 24시간 값
            String so2Grade;            // 이산화황 등급
            String so2Value;            // 이산화황 량

        }

    }

}