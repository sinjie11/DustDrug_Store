package edu.android.dustdrug;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
//

public class GeoCoding extends MainFragment{
    List<Address> list = null;
    public static final String TAG = "edu.android";
    private static GeoCoding instance = null;
    public static Geocoder geocoder = null;
    public double d1 = latitude;
    public double d2 = longtitude;
    public String str;
    // 지오코딩(GeoCoding) : 주소,지명 => 위도,경도 좌표로 변환

    //Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());


    public static GeoCoding newInstance() {
        GeoCoding geoCoding = new GeoCoding();
        Log.i(TAG,"GeoCoding - GeoCoding newInstance()");
        return geoCoding;
    }
    public static GeoCoding getInstance() {
        if (instance == null) {
            Log.i(TAG, "GeoCoding - getInstance");
            instance = new GeoCoding();
        }
        return instance;
    }

    public GeoCoding() {
        Log.i(TAG,"GeoCoding - GeoCoding");
        d1 = latitude;
        d2 = longtitude;
        List<Address> list = null;
        //Geocoder geocoder = new Geocoder(getContext(), Locale.KOREA);
        // 위도,경도 입력 후 변환 버튼 클릭

//        try {
//            d1 = latitude;
//            d2 = longtitude;
//
//            list = geocoder.getFromLocation(
//                    d1, // 위도
//                    d2, // 경도
//                    10); // 얻어올 값의 개수
//            Log.i(TAG,"위도" + d1 + "경도" + d2);
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.e(TAG, "입출력 오류 - 서버에서 주소변환시 에러발생");
//        }

//        if (list != null) {
//            if (list.size() == 0) {
//                Log.i(TAG,"MainFragment - lineChart 생성");
//                txtGeo.setText("해당되는 주소 정보는 없습니다");
//            } else {
//                Log.i(TAG,"GeoCoding - 위도경도 변환 성공");
//                txtGeo.setText(list.get(0).toString());
////                String admin = list.get(0).getAdminArea();
////                String local = list.get(0).getLocality();
////                txtGeo.setText(admin);
////                txtGeo.setText(local);
//            }
//        }

    }
    public static void getGeocoder(Geocoder ingeocoder){
        geocoder = ingeocoder;
    }

    public void test(){
        try {
            //d1// = latitude;
            //d2// = longtitude;

            list = geocoder.getFromLocation(
                    d1, // 위도
                    d2, // 경도
                    10); // 얻어올 값의 개수
            Log.i(TAG,"GeoCoding - 정말로 되는거? 위도" + d1 + "    경도" + d2);
            //Log.i(TAG, "latitude : " + latitude + "\t" + "longtitude : " + longtitude);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "입출력 오류 - 서버에서 주소변환시 에러발생");
        }
        if (list != null) {
            if (list.size() == 0) {
                Log.i(TAG,"GeoCoding - lineChart 생성");
                //txtGeo.setText("해당되는 주소 정보는 없습니다");
            } else {
                Log.i(TAG,"GeoCoding - 위도경도 변환 성공");
                //txtGeo.setText(list.get(0).toString());

                String Locality = list.get(0).getLocality();
                String Thoroughfare = list.get(1).getThoroughfare();
                //String Thoroughfare = list.get(0).getThoroughfare();
                Log.i(TAG,"GeoCoding - Locality : " + Locality );//+ "\t Thoroughfare : " + Thoroughfare);
                str = Locality;
                str += Thoroughfare;
                Log.i(TAG, "GeoCoding - Str : " + str);
                //txtGeo.setText(Locality);// + Thoroughfare);
//                txtGeo.setText(local);
            }
        }
    }

    public void getlatitude(double latitude, double longtitude){
        Log.i(TAG, "GeoCoding - latitude : " + latitude + "\t" + "longtitude : " + longtitude);
        this.d1 = latitude;
        this.d2 = longtitude;
    }

    public String setText(){
        return str;
    }
}




//    {
//        List<Address> list = null;
//        String str = txtGeo.getText().toString();
//        try {
//            list = geocoder.getFromLocationName(
//                    str, // 지역 이름
//                    10); // 읽을 개수
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.e("test","입출력 오류 - 서버에서 주소변환시 에러발생");
//        }
//
//        if (list != null) {
//            if (list.size() == 0) {
//                txtGeo.setText("해당되는 주소 정보는 없습니다");
//            } else {
//                txtGeo.setText(list.get(0).toString());
//                //          list.get(0).getCountryName();  // 국가명
//                //          list.get(0).getLatitude();        // 위도
//                //          list.get(0).getLongitude();    // 경도
//            }
//        }
//    }




