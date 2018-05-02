package edu.android.dustdrug;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 */

public class MainFragment extends Fragment {
    public static final String TAG = "edu.android";

    /**
     * # 참고 사항 #
     * <p>
     * - 실시간 정보 : 10분(매 시간 시간자료 갱신은 20분 전후로 반영됨)
     * <p>
     * - 대기질 예보 정보 : 매 시간 22분, 57분
     * <p>
     * - Grade 값 →   좋음    : 1   ( pm10 => 0 ~ 30 , pm2.5 => 0 ~ 15 )
     * 보통   : 2   ( pm10 => 31 ~ 80 , pm2.5 => 16 ~ 50 )
     * 나쁨   : 3   ( pm10 => 81 ~ 150 , pm2.5 => 51 ~ 100 )
     * 매우나쁨 : 4   ( pm10 => 151 ~ , pm2.5 => 101 ~ )
     * <p>
     * ※ JSON 방식 호출 방법 : URL 제일 뒷 부분에 다음 파라미터(&_returnType=json)를 추가하여 호출
     */

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

    public TextView textLocation, textShowValue, textValueGrade, textTime, textShowValuePm25, textShowGraphDate;
    public ImageButton btnBlueTooth, btnSearch;

    public int pm10value;
    public int pm10Grade;

    public int calendar;
    public int year;
    public int month;
    public int date;
    public int[] list_pm10value = new int[24];


    private static final int REQUEST_CONNECT_DEVICE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    private Handler mHandler = null;
    private BluetoothAdapter btAdapter = null;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;

    private int mState;

    // 상태를 나타내는 상태 변수
    private static final int STATE_NONE = 0; // we're doing nothing
    private static final int STATE_LISTEN = 1; // now listening for incoming
    // connections
    private static final int STATE_CONNECTING = 2; // now initiating an outgoing
    // connection
    private static final int STATE_CONNECTED = 3; // now connected to a remote device

    // RFCOMM Protocol
    private static final UUID MY_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");

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
        lineChart.setDescription(" ");
        showLineChart(); // Line Graph를 보여주는 메소드를 불러옵니다.

        Log.i(TAG, "MainFragment - lineChart 생성");

        textLocation = view.findViewById(R.id.textLocation);
        textShowValue = view.findViewById(R.id.textShowValue);
        btnBlueTooth = view.findViewById(R.id.btnBlueTooth);
        btnSearch = view.findViewById(R.id.btnSearch);
        textValueGrade = view.findViewById(R.id.textValueGrade);
        textTime = view.findViewById(R.id.textTime);
        textShowValuePm25 = view.findViewById(R.id.textShowValuePm25);


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

                    SexyAss sexyAss = new SexyAss();
                    sexyAss.execute();

                } else {
                    Toast.makeText(getContext(), "위도와 경도가 준비되지 않음", Toast.LENGTH_SHORT).show();
                }
            }
        });
        startLocationService();
        showLineChart(); // 새로 고침시에도 LineChart 메소드 다시 불러옴

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

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        btnBlueTooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        FragmentActivity activity = getActivity();
                        super.handleMessage(msg);
                    }

                };
                if (getDeviceState()) {
                    enableBluetooth();
                    Log.i(TAG, "btService => enableBT" + getDeviceState());
                } else {

                }
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
            Log.i(TAG, "MainFragment - onLocationChanged");
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
    public void startLocationService() { // GPS 권한 체크여부
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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60 * 1000, 0, locationListener);
    }

    public void showLocationInfo() { // 권한 부여 및 위도, 경도 받아오는 메소드

        try {
            if (hasPermissions(permissions)) {

                startLocationService();
                longtitude = location.getLongitude();
                latitude = location.getLatitude();
                textLocation.setText("경도 : " + longtitude + "\n" + "위도 : " + latitude);
                Log.i(TAG, "MainFragment - showLocationInfo");

                swipeRefreshLayout.setRefreshing(false);
            } else {
                if (ActivityCompat
                        .shouldShowRequestPermissionRationale(getActivity(), permissions[0])) {
                    Toast.makeText(getContext(), "아래로 끌어 새로고침이 필요합니다.", Toast.LENGTH_LONG).show();
                } else if (ActivityCompat
                        .shouldShowRequestPermissionRationale(getActivity(), permissions[1])) {
                    Toast.makeText(getContext(), "GPS 가 안되서 근접한 거리라도...", Toast.LENGTH_LONG).show();
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
                {"", "1시", "2시", "3시", "4시",
                        "5시", "6시", "7시", "8시", "9시",
                        "10시", "11시", "12시", "13시", "14시",
                        "15시", "16시", "17시", "18시", "19시",
                        "20시", "21시", "22시", "23시", "24시"};

        ArrayList<Entry> dataset1 = new ArrayList<Entry>();

        if (calendar < 24) { // 해당시간대 k 이후는 0으로 초기화합니다. 24시가 되면 그날의 1시부터 24시까지 모두 나옵니다
            for (int k = calendar; k < xAxis.length - 1; k++) {
                list_pm10value[k] = 0;
            }
            for (int i = 0; i < calendar; i++) { // 해당 시간대까지 그래프를 출력하도록 합니다
                dataset1.add(new Entry(list_pm10value[i], i + 1));
            }
        } else {
            for (int i = 0; i < calendar; i++) { // 해당 시간대까지 그래프를 출력하도록 합니다
                dataset1.add(new Entry(list_pm10value[i], i + 1));
            }
        }

        ArrayList<Entry> dataset2 = new ArrayList<Entry>();
        dataset2.add(new Entry(55, 1));
        dataset2.add(new Entry(35, 2));
        dataset2.add(new Entry(25, 3));
        dataset2.add(new Entry(25, 4));
        dataset2.add(new Entry(45, 5));
        dataset2.add(new Entry(35, 6));
        dataset2.add(new Entry(35, 7));
        dataset2.add(new Entry(25, 8));
        dataset2.add(new Entry(75, 9));
        dataset2.add(new Entry(35, 10));
        dataset2.add(new Entry(45, 11));
        dataset2.add(new Entry(35, 12));
        dataset2.add(new Entry(35, 13));
        dataset2.add(new Entry(25, 14));
        dataset2.add(new Entry(18, 15));
        dataset2.add(new Entry(25, 16));
        dataset2.add(new Entry(15, 17));
        dataset2.add(new Entry(55, 18));
        dataset2.add(new Entry(35, 19));
        dataset2.add(new Entry(25, 20));
        dataset2.add(new Entry(45, 21));
        dataset2.add(new Entry(35, 22));
        dataset2.add(new Entry(25, 23));
        dataset2.add(new Entry(16, 24));

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
        lineChart.getData().setHighlightEnabled(false);
        Legend legend = lineChart.getLegend();
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_RIGHT);
    }


    //pgs 위도 경도 값 불러오기 끝

    public void getAddress() { // 주소값 불러오기
        list = GeoCoding.getlatitude(latitude, longtitude, getContext());


    }

    class SexyAss extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) { // 인터넷 사용을 위한 쓰래드

            if (list.get(0).getThoroughfare() == null) { // 이름이 없을 시 "구" 로 검색
                dustDrugDAOImple.fuckTM(list);
                dustDrugDAOImple.getStationName(dustDrugDAOImple.data.getTmX(), dustDrugDAOImple.data.getTmY());
                dustDrugDAOImple.kimKwangSukInthespiritofforgetting(dustDrugDAOImple.data.getStationName());

            } else { // "동" 으로 검색
                dustDrugDAOImple.fuckTM(list);
                dustDrugDAOImple.getStationName(dustDrugDAOImple.data.getTmX(), dustDrugDAOImple.data.getTmY());
                dustDrugDAOImple.kimKwangSukInthespiritofforgetting(dustDrugDAOImple.data.getStationName());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) { // 데이터 수치 갱신
            super.onPostExecute(o);
            textLocation.setText(list.get(0).getLocality()); // 시, 도
//            Log.i(TAG, "location = " + list.get(0).getLocality());
            textLocation.append(" ");
            textLocation.append(list.get(0).getSubLocality()); // 구,군
//            Log.i(TAG, "Sub = " + list.get(0).getSubLocality());


            year = Integer.parseInt(dustDrugDAOImple.data.getDetailData().get(0).getDataTime().substring(0, 4)); // 년
//            Log.i("TAG", year + "년");

            month = Integer.parseInt(dustDrugDAOImple.data.getDetailData().get(0).getDataTime().substring(5, 7)); // 월
//            Log.i("TAG", month + "월");

            date = Integer.parseInt(dustDrugDAOImple.data.getDetailData().get(0).getDataTime().substring(8, 10)); // 일
//            Log.i("TAG", date + "일");

            calendar = Integer.parseInt(dustDrugDAOImple.data.getDetailData().get(0).getDataTime().substring(11, 13)); // 시(時)
//            Log.i("TAG", "현재 hour : " + calendar + "시");

            textTime.setText("※ " + year + "년 " + month + "월 " + date + "일 " + calendar + "시 기준"); // 날짜, 시간 출력
//            textTime.setText(dustDrugDAOImple.data.getDetailData().get(0).getDataTime());
//            Log.i(TAG, "DATE = " + dustDrugDAOImple.data.getDetailData().get(0).getDataTime());

            textShowValue.setText("미세먼지 : " + dustDrugDAOImple.data.getDetailData().get(0).getPm10Value() + " ㎍/㎥"); // 미세먼지(PM10)
//            Log.i(TAG, "PM10 = " + dustDrugDAOImple.data.getDetailData().get(0).getPm10Value());

            String gradePm10 = dustDrugDAOImple.data.getDetailData().get(0).getPm10Grade1h().toString(); // 미세먼지 등급(PM2.5)
            if (gradePm10.equals("1")) {
                textValueGrade.setText("좋음");

            } else if (gradePm10.equals("2")) {
                textValueGrade.setText("보통");

            } else if (gradePm10.equals("3")) {
                textValueGrade.setText("나쁨");

            } else if (gradePm10.equals("4")) {
                textValueGrade.setText("매우나쁨");

            } else {
                textValueGrade.setText("등급확인 불가");
            }
//            Log.i(TAG, "Grade = " + dustDrugDAOImple.data.getDetailData().get(0).getPm10Gradel());


            textShowValuePm25.setText("초미세먼지 : " + dustDrugDAOImple.data.getDetailData().get(0).getPm25Value() + " ㎍/㎥"); // 초미세먼지(PM2.5)
//            Log.i(TAG, "PM2.5 = " + dustDrugDAOImple.data.getDetailData().get(0).getPm25Value());


            for (int i = 0; i < 24; i++) { // 그래프 수치
                list_pm10value[i] = Integer.parseInt(dustDrugDAOImple.data.getDetailData().get(23 - i).getPm10Value());
//                Log.i("TAG", "현재 수치 : " + pm10value);
            }

        }

    } // end class SexyAss extends AsyncTask

    public void getSearchFragmentAddress(List<Address> list) {
        this.list = list;
        SexyAss sexyAss = new SexyAss();
        sexyAss.execute();
    }

    private void enableBluetooth() {
        if (btAdapter.isEnabled()) {
            scanDevice();
        } else {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    private void scanDevice() {
        Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }

    private boolean getDeviceState() {
        if (btAdapter == null) {
            Toast.makeText(getActivity(), "블루투스 지원안함요", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            Toast.makeText(getActivity(), "블루투스 지원해요", Toast.LENGTH_SHORT).show();
            return true;

        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "requestCode는" + requestCode);
        Log.i(TAG, "resultCode" + resultCode);

        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    getDeviceInfo(data);
                    Log.i(TAG, "REQUEST_CONNECT_DEVICE = " + REQUEST_CONNECT_DEVICE);
                }
                break;
            case REQUEST_ENABLE_BT:
                if (requestCode == Activity.RESULT_OK) {
                    scanDevice();
                    Log.i(TAG, "REQUEST_CONNECT_DEVICE = " + REQUEST_ENABLE_BT);
                } else {
                    Toast.makeText(getContext(), "블루투스를 활성화 해라...", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    private void getDeviceInfo(Intent data) {
        String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        BluetoothDevice device = btAdapter.getRemoteDevice(address);
        Log.i(TAG, "Get Device Info \n" + "address : " + address);
        connect(device);
    }

    // Bluetooth 상태 set
    private synchronized void setState(int state) {
        Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;
        if (state == STATE_CONNECTED) {
            Log.i(TAG, "연결 됨");
        }
    }

    // Bluetooth 상태 get
    public synchronized int getState() {
        return mState;
    }

    public synchronized void start() {
        Log.d(TAG, "start");

        // Cancel any thread attempting to make a connection
        if (mConnectThread == null) {

        } else {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread == null) {

        } else {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
    }

    // ConnectThread 초기화 device의 모든 연결 제거
    public synchronized void connect(BluetoothDevice device) {
        Log.d(TAG, "connect to: " + device);

        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (mConnectThread == null) {

            } else {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread == null) {

        } else {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device);

        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    // ConnectedThread 초기화
    public synchronized void connected(BluetoothSocket socket,
                                       BluetoothDevice device) {
        Log.d(TAG, "connected");

        // Cancel the thread that completed the connection
        if (mConnectThread == null) {

        } else {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread == null) {

        } else {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        setState(STATE_CONNECTED);
    }

    // 모든 thread stop
    public synchronized void stop() {
        Log.d(TAG, "BluetoothServie - stop");

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        setState(STATE_NONE);
    }

    // 값을 쓰는 부분(보내는 부분)
    public void write(byte[] out) { // Create temporary object
        ConnectedThread r; // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED)
                return;
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
//         r.write(out);

    }

    // 연결 실패했을때
    private void connectionFailed() {
        setState(STATE_LISTEN);
        Log.d(TAG, "BluetoothServie - stop");
    }

    // 연결을 잃었을 때
    private void connectionLost() {
        setState(STATE_LISTEN);
        Log.d(TAG, "BluetoothServie - stop");
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;

            // 디바이스 정보를 얻어서 BluetoothSocket 생성
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "create() failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectThread");
            setName("ConnectThread");

            // 연결을 시도하기 전에는 항상 기기 검색을 중지한다.
            // 기기 검색이 계속되면 연결속도가 느려지기 때문이다.
            btAdapter.cancelDiscovery();

            // BluetoothSocket 연결 시도
            try {
                // BluetoothSocket 연결 시도에 대한 return 값은 succes 또는 exception이다.
                mmSocket.connect();
                Log.d(TAG, "Connect Success");

            } catch (IOException e) {
                connectionFailed(); // 연결 실패시 불러오는 메소드
                Log.d(TAG, "Connect Fail");

                // socket을 닫는다.
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() socket during connection failure", e2);
                }
                // 연결중? 혹은 연결 대기상태인 메소드를 호출한다.
                start();
                return;
            }

            // ConnectThread 클래스를 reset한다.
            synchronized (this) {
                mConnectThread = null;
            }

            // ConnectThread를 시작한다.
            connected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "create ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // BluetoothSocket의 inputstream 과 outputstream을 얻는다.
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // InputStream으로부터 값을 받는 읽는 부분(값을 받는다)
                    bytes = mmInStream.read(buffer);

                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         *
         * @param buffer The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                // 값을 쓰는 부분(값을 보낸다)
                mmOutStream.write(buffer);

            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

} // end MainFragment