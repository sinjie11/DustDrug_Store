package edu.android.dustdrug;

import android.graphics.Path;

public class OpenApi {
    private String addr;
    private double pm10Value; // 미세먼지 농도
    private double pm25Value; // 초미세먼지 농도
    private static OpenApi instance = null;

    public static OpenApi getInstance() {
        if(instance == null)
            instance = new OpenApi();
        return instance;
    }

    public OpenApi(){}

    public OpenApi(String addr, double pm10Value, double pm25Value) {
        this.addr = addr;
        this.pm10Value = pm10Value;
        this.pm25Value = pm25Value;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public double getPm10Value() {
        return pm10Value;
    }

    public void setPm10Value(double pm10Value) {
        this.pm10Value = pm10Value;
    }

    public double getPm25Value() {
        return pm25Value;
    }

    public void setPm25Value(double pm25Value) {
        this.pm25Value = pm25Value;
    }

    @Override
    public String toString() {
        return "OpenApi{" +
                "addr='" + addr + '\'' +
                ", pm10Value=" + pm10Value +
                ", pm25Value=" + pm25Value +
                '}';
    }
}
