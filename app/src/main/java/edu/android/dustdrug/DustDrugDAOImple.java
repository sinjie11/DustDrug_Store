package edu.android.dustdrug;

import android.location.Address;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DustDrugDAOImple {
    public static final String TAG = "edu.android";

    private AirQulity_API airQulity_api = new AirQulity_API();
    private static DustDrugDAOImple instance = null;
    public Data data;

    private DustDrugDAOImple() {
    }

    public static DustDrugDAOImple getInstence() {
        if (instance == null) {
            instance = new DustDrugDAOImple();
        }
        return instance;
    }

    public void changeAddressTmConvert(List<Address> addresses) {//구또는 동을 TM 으로 변환

        data = new Data();

        Log.i(TAG, "fullAddress ---> " + addresses.get(0));
        Log.i(TAG, "SidoName ---> " + addresses.get(0).getAdminArea());
        Log.i(TAG, "Thoroughfare ---> " + addresses.get(0).getThoroughfare());
        
        ArrayList<AirQulity_API.GetAPIGsonTM.List> list;
        if (addresses.get(0).getThoroughfare() == null) {
            list = airQulity_api.getChangeTm(addresses.get(0).getSubLocality());
            
        } else {

            list = airQulity_api.getChangeTm(addresses.get(0).getThoroughfare());
        }

        /**
         * 2020.03.29 추가
         * 시도 값이 없을 때 API에 있는 시도 정보로 세팅
         */
        if (addresses.get(0).getLocality() != null) {
            data.setLocality(addresses.get(0).getLocality());
        } else {
            data.setLocality(addresses.get(0).getAdminArea());
        }

        data.setSubLocality(addresses.get(0).getSubLocality());

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getSidoName().equals(data.getLocality())) {
                data.setThoroughfare(list.get(i).umdName);
                data.setTmX(list.get(i).tmX);
                data.setTmY(list.get(i).tmY);
                break;
            }
        }
    }

    public void getStationName(double tmx, double tmy) {//tm 좌표를 이용 하여 측정소명 획득
        ArrayList<AirQulity_API.GetAPIGsonMeasuringStation.List> list = airQulity_api.getMeasuringStation(tmx, tmy);
        data.setStationName(list.get(0).stationName);
    }

    public void measurementCallGetData(String stationName) {//측정소 명으로 데이터 가져오기

        ArrayList<AirQulity_API.GetAPIGsonMainData.List> list = airQulity_api.getDataclass(stationName);

        for (int i = 0; i < list.size(); i++) {

            Data.DetailData detailData1 = new Data.DetailData();
            detailData1.setCoGrade(list.get(i).coGrade);
            detailData1.setCoValue(list.get(i).coValue);
            detailData1.setDataTime(list.get(i).dataTime);
            detailData1.setKhaiGrade(list.get(i).khaiGrade);
            detailData1.setKhaiValue(list.get(i).khaiValue);
            detailData1.setMangName(list.get(i).mangName);
            detailData1.setNo2Grade(list.get(i).no2Grade);
            detailData1.setNo2Value(list.get(i).no2Value);
            detailData1.setNo2Grade(list.get(i).no2Grade);
            detailData1.setO3Grade(list.get(i).o3Grade);
            detailData1.setO3Value(list.get(i).o3Value);
            detailData1.setPm10Grade1h(list.get(i).pm10Grade1h);
            detailData1.setPm10Gradel(list.get(i).pm10Gradel);
            detailData1.setPm10Value(list.get(i).pm10Value);
            detailData1.setPm10Value24(list.get(i).pm10Value24);
            detailData1.setPm25Grade(list.get(i).pm25Grade);
            detailData1.setPm25Grade1h(list.get(i).pm25Grade1h);
            detailData1.setPm25Value(list.get(i).pm25Value);
            detailData1.setPm25Value24(list.get(i).pm25Value24);
            detailData1.setSo2Grade(list.get(i).so2Grade);
            detailData1.setSo2Value(list.get(i).so2Value);

            data.detailData.add(detailData1);
        }

        Log.i(TAG, data.toString());
    }


    public static class Data {

        private String locality;     // 시
        private String subLocality;  // 구
        private String thoroughfare; // 동
        private double tmX;
        private double tmY;
        private String stationName; // 측정소명
        private ArrayList<DetailData> detailData = new ArrayList<>();//상세 클래스

        public static class DetailData {

            private String dataTime;    // 날짜 및 시간
            private String coGrade;     // 일산화 등급
            private String coValue;     // 일산화 량
            private String khaiGrade;   // 통합대기환경 등급
            private String khaiValue;   // 통합대기환경 수치
            private String mangName;    // 측정망
            private String no2Grade;    // 이산화 질소 등급
            private String no2Value;    // 이산화 질소량
            private String o3Grade;     // 오존 등급
            private String o3Value;     // 오존 량
            private String pm10Gradel;  // 미세먼지 등급
            private String pm10Grade1h; // 미세먼지 1시간 등급
            private String pm10Value;   // 미세먼지 값
            private String pm10Value24; // 미세먼지 24시간 등급
            private String pm25Grade;   // 초미세먼지 등급
            private String pm25Grade1h; // 초미세먼지 1시간 등급
            private String pm25Value;   // 초미세먼지 값
            private String pm25Value24; // 초미세먼지 24시간 값
            private String so2Grade;    // 이산화황 등급
            private String so2Value;    // 이산화황 양

            public String getDataTime() {
                return dataTime;
            }

            public void setDataTime(String dataTime) {

                dataTime = dataTime.replace("-1", "-");
                this.dataTime = dataTime;
            }

            public String getCoGrade() {
                return coGrade;
            }

            public void setCoGrade(String coGrade) {
                this.coGrade = coGrade;
            }

            public String getCoValue() {
                return coValue;
            }

            public void setCoValue(String coValue) {
                this.coValue = coValue;
            }

            public String getKhaiGrade() {
                return khaiGrade;
            }

            public void setKhaiGrade(String khaiGrade) {
                this.khaiGrade = khaiGrade;
            }

            public String getKhaiValue() {
                return khaiValue;
            }

            public void setKhaiValue(String khaiValue) {
                this.khaiValue = khaiValue;
            }

            public String getMangName() {
                return mangName;
            }

            public void setMangName(String mangName) {
                this.mangName = mangName;
            }

            public String getNo2Grade() {
                return no2Grade;
            }

            public void setNo2Grade(String no2Grade) {
                this.no2Grade = no2Grade;
            }

            public String getNo2Value() {
                return no2Value;
            }

            public void setNo2Value(String no2Value) {
                this.no2Value = no2Value;
            }

            public String getO3Grade() {
                return o3Grade;
            }

            public void setO3Grade(String o3Grade) {
                this.o3Grade = o3Grade;
            }

            public String getO3Value() {
                return o3Value;
            }

            public void setO3Value(String o3Value) {
                this.o3Value = o3Value;
            }

            public String getPm10Gradel() {
                return pm10Gradel;
            }

            public void setPm10Gradel(String pm10Gradel) {
                this.pm10Gradel = pm10Gradel;
            }

            public String getPm10Grade1h() {
                return pm10Grade1h;
            }

            public void setPm10Grade1h(String pm10Grade1h) {
                this.pm10Grade1h = pm10Grade1h;
            }

            public String getPm10Value() {
                return pm10Value;
            }

            public void setPm10Value(String pm10Value) {
                this.pm10Value = pm10Value;
            }

            public String getPm10Value24() {
                return pm10Value24;
            }

            public void setPm10Value24(String pm10Value24) {
                this.pm10Value24 = pm10Value24;
            }

            public String getPm25Grade() {
                return pm25Grade;
            }

            public void setPm25Grade(String pm25Grade) {
                this.pm25Grade = pm25Grade;
            }

            public String getPm25Grade1h() {
                return pm25Grade1h;
            }

            public void setPm25Grade1h(String pm25Grade1h) {
                this.pm25Grade1h = pm25Grade1h;
            }

            public String getPm25Value() {
                return pm25Value;
            }

            public void setPm25Value(String pm25Value) {
                this.pm25Value = pm25Value;
            }

            public String getPm25Value24() {
                return pm25Value24;
            }

            public void setPm25Value24(String pm25Value24) {
                this.pm25Value24 = pm25Value24;
            }

            public String getSo2Grade() {
                return so2Grade;
            }

            public void setSo2Grade(String so2Grade) {
                this.so2Grade = so2Grade;
            }

            public String getSo2Value() {
                return so2Value;
            }

            public void setSo2Value(String so2Value) {
                this.so2Value = so2Value;
            }

            @Override
            public String toString() {
                return "DetailData{" +
                        "dataTime='" + dataTime + '\'' +
                        ", coGrade=" + coGrade +
                        ", coValue=" + coValue +
                        ", khaiGrade=" + khaiGrade +
                        ", khaiValue=" + khaiValue +
                        ", mangName='" + mangName + '\'' +
                        ", no2Grade=" + no2Grade +
                        ", no2Value=" + no2Value +
                        ", o3Grade=" + o3Grade +
                        ", o3Value=" + o3Value +
                        ", pm10Gradel=" + pm10Gradel +
                        ", pm10Grade1h=" + pm10Grade1h +
                        ", pm10Value=" + pm10Value +
                        ", pm10Value24=" + pm10Value24 +
                        ", pm25Grade=" + pm25Grade +
                        ", pm25Grade1h=" + pm25Grade1h +
                        ", pm25Value=" + pm25Value +
                        ", pm25Value24=" + pm25Value24 +
                        ", so2Grade=" + so2Grade +
                        ", so2Value=" + so2Value +
                        '}';
            }
        }

        public String getLocality() {
            return locality;
        }

        public void setLocality(String locality) {
            this.locality = locality;
        }

        public String getSubLocality() {
            return subLocality;
        }

        public void setSubLocality(String subLocality) {
            this.subLocality = subLocality;
        }

        public String getThoroughfare() {
            return thoroughfare;
        }

        public void setThoroughfare(String thoroughfare) {
            this.thoroughfare = thoroughfare;
        }

        public double getTmX() {
            return tmX;
        }

        public void setTmX(double tmX) {
            this.tmX = tmX;
        }

        public double getTmY() {
            return tmY;
        }

        public void setTmY(double tmY) {
            this.tmY = tmY;
        }

        public String getStationName() {
            return stationName;
        }

        public void setStationName(String stationName) {
            this.stationName = stationName;
        }

        public ArrayList<DetailData> getDetailData() {
            return detailData;
        }

        public void setDetailData(ArrayList<DetailData> detailData) {
            this.detailData = detailData;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "locality='" + locality + '\'' +
                    ", subLocality='" + subLocality + '\'' +
                    ", thoroughfare='" + thoroughfare + '\'' +
                    ", tmX=" + tmX +
                    ", tmY=" + tmY +
                    ", stationName='" + stationName + '\'' +
                    ", detailData=" + detailData +
                    '}';
        }
    }
}


