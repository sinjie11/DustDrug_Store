package edu.android.dustdrug;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {
    public static final String TAG = "edu.android";
    private static final int REQ_CODE_PERMISSION = 1;
    private DustDrugDAOImple dustDrugDAOImple;
    public double longtitude;
    public double latitude;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SearchFragment searchFragment;
    private LocationManager locationManager;
    private Location location;
    private LineChart lineChart; // 그래프(jar 파일 사용)
    private List<Address> list;
    public TextView textLocation, textShowValue, textValueGrade, textTime;
    public ImageButton btnBlueTooth, btnSearch;
    public Button btnAddress;
    public DustDrugDAOImple.Data.DetailData detailDatakk = new DustDrugDAOImple.Data.DetailData();


    public MainFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        dustDrugDAOImple = DustDrugDAOImple.getInstence();
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        lineChart = view.findViewById(R.id.chartValueEveryHour);
        lineChart.setDescription("");
        showLineChart(); // Line Graph를 보여주는 메소드를 불러옵니다.
        Log.i(TAG,"MainFragment - lineChart 생성");

        textLocation = view.findViewById(R.id.textLocation);
        textShowValue = view.findViewById(R.id.textShowValue);
        btnAddress = view.findViewById(R.id.btnAddress);
        btnBlueTooth = view.findViewById(R.id.btnBlueTooth);
        btnSearch = view.findViewById(R.id.btnSearch);
        textValueGrade = view.findViewById(R.id.textValueGrade);
        textTime = view.findViewById(R.id.textTime);


        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.mainFragment);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() { // 새로고침 시 권한부여 밑 좌표 받아오기
                //TODO
                showLocationInfo(); // 위도경도 불러오기
                startLocationService();
                showLineChart(); // 새로 고침시에도 LineChart 메소드 다시 불러옴
                getAddress(); // 좌표 주소로 변환 시 구 동
                if (list.size() > 0) {
                    textLocation.setText(list.get(0).getLocality()); // 시,도 정보
                    textLocation.append(" ");
                    textLocation.append(list.get(0).getSubLocality()); // 구,군 정보
                    textValueGrade.append(detailDatakk.getPm10Gradel() + " ");
                    Log.i(TAG, "Grade = " + detailDatakk.getPm10Gradel());
                    textTime.append(detailDatakk.getDataTime() + " ");
                    Log.i(TAG, "DATE = " + detailDatakk.getDataTime());
                    textShowValue.append(detailDatakk.getPm10Value() + " ug/m3");
                    Log.i(TAG, "DAO = " + "" + detailDatakk.getPm10Value());
                    SexyAss sexyAss = new SexyAss();
                    sexyAss.execute();

                }else {
                    Toast.makeText(getContext(), "위도와 경도가 준비되지 않음", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                searchFragment = new SearchFragment();
                transaction.replace(R.id.fragment_container, searchFragment);
                transaction.commit();
                Log.i(TAG, "search fragment call");
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public static MainFragment newInstance() {
        MainFragment mainFragment = new MainFragment();
        return mainFragment;
    }



    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.i(TAG, "MainFragment - onLocationChanged, location : " + location);
            longtitude = location.getLongitude();
            latitude = location.getLatitude();
            Log.i(TAG,"MainFragment - onLocationChanged");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    String[] permissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "MainFragment - onRequestPermissionsResult start");
        if (requestCode == REQ_CODE_PERMISSION) {
            if (grantResults.length == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                return;
            } else {
                Toast.makeText(getContext(), "Make the authorization get allowed", Toast.LENGTH_SHORT).show();
            }
        }
        Log.i(TAG, "MainFragment - onRequestPermissionsResult end");

    }

    private boolean hasPermissions(String[] permissions) {
        boolean result = true;
        for (String p : permissions) {
            if (ActivityCompat.checkSelfPermission(getContext(), p) != PackageManager.PERMISSION_GRANTED) {
                result = false;
                break;
            }
        }
        return result;
    }
    //gps
    public void startLocationService() { // GPS 권한 채크여부
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60*1000, 0, locationListener);
    }

    public void showLocationInfo() { // 권한 부여 및 위도, 경도 받아오는 메소드

        try {
            if (hasPermissions(permissions)) {

                startLocationService();
                longtitude = location.getLongitude();
                latitude = location.getLatitude();
//                GeoCoding geoCoding = GeoCoding.getInstance();
//                geoCoding.getlatitude(latitude,longtitude,getContext());
                textLocation.setText("경도 : " + longtitude + "\n" + "위도 : " + latitude);
                Log.i(TAG,"MainFragment - showLocationInfo");

                swipeRefreshLayout.setRefreshing(false);
            } else {
                if (ActivityCompat
                        .shouldShowRequestPermissionRationale(getActivity(), permissions[0])) {
                    Toast.makeText(getContext(), "아래로 끌어 새로고침이 필요합니다.", Toast.LENGTH_LONG).show();
                } else if (ActivityCompat
                        .shouldShowRequestPermissionRationale(getActivity(), permissions[1])) {
                    Toast.makeText(getContext(), "GPS가 안되서 근접한 거리라도...", Toast.LENGTH_LONG).show();
                }
                swipeRefreshLayout.setRefreshing(false);
                ActivityCompat.requestPermissions(getActivity(), permissions, REQ_CODE_PERMISSION);
            }
        } catch (NullPointerException e) {
            e.getMessage();
            Toast.makeText(getContext(), "위치정보를 받아오지 못했습니다.", Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
        }

    }

        public void showLineChart() {
        int max = 0;

        String[] xAxis = new String[]
                {"0시", "1시", "2시", "3시", "4시",
                        "5시", "6시", "7시", "8시", "9시",
                        "10시", "11시", "12시", "13시", "14시",
                        "15시", "16시", "17시", "18시", "19시",
                        "20시", "21시", "22시", "23시"};

        ArrayList<Entry> dataset1 = new ArrayList<Entry>();
//        dataset1.add(new Entry(40f, 0));
//        dataset1.add(new Entry(80f, 1));
//        dataset1.add(new Entry(60f, 2));
//        dataset1.add(new Entry(20f, 3));
//        dataset1.add(new Entry(180f, 4));
//        dataset1.add(new Entry(90f, 5));
//        dataset1.add(new Entry(160f, 6));
//        dataset1.add(new Entry(50f, 7));
//        dataset1.add(new Entry(30f, 8));
//        dataset1.add(new Entry(70f, 10));
//        dataset1.add(new Entry(90f, 11));
//        dataset1.add(new Entry(40f, 12));
//        dataset1.add(new Entry(80f, 13));
//        dataset1.add(new Entry(60f, 14));
//        dataset1.add(new Entry(20f, 15));
//        dataset1.add(new Entry(180f, 16));
//        dataset1.add(new Entry(90f, 17));
//        dataset1.add(new Entry(160f, 18));
//        dataset1.add(new Entry(50f, 19));
//        dataset1.add(new Entry(30f, 20));
//        dataset1.add(new Entry(70f, 21));
//        dataset1.add(new Entry(90f, 22));
//        dataset1.add(new Entry(70f, 23));

        for (int i = 0; i < xAxis.length; i++) {
            dataset1.add(new Entry((i + 1) * 10f, i));
        }

        ArrayList<Entry> dataset2 = new ArrayList<Entry>();
        dataset2.add(new Entry(165f, 0));
        dataset2.add(new Entry(55f, 1));
        dataset2.add(new Entry(35f, 2));
        dataset2.add(new Entry(75f, 3));
        dataset2.add(new Entry(95f, 4));
        dataset2.add(new Entry(45f, 5));
        dataset2.add(new Entry(85f, 6));
        dataset2.add(new Entry(65f, 7));
        dataset2.add(new Entry(25f, 8));
        dataset2.add(new Entry(75f, 9));
        dataset2.add(new Entry(95f, 10));
        dataset2.add(new Entry(45f, 11));
        dataset2.add(new Entry(85f, 12));
        dataset2.add(new Entry(65f, 13));
        dataset2.add(new Entry(25f, 14));
        dataset2.add(new Entry(185f, 15));
        dataset2.add(new Entry(95f, 16));
        dataset2.add(new Entry(165f, 17));
        dataset2.add(new Entry(55f, 18));
        dataset2.add(new Entry(35f, 19));
        dataset2.add(new Entry(75f, 20));
        dataset2.add(new Entry(95f, 21));
        dataset2.add(new Entry(75f, 22));
        dataset2.add(new Entry(75f, 23));

        ArrayList<ILineDataSet> lines = new ArrayList<ILineDataSet>();

        LineDataSet lineDataSet1 = new LineDataSet(dataset1, "미세먼지");
        lineDataSet1.setColor(Color.parseColor("#cb1ad6"));
        lineDataSet1.setCircleColor(Color.parseColor("#cb1ad6"));
        lineDataSet1.setDrawCubic(true);
        lines.add(lineDataSet1);
        LineDataSet lineDataSet2 = new LineDataSet(dataset2, "초미세먼지");
        lineDataSet2.setColor(Color.parseColor("#0deaf0"));
        lineDataSet2.setCircleColor(Color.parseColor("#0deaf0"));
        lineDataSet2.setDrawCubic(true);
        lines.add(lineDataSet2);

        lineChart.setData(new LineData(xAxis, lines));
        lineChart.animateY(1500);
        lineChart.setScaleEnabled(false);


    }


    //pgs 위도 경도 값 불러오기 끝

    public void getAddress(){ // 주소값 불러오기
        list = GeoCoding.getlatitude( latitude, longtitude,getContext());


    }
    class SexyAss extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] objects) { // 인터넷 사용을 위한 쓰래드
            if(list.get(0).getThoroughfare()==null)  { // 이름이 없을 시 "구" 로 검색
                dustDrugDAOImple.fuckTM(list);
                dustDrugDAOImple.getStationName(dustDrugDAOImple.data.getTmX(),dustDrugDAOImple.data.getTmY());
                dustDrugDAOImple.kimKwangSukInthespiritofforgetting(dustDrugDAOImple.data.getStationName());
            }else { // "동" 으로 검색
                dustDrugDAOImple.fuckTM(list);
                dustDrugDAOImple.getStationName(dustDrugDAOImple.data.getTmX(),dustDrugDAOImple.data.getTmY());
                dustDrugDAOImple.kimKwangSukInthespiritofforgetting(dustDrugDAOImple.data.getStationName());
            }

            return null;
        }
    }

}























//package edu.android.dustdrug;
//
//import android.Manifest;
//import android.content.Context;
//import android.content.pm.PackageManager;
//import android.graphics.Color;
//import android.location.Address;
//import android.location.Geocoder;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.os.AsyncTask;
//import android.os.Build;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentTransaction;
//import android.support.v4.widget.SwipeRefreshLayout;
//import android.util.EventLogTags;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageButton;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.github.mikephil.charting.charts.Chart;
//import com.github.mikephil.charting.charts.LineChart;
//import com.github.mikephil.charting.data.Entry;
//import com.github.mikephil.charting.data.LineData;
//import com.github.mikephil.charting.data.LineDataSet;
//import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
//
//import java.util.ArrayList;
//
//
///**
// * A simple {@link Fragment} subclass.
// */
//public class MainFragment extends Fragment {
//    public static final String TAG = "MainFragment";
//    private static final int REQ_CODE_PERMISSION = 1;
//
//    public double longtitude;
//    public double latitude;
//    private SwipeRefreshLayout swipeRefreshLayout;
//    private LocationManager locationManager;
//    public Location location;
//    public LineChart lineChart; // 그래프(jar 파일 사용)
//    public TextView textView;
//    public ImageButton imageButton;
//    private SearchFragment searchFragment;
//
//    public MainFragment() {
//        // Required empty public constructor
//    }
//
//    public static MainFragment newInstance() {
//        MainFragment mainFragment = new MainFragment();
//        return mainFragment;
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//
//        View view = inflater.inflate(R.layout.fragment_main, container, false);
//        lineChart = view.findViewById(R.id.chartValueEveryHour);
//
//        showLineChart(); // Line Graph를 보여주는 메소드를 불러옵니다.
//
//        view.setFocusableInTouchMode(true);
//        view.requestFocus();
//        lineChart.setDescription("");
//
//        textView = view.findViewById(R.id.textLocation);
//        imageButton = view.findViewById(R.id.imageButton);
//
//        textView.setText("Location");
//
////        if(hasPermissions(permissions)) { // 위치가 꺼져있을 경우 앱을 실행시키자마자 바로 위치 권한 수락여부 다이얼로그를 띄우게 함
////            showLocationInfo();
////        } else {
////            if (ActivityCompat
////                    .shouldShowRequestPermissionRationale(getActivity(), permissions[0])) {
////                Toast.makeText(getContext(), "아래로 끌어 새로고침이 필요합니다.", Toast.LENGTH_LONG).show();
////            } else if (ActivityCompat
////                    .shouldShowRequestPermissionRationale(getActivity(), permissions[1])) {
////                Toast.makeText(getContext(), "GPS가 안되서 근접한 거리라도...", Toast.LENGTH_LONG).show();
////            }
////            ActivityCompat.requestPermissions(getActivity(), permissions, REQ_CODE_PERMISSION);
////        }
//
//        swipeRefreshLayout = view.findViewById(R.id.mainFragment);
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                try {
//                    if (hasPermissions(permissions)) {
//                        showLocationInfo();
//                        swipeRefreshLayout.setRefreshing(false);
//                    } else {
//                        if (ActivityCompat
//                                .shouldShowRequestPermissionRationale(getActivity(), permissions[0])) {
//                            Toast.makeText(getContext(), "아래로 끌어 새로고침이 필요합니다.", Toast.LENGTH_LONG).show();
//                        } else if (ActivityCompat
//                                .shouldShowRequestPermissionRationale(getActivity(), permissions[1])) {
//                            Toast.makeText(getContext(), "GPS가 안되서 근접한 거리라도...", Toast.LENGTH_LONG).show();
//                        }
//                        swipeRefreshLayout.setRefreshing(false);
//                        ActivityCompat.requestPermissions(getActivity(), permissions, REQ_CODE_PERMISSION);
//                    }
//                } catch (NullPointerException e) {
//                    e.getMessage();
//                    Toast.makeText(getContext(), "위치정보를 받아오지 못했습니다.", Toast.LENGTH_SHORT).show();
//                    swipeRefreshLayout.setRefreshing(false);
//                }
//            }
//        });
//        startLocationService();
//        showLineChart(); // 새로고침을 할 때에도 해당 메소드를 호출해 Line Graph를 다시 보여줍니다.
//
//        imageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FragmentManager manager = getActivity().getSupportFragmentManager();
//                FragmentTransaction transaction = manager.beginTransaction();
//                searchFragment = new SearchFragment();
//                transaction.replace(R.id.fragment_container, searchFragment);
//                transaction.commit();
//                Log.i(TAG, "search fragment call");
//            }
//        });
//
//        return view;
//    }
//
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//    }
//
//    public void startLocationService() {
//        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
//
//        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
//    }
//
//    private LocationListener locationListener = new LocationListener() {
//        @Override
//        public void onLocationChanged(Location location) {
//            Log.i(TAG, "onLocationChanged, location : " + location);
//            longtitude = location.getLongitude();
//            latitude = location.getLatitude();
//            //TODO : 저장;
//        }
//
//        @Override
//        public void onStatusChanged(String provider, int status, Bundle extras) {
//
//        }
//
//        @Override
//        public void onProviderEnabled(String provider) {
//
//        }
//
//        @Override
//        public void onProviderDisabled(String provider) {
//
//        }
//    };
//
//    String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
//            Manifest.permission.ACCESS_COARSE_LOCATION};
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        Log.i(TAG, "onRequestPermissionsResult start");
//        if (requestCode == REQ_CODE_PERMISSION) {
//            if (grantResults.length == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
//                return;
//            } else {
//                Toast.makeText(getContext(), "Make the authorization get allowed", Toast.LENGTH_SHORT).show();
//            }
//        }
//        Log.i(TAG, "onRequestPermissionsResult end");
//
//    }
//
//    private boolean hasPermissions(String[] permissions) {
//        boolean result = true;
//        for (String p : permissions) {
//            if (ActivityCompat.checkSelfPermission(getContext(), p) != PackageManager.PERMISSION_GRANTED) {
//                result = false;
//                break;
//            }
//        }
//        return result;
//    }
//
//    public void showLocationInfo() {
//        startLocationService();
//        longtitude = location.getLongitude();
//        latitude = location.getLatitude();
//        textView.setText("경도 : " + longtitude + "\n" + "위도 : " + latitude);
//    }
//
//    public void showLineChart() {
//        int max = 0;
//
//        String[] xAxis = new String[]
//                {"0시", "1시", "2시", "3시", "4시",
//                        "5시", "6시", "7시", "8시", "9시",
//                        "10시", "11시", "12시", "13시", "14시",
//                        "15시", "16시", "17시", "18시", "19시",
//                        "20시", "21시", "22시", "23시"};
//
//        ArrayList<Entry> dataset1 = new ArrayList<Entry>();
////        dataset1.add(new Entry(40f, 0));
////        dataset1.add(new Entry(80f, 1));
////        dataset1.add(new Entry(60f, 2));
////        dataset1.add(new Entry(20f, 3));
////        dataset1.add(new Entry(180f, 4));
////        dataset1.add(new Entry(90f, 5));
////        dataset1.add(new Entry(160f, 6));
////        dataset1.add(new Entry(50f, 7));
////        dataset1.add(new Entry(30f, 8));
////        dataset1.add(new Entry(70f, 10));
////        dataset1.add(new Entry(90f, 11));
////        dataset1.add(new Entry(40f, 12));
////        dataset1.add(new Entry(80f, 13));
////        dataset1.add(new Entry(60f, 14));
////        dataset1.add(new Entry(20f, 15));
////        dataset1.add(new Entry(180f, 16));
////        dataset1.add(new Entry(90f, 17));
////        dataset1.add(new Entry(160f, 18));
////        dataset1.add(new Entry(50f, 19));
////        dataset1.add(new Entry(30f, 20));
////        dataset1.add(new Entry(70f, 21));
////        dataset1.add(new Entry(90f, 22));
////        dataset1.add(new Entry(70f, 23));
//
//        for (int i = 0; i < xAxis.length; i++) {
//            dataset1.add(new Entry((i + 1) * 10f, i));
//            // TODO : 여기까지 하다 말았습니다.
//        }
//
//        ArrayList<Entry> dataset2 = new ArrayList<Entry>();
//        dataset2.add(new Entry(165f, 0));
//        dataset2.add(new Entry(55f, 1));
//        dataset2.add(new Entry(35f, 2));
//        dataset2.add(new Entry(75f, 3));
//        dataset2.add(new Entry(95f, 4));
//        dataset2.add(new Entry(45f, 5));
//        dataset2.add(new Entry(85f, 6));
//        dataset2.add(new Entry(65f, 7));
//        dataset2.add(new Entry(25f, 8));
//        dataset2.add(new Entry(75f, 9));
//        dataset2.add(new Entry(95f, 10));
//        dataset2.add(new Entry(45f, 11));
//        dataset2.add(new Entry(85f, 12));
//        dataset2.add(new Entry(65f, 13));
//        dataset2.add(new Entry(25f, 14));
//        dataset2.add(new Entry(185f, 15));
//        dataset2.add(new Entry(95f, 16));
//        dataset2.add(new Entry(165f, 17));
//        dataset2.add(new Entry(55f, 18));
//        dataset2.add(new Entry(35f, 19));
//        dataset2.add(new Entry(75f, 20));
//        dataset2.add(new Entry(95f, 21));
//        dataset2.add(new Entry(75f, 22));
//        dataset2.add(new Entry(75f, 23));
//
//        ArrayList<ILineDataSet> lines = new ArrayList<ILineDataSet>();
//
//        LineDataSet lineDataSet1 = new LineDataSet(dataset1, "미세먼지");
//        lineDataSet1.setColor(Color.parseColor("#cb1ad6"));
//        lineDataSet1.setCircleColor(Color.parseColor("#cb1ad6"));
//        lineDataSet1.setDrawCubic(true);
//        lines.add(lineDataSet1);
//        LineDataSet lineDataSet2 = new LineDataSet(dataset2, "초미세먼지");
//        lineDataSet2.setColor(Color.parseColor("#0deaf0"));
//        lineDataSet2.setCircleColor(Color.parseColor("#0deaf0"));
//        lineDataSet2.setDrawCubic(true);
//        lines.add(lineDataSet2);
//
//        lineChart.setData(new LineData(xAxis, lines));
//        lineChart.animateY(1500);
//        lineChart.setScaleEnabled(false);
//
//
//    }
//
//}