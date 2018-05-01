package edu.android.dustdrug;

import android.location.Address;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class DustDrugDAOImple {
    private AirQulity_API airQulity_api = new AirQulity_API();
    private static DustDrugDAOImple instance = null;
    public Data data = new Data();

    //    private MainActivity mainActivity ;
    private DustDrugDAOImple() {
    }

    public static DustDrugDAOImple getInstence() {
        if (instance == null) {
            instance = new DustDrugDAOImple();
        }
        return instance;
    }

    public void fuckTM(List<Address> addresses) {//구또는 동을 TM 으로 변환
        Log.i("s1","Thoroughfare"+addresses.get(0).getThoroughfare());
        ArrayList<AirQulity_API.GetAPIGsonTM.List> list;
        if (addresses.get(0).getThoroughfare()==null){
            list = airQulity_api.getFackTm(addresses.get(0).getSubLocality());
        }else{
            list = airQulity_api.getFackTm(addresses.get(0).getThoroughfare());
        }
        data.setLocality(addresses.get(0).getLocality());
        data.setSubLocality(addresses.get(0).getSubLocality());
        data.setThoroughfare(list.get(0).umdName);
        for (int i = 0 ; i<list.size();i++){
            if(list.get(i).getSidoName().equals(data.getLocality())){
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

    public void kimKwangSukInthespiritofforgetting(String stationNmae) {//측정소 명으로 데이터 가져오기
        ArrayList<AirQulity_API.GetAPIGsonMainData.List> list = airQulity_api.getDataclass(stationNmae);
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
    }


    public static class Data {

        private String locality; // 서울시
        private String subLocality; //구
        private String thoroughfare;//동
        private double tmX;
        private double tmY;
        private String stationName;//측정소명
        private ArrayList<DetailData> detailData = new ArrayList<>();//상세 클래스

        @Override
        public String toString() {
            return "Data{" +
                    "detailData=" + detailData +
                    '}';
        }

        public static class DetailData {
            private String dataTime;//시간
            private int coGrade;//일산화 등급
            private double coValue;//일산화 량
            private int khaiGrade;// 통합대기환경 지수
            private int khaiValue;//  통합대기환경 수치
            private String mangName;// 측정망
            private int no2Grade;// 이산화 질소 등급
            private double no2Value;// 이산화 질소량
            private int o3Grade;//오존 등급
            private double o3Value;//오존 량
            private int pm10Gradel; //미먼 등급
            private int pm10Grade1h; //밈먼등급 1시간 등급
            private int pm10Value; //미먼 측정치
            private int pm10Value24; // 미먼 24 시간 등급
            private int pm25Grade; // 초미먼 등급
            private int pm25Grade1h;//초미먼 1시간 등급
            private int pm25Value;//초미먼 값
            private int pm25Value24;//초미먼 24시간 값
            private int so2Grade;//이산화황 등급
            private double so2Value; // 이산화황 량


            public int getCoGrade() {
                return coGrade;
            }

            public void setCoGrade(int coGrade) {
                this.coGrade = coGrade;
            }

            public double getCoValue() {
                return coValue;
            }

            public void setCoValue(double coValue) {
                this.coValue = coValue;
            }

            public String getDataTime() {
                return dataTime;
            }

            public void setDataTime(String dataTime) {
                this.dataTime = dataTime;
            }

            public int getKhaiGrade() {
                return khaiGrade;
            }

            public void setKhaiGrade(int khaiGrade) {
                this.khaiGrade = khaiGrade;
            }

            public int getKhaiValue() {
                return khaiValue;
            }

            public void setKhaiValue(int khaiValue) {
                this.khaiValue = khaiValue;
            }

            public String getMangName() {
                return mangName;
            }

            public void setMangName(String mangName) {
                this.mangName = mangName;
            }

            public int getNo2Grade() {
                return no2Grade;
            }

            public void setNo2Grade(int no2Grade) {
                this.no2Grade = no2Grade;
            }

            public double getNo2Value() {
                return no2Value;
            }

            public void setNo2Value(double no2Value) {
                this.no2Value = no2Value;
            }

            public int getO3Grade() {
                return o3Grade;
            }

            public void setO3Grade(int o3Grade) {
                this.o3Grade = o3Grade;
            }

            public double getO3Value() {
                return o3Value;
            }

            public void setO3Value(double o3Value) {
                this.o3Value = o3Value;
            }

            public int getPm10Gradel() {
                return pm10Gradel;
            }

            public void setPm10Gradel(int pm10Gradel) {
                this.pm10Gradel = pm10Gradel;
            }

            public int getPm10Grade1h() {
                return pm10Grade1h;
            }

            public void setPm10Grade1h(int pm10Grade1h) {
                this.pm10Grade1h = pm10Grade1h;
            }

            public int getPm10Value() {
                return pm10Value;
            }

            public void setPm10Value(int pm10Value) {
                this.pm10Value = pm10Value;
            }

            public int getPm10Value24() {
                return pm10Value24;
            }

            public void setPm10Value24(int pm10Value24) {
                this.pm10Value24 = pm10Value24;
            }

            public int getPm25Grade() {
                return pm25Grade;
            }

            public void setPm25Grade(int pm25Grade) {
                this.pm25Grade = pm25Grade;
            }

            public int getPm25Grade1h() {
                return pm25Grade1h;
            }

            public void setPm25Grade1h(int pm25Grade1h) {
                this.pm25Grade1h = pm25Grade1h;
            }

            public int getPm25Value() {
                return pm25Value;
            }

            public void setPm25Value(int pm25Value) {
                this.pm25Value = pm25Value;
            }

            public int getPm25Value24() {
                return pm25Value24;
            }

            public void setPm25Value24(int pm25Value24) {
                this.pm25Value24 = pm25Value24;
            }

            public int getSo2Grade() {
                return so2Grade;
            }

            public void setSo2Grade(int so2Grade) {
                this.so2Grade = so2Grade;
            }

            public double getSo2Value() {
                return so2Value;
            }

            public void setSo2Value(double so2Value) {
                this.so2Value = so2Value;
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

    }
}





//package edu.android.dustdrug;
//
//import android.location.Address;
//import android.util.Log;
//import java.util.ArrayList;
//import java.util.List;
//
//public class DustDrugDAOImple {
//    private AirQulity_API airQulity_api = new AirQulity_API();
//    private static DustDrugDAOImple instance = null;
//    public Data data = new Data();
//
//    //    private MainActivity mainActivity ;
//    private DustDrugDAOImple() {
//    }
//
//    public static DustDrugDAOImple getInstence() {
//        if (instance == null) {
//            instance = new DustDrugDAOImple();
//        }
//        return instance;
//    }
//
//    public void fuckTM(List<Address> addresses) {//구또는 동을 TM 으로 변환
//        Log.i("s1","Thoroughfare"+addresses.get(0).getThoroughfare());
//        ArrayList<AirQulity_API.GetAPIGsonTM.List> list;
//        if (addresses.get(0).getThoroughfare()==null){
//            list = airQulity_api.getFackTm(addresses.get(0).getSubLocality());
//        }else{
//            list = airQulity_api.getFackTm(addresses.get(0).getThoroughfare());
//        }
//        data.setLocality(addresses.get(0).getLocality());
//        data.setSubLocality(addresses.get(0).getSubLocality());
//        data.setThoroughfare(list.get(0).umdName);
//        for (int i = 0 ; i<list.size();i++){
//        if(list.get(i).getSidoName().equals(data.getLocality())){
//            data.setTmX(list.get(i).tmX);
//            data.setTmY(list.get(i).tmY);
//            break;
//        }
//        }
//    }
//
//    public void getStationName(double tmx, double tmy) {//tm 좌표를 이용 하여 측정소명 획득
//        ArrayList<AirQulity_API.GetAPIGsonMeasuringStation.List> list = airQulity_api.getMeasuringStation(tmx, tmy);
//        data.setStationName(list.get(0).stationName);
//    }
//
//    public void kimKwangSukInthespiritofforgetting(String stationNmae) {//측정소 명으로 데이터 가져오기
//        ArrayList<AirQulity_API.GetAPIGsonMainData.List> list = airQulity_api.getDataclass(stationNmae);
//        for (int i = 0; i < list.size(); i++) {
//            Data.DetailData detailData1 = new Data.DetailData();
//            detailData1.setCoGrade(list.get(i).coGrade);
//
//            detailData1.setCoValue(list.get(i).coValue);
//            detailData1.setDataTime(list.get(i).dataTime);
//            detailData1.setKhaiGrade(list.get(i).khaiGrade);
//            detailData1.setKhaiValue(list.get(i).khaiValue);
//            detailData1.setMangName(list.get(i).mangName);
//            detailData1.setNo2Grade(list.get(i).no2Grade);
//            detailData1.setNo2Value(list.get(i).no2Value);
//            detailData1.setNo2Grade(list.get(i).no2Grade);
//            detailData1.setO3Grade(list.get(i).o3Grade);
//            detailData1.setO3Value(list.get(i).o3Value);
//            detailData1.setPm10Grade1h(list.get(i).pm10Grade1h);
//            detailData1.setPm10Gradel(list.get(i).pm10Gradel);
//            detailData1.setPm10Value(list.get(i).pm10Value);
//            detailData1.setPm10Value24(list.get(i).pm10Value24);
//            detailData1.setPm25Grade(list.get(i).pm25Grade);
//            detailData1.setPm25Grade1h(list.get(i).pm25Grade1h);
//            detailData1.setPm25Value(list.get(i).pm25Value);
//            detailData1.setPm25Value24(list.get(i).pm25Value24);
//            detailData1.setSo2Grade(list.get(i).so2Grade);
//            detailData1.setSo2Value(list.get(i).so2Value);
//            data.detailData.add(detailData1);
//        }
//    }
//
//
//    public static class Data {
//
//        private String locality; // 서울시
//        private String subLocality; //구
//        private String thoroughfare;//동
//        private double tmX;
//        private double tmY;
//        private String stationName;//측정소명
//        private ArrayList<DetailData> detailData = new ArrayList<>();//상세 클래스
//
//        @Override
//        public String toString() {
//            return "Data{" +
//                    "detailData=" + detailData +
//                    '}';
//        }
//
//        public static class DetailData {
//            private String dataTime;//시간
//            private int coGrade;//일산화 등급
//            private double coValue;//일산화 량
//            private int khaiGrade;// 통합대기환경 지수
//            private int khaiValue;//  통합대기환경 수치
//            private String mangName;// 측정망
//            private int no2Grade;// 이산화 질소 등급
//            private double no2Value;// 이산화 질소량
//            private int o3Grade;//오존 등급
//            private double o3Value;//오존 량
//            private int pm10Gradel; //미먼 등급
//            private int pm10Grade1h; //밈먼등급 1시간 등급
//            private int pm10Value; //미먼 측정치
//            private int pm10Value24; // 미먼 24 시간 등급
//            private int pm25Grade; // 초미먼 등급
//            private int pm25Grade1h;//초미먼 1시간 등급
//            private int pm25Value;//초미먼 값
//            private int pm25Value24;//초미먼 24시간 값
//            private int so2Grade;//이산화황 등급
//            private double so2Value; // 이산화황 량
//
//
//            public int getCoGrade() {
//                return coGrade;
//            }
//
//            public void setCoGrade(int coGrade) {
//                this.coGrade = coGrade;
//            }
//
//            public double getCoValue() {
//                return coValue;
//            }
//
//            public void setCoValue(double coValue) {
//                this.coValue = coValue;
//            }
//
//            public String getDataTime() {
//                return dataTime;
//            }
//
//            public void setDataTime(String dataTime) {
//                this.dataTime = dataTime;
//            }
//
//            public int getKhaiGrade() {
//                return khaiGrade;
//            }
//
//            public void setKhaiGrade(int khaiGrade) {
//                this.khaiGrade = khaiGrade;
//            }
//
//            public int getKhaiValue() {
//                return khaiValue;
//            }
//
//            public void setKhaiValue(int khaiValue) {
//                this.khaiValue = khaiValue;
//            }
//
//            public String getMangName() {
//                return mangName;
//            }
//
//            public void setMangName(String mangName) {
//                this.mangName = mangName;
//            }
//
//            public int getNo2Grade() {
//                return no2Grade;
//            }
//
//            public void setNo2Grade(int no2Grade) {
//                this.no2Grade = no2Grade;
//            }
//
//            public double getNo2Value() {
//                return no2Value;
//            }
//
//            public void setNo2Value(double no2Value) {
//                this.no2Value = no2Value;
//            }
//
//            public int getO3Grade() {
//                return o3Grade;
//            }
//
//            public void setO3Grade(int o3Grade) {
//                this.o3Grade = o3Grade;
//            }
//
//            public double getO3Value() {
//                return o3Value;
//            }
//
//            public void setO3Value(double o3Value) {
//                this.o3Value = o3Value;
//            }
//
//            public int getPm10Gradel() {
//                return pm10Gradel;
//            }
//
//            public void setPm10Gradel(int pm10Gradel) {
//                this.pm10Gradel = pm10Gradel;
//            }
//
//            public int getPm10Grade1h() {
//                return pm10Grade1h;
//            }
//
//            public void setPm10Grade1h(int pm10Grade1h) {
//                this.pm10Grade1h = pm10Grade1h;
//            }
//
//            public int getPm10Value() {
//                return pm10Value;
//            }
//
//            public void setPm10Value(int pm10Value) {
//                this.pm10Value = pm10Value;
//            }
//
//            public int getPm10Value24() {
//                return pm10Value24;
//            }
//
//            public void setPm10Value24(int pm10Value24) {
//                this.pm10Value24 = pm10Value24;
//            }
//
//            public int getPm25Grade() {
//                return pm25Grade;
//            }
//
//            public void setPm25Grade(int pm25Grade) {
//                this.pm25Grade = pm25Grade;
//            }
//
//            public int getPm25Grade1h() {
//                return pm25Grade1h;
//            }
//
//            public void setPm25Grade1h(int pm25Grade1h) {
//                this.pm25Grade1h = pm25Grade1h;
//            }
//
//            public int getPm25Value() {
//                return pm25Value;
//            }
//
//            public void setPm25Value(int pm25Value) {
//                this.pm25Value = pm25Value;
//            }
//
//            public int getPm25Value24() {
//                return pm25Value24;
//            }
//
//            public void setPm25Value24(int pm25Value24) {
//                this.pm25Value24 = pm25Value24;
//            }
//
//            public int getSo2Grade() {
//                return so2Grade;
//            }
//
//            public void setSo2Grade(int so2Grade) {
//                this.so2Grade = so2Grade;
//            }
//
//            public double getSo2Value() {
//                return so2Value;
//            }
//
//            public void setSo2Value(double so2Value) {
//                this.so2Value = so2Value;
//            }
//        }
//
//        public String getLocality() {
//            return locality;
//        }
//
//        public void setLocality(String locality) {
//            this.locality = locality;
//        }
//
//        public String getSubLocality() {
//            return subLocality;
//        }
//
//        public void setSubLocality(String subLocality) {
//            this.subLocality = subLocality;
//        }
//
//        public String getThoroughfare() {
//            return thoroughfare;
//        }
//
//        public void setThoroughfare(String thoroughfare) {
//            this.thoroughfare = thoroughfare;
//        }
//
//        public double getTmX() {
//            return tmX;
//        }
//
//        public void setTmX(double tmX) {
//            this.tmX = tmX;
//        }
//
//        public double getTmY() {
//            return tmY;
//        }
//
//        public void setTmY(double tmY) {
//            this.tmY = tmY;
//        }
//
//        public String getStationName() {
//            return stationName;
//        }
//
//        public void setStationName(String stationName) {
//            this.stationName = stationName;
//        }
//
//        public ArrayList<DetailData> getDetailData() {
//            return detailData;
//        }
//
//        public void setDetailData(ArrayList<DetailData> detailData) {
//            this.detailData = detailData;
//        }
//
//    }
//}
