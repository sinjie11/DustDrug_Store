package edu.android.dustdrug;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class GeoCoding {
    public static final String TAG = "edu.android";

    private static List<Address> list = null;
    private static GeoCoding instance = null;
    public static Geocoder geocoder = null;
    public String str;

    // 지오코딩(GeoCoding) : 주소,지명 => 위도,경도 좌표로 변환


    public static GeoCoding newInstance() {
        GeoCoding geoCoding = new GeoCoding();
        Log.i(TAG, "GeoCoding - GeoCoding newInstance()");
        return geoCoding;
    }

    public static GeoCoding getInstance() {
        if (instance == null) {
            Log.i(TAG, "GeoCoding - getInstance");
            instance = new GeoCoding();
        }
        return instance;
    }

    public static void getGeocoder(Geocoder ingeocoder) {
        geocoder = ingeocoder;
    }

    public void getAddress(double d1, double d2) {

    }

    public static List<Address> getlatitude(double latitude, double longtitude, Context context) { //위도 경도 입력시 주소값 리턴

        double d1 = latitude;
        double d2 = longtitude;

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        try {
            Log.i(TAG, latitude + " " + longtitude);
            list = geocoder.getFromLocation(latitude, longtitude, 10);

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (list.size() < 1) {
            Toast.makeText(context, "위도, 경도 값을 받아오지 못했습니다.", Toast.LENGTH_SHORT).show();
        }

        return list;

    } // 위도 경도 입력시 주소값 리턴

    public String setText() {
        return str;
    }
}
