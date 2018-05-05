package edu.android.dustdrug;

import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class AirQulity_API {
    public static final String TAG = "edu.android";

    String AuthenticationKey = "2WjM1G6ETI%2F3HKoHrAC9MhjgY3PufrijH35VWAgVnh3A5ZrEkBkXovDVizsiQoKm7FDHO2AmW4LG%2FA2oiF8new%3D%3D"; // 인증키 1(My)
//    String AuthenticationKey = "x8wVPw0ebqpfGHvFnY1u0ESYkwICViliP26eUlnenbgGWPAes0aX0sNMb47qg22efyWm4e3UDMB%2FUAOmIQUIaQ%3D%3D"; // 인증키 2
    private Gson gson = new Gson();


    public ArrayList<GetAPIGsonTM.List> getChangeTm(String original) { // 동이름이 있을 시 동을 tm 주소로 바꿔주는 메서드  검색 시 원하는 위차를 tm 좌표로 받아오는 메서드
        for (int i = 0; i <= 9; i++) {// 동 이름에 숫자가 있을때 바꿔줌
            original = original.replace(String.valueOf(i), "");
        }

        String uri = "http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getTMStdrCrdnt?umdName=" + original + "&pageNo=1&numOfRows=1000&ServiceKey=" + AuthenticationKey + "&_returnType=json";
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
                buffer.append(line+"\n");
            }

            json = buffer.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if(json == null){
            Log.i(TAG,"json null");
        }

        GetAPIGsonTM getAPIGsonTM = gson.fromJson(json,GetAPIGsonTM.class);


        if(getAPIGsonTM == null){
            Log.i("s1","여기1");
        }
        return getAPIGsonTM.list;
    }

    class GetAPIGsonTM { //파서에서 가져올 class
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
    //tm좌표 받아오기 끝

    public ArrayList<GetAPIGsonMeasuringStation.List> getMeasuringStation (double tmX , double tmY){//tm 으로 가까운 측정소명 받아오기
        String uri = "http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getNearbyMsrstnList?tmX="+tmX+"&tmY="+tmY+"&pageNo=1&numOfRows=100&ServiceKey="+AuthenticationKey+"&_returnType=json";
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
                buffer.append(line+"\n");
            }
            json=buffer.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        GetAPIGsonMeasuringStation getAPIGsonMeasuringStation =
                gson.fromJson(json,GetAPIGsonMeasuringStation.class);
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
                buffer.append(line+"\n");
            }
            json=buffer.toString();

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
            String coGrade;//일산화 등급
            String coValue;//일산화 량
            String dataTime;//시간
            String khaiGrade;// 통합대기환경 지수
            String khaiValue;//  통합대기환경 수치
            String mangName;// 측정망
            String no2Grade;// 이산화 질소 등급
            String no2Value;// 이산화 질소량
            String o3Grade;//오존 등급
            String o3Value;//오존 량
            String pm10Gradel; //미먼 등급
            String pm10Grade1h; //밈먼등급 1시간 등급
            String pm10Value; //미먼 측정치
            String pm10Value24; // 미먼 24 시간 등급
            String pm25Grade; // 초미먼 등급
            String pm25Grade1h;//초미먼 1시간 등급
            String pm25Value;//초미먼 값
            String pm25Value24;//초미먼 24시간 값
            String so2Grade;//이산화황 등급
            String so2Value; // 이산화황 량
//            int coGrade;//일산화 등급
//            double coValue;//일산화 량
//            String dataTime;//시간
//            int khaiGrade;// 통합대기환경 지수
//            int khaiValue;//  통합대기환경 수치
//            String mangName;// 측정망
//            int no2Grade;// 이산화 질소 등급
//            double no2Value;// 이산화 질소량
//            int o3Grade;//오존 등급
//            double o3Value;//오존 량
//            int pm10Gradel; //미먼 등급
//            int pm10Grade1h; //밈먼등급 1시간 등급
//            int pm10Value; //미먼 측정치
//            int pm10Value24; // 미먼 24 시간 등급
//            int pm25Grade; // 초미먼 등급
//            int pm25Grade1h;//초미먼 1시간 등급
//            int pm25Value;//초미먼 값
//            int pm25Value24;//초미먼 24시간 값
//            int so2Grade;//이산화황 등급
//            double so2Value; // 이산화황 량
        }
    }
}