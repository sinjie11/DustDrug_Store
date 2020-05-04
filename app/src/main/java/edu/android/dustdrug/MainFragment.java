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
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
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
     *
     * - 실시간 정보 : 10분(매 시간 시간자료 갱신은 20분 전후로 반영됨)
     *
     * - 대기질 예보 정보 : 매 시간 22분, 57분
     *
     * ※ Grade 값
     * 좋음     : 1   ( pm10 => 0 ~ 30 , pm2.5 => 0 ~ 15 )
     * 보통     : 2   ( pm10 => 31 ~ 80 , pm2.5 => 16 ~ 50 )
     * 나쁨     : 3   ( pm10 => 81 ~ 150 , pm2.5 => 51 ~ 100 )
     * 매우나쁨 : 4   ( pm10 => 151 ~ , pm2.5 => 101 ~ )
     *
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
    private MainFragment mainFragment;

    private MainActivity mainActivity;
    public TextView textLocation, textShowValue, textValueGrade, textTime, textShowValuePm25, textInfo;
    public ImageButton btnBlueTooth, btnSearch;

    public int pm10value;
    public int pm10Grade;

    public int calendar;
    public int year;
    public int month;
    public int date;
    public int[] list_pm10value = new int[24];
    public int[] list_pm25value = new int[24];

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

    //블루투스 관련
    private char mCharDelimiter = '\n';
    private String mStrDelimiter = "\n";
    private int readBufferPosition;
    private byte[] readBuffer;
    private String reciveData = ""; //블루투스 받은 데이터 저장 공간
    private boolean bluetoothOn = false; //블루투스 데이터 받고 리플레쉬 했을 경우 채크 변수
    private ImageView gbView;

    private void refresh() { // 블루투스 새로고침
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.detach(this).attach(this).commit();
        bluetoothOn = true;
    }


    // RfComm Protocol
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
        mainActivity = (MainActivity) getContext();
        textLocation = view.findViewById(R.id.textLocation);
        textShowValue = view.findViewById(R.id.textShowValue);
        btnBlueTooth = view.findViewById(R.id.btnBlueTooth);
        btnSearch = view.findViewById(R.id.btnSearch);
        textValueGrade = view.findViewById(R.id.textValueGrade);
        textTime = view.findViewById(R.id.textTime);
        textShowValuePm25 = view.findViewById(R.id.textShowValuePm25);
        textInfo = view.findViewById(R.id.textInfo);
        gbView = view.findViewById(R.id.gbView);

        textLocation.setTextColor(Color.parseColor("#FFFFFF"));

        lineChart.setDescription(" ");
        showLineChart(); // LineChart 를 보여주는 메소드를 불러옵니다.

        resourceDataInfo(); // 자료제공 내용

        btnBlueTooth.setVisibility(View.GONE); // 블루투스 버튼 이미지 보이지 않게 설정. (VISIBLE: 보임, GONE : 보이지 않음)

        /**
         * 2020.03.28 주석 처리
         * GIF 이미지 사용 시 필요
         */
        /*GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(gbView);
        Glide.with(this).load(R.drawable.gbb).into(gifImage);*/

        /**
         * 2020.03.28 추가
         * JPG 이미지로 변경
         */
        GlideDrawableImageViewTarget jpgImage = new GlideDrawableImageViewTarget(gbView);
        Glide.with(this).load(R.drawable.ic_pr_logo_black).into(jpgImage);

        /**
         * 2020.03.28 주석 처리
         * 이미지 사용 시 클릭 이벤트 (URL이동)
         */
        /*gbView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://dak.gg/profile/Suhyeoney/2018-02/as"));
                startActivity(intent);
            }
        });*/

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.mainFragment);
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.YELLOW, Color.RED); // 새로고침 색상 변경
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() { // 새로고침 시 권한부여 및 좌표 받아오기

                startLocationService(); // GPS 권한 체크여부
                showLocationInfo(); // 위도, 경도 불러오기
                showLineChart(); // 새로고침 할때도 LineChart 메소드 다시 불러옴
                getAddress(); // 좌표 주소로 변환 시 구 동

                try {
                    if (list.size() > 0) {
                        textLocation.setText(list.get(0).getLocality()); // 시,도 정보
                        textLocation.append(" ");
                        textLocation.append(list.get(0).getSubLocality()); // 구,군 정보
                        textLocation.append(" ");

                        AddressSearch addressSearch = new AddressSearch();
                        addressSearch.execute();

                    } else {
                        Toast.makeText(getContext(), "위치정보가 확인되지 않습니다.", Toast.LENGTH_SHORT).show();
                    }

                    bluetoothOn = false;

                } catch (NullPointerException e) {
                    e.getMessage();
                } // end try-catch

            }
        });

        try {

            if (dustDrugDAOImple.data.getStationName() != null) {
                updatingData();
            }

            // SharedPreference 값 불러오기
            if (null == dustDrugDAOImple.data.getStationName()) {

            }

        } catch (Exception e) {
            list = mainActivity.sharedPrefRead();
        }

        Log.i("async", "MainFragment #244");

        if (list.get(0).getLocality() != null) {
            AddressSearch addressSearch = new AddressSearch();
            addressSearch.execute();
        }

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                searchFragment = new SearchFragment();
                transaction.replace(R.id.fragment_container, searchFragment);
                transaction.commit();
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
            longtitude = location.getLongitude();
            latitude = location.getLatitude();
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
        if (requestCode == REQ_CODE_PERMISSION) {
            if (grantResults.length == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                return;

            } else {
                Toast.makeText(getContext(), "Make the authorization get allowed", Toast.LENGTH_SHORT).show();
            }
        }
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

    // GPS 권한 체크여부
    public void startLocationService() {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }

        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60 * 1000, 0, locationListener);
    }

    // GPS 권한 부여, 위도, 경도 받아오는 메소드
    public void showLocationInfo() {

        try {
            if (hasPermissions(permissions)) {

                startLocationService();
                longtitude = location.getLongitude();
                latitude = location.getLatitude();
                textLocation.setText(longtitude + "," + latitude);

                swipeRefreshLayout.setRefreshing(false);

            } else {

                if (ActivityCompat
                        .shouldShowRequestPermissionRationale(getActivity(), permissions[0])) {
                    Toast.makeText(getContext(), "새로고침이 필요합니다.(아래로 끌어당겨주세요)", Toast.LENGTH_LONG).show();

                } else if (ActivityCompat
                        .shouldShowRequestPermissionRationale(getActivity(), permissions[1])) {
                    Toast.makeText(getContext(), "GPS 가 수신 되지 않고 있습니다. 확인해주세요.", Toast.LENGTH_LONG).show();
                }

                swipeRefreshLayout.setRefreshing(false);
                ActivityCompat.requestPermissions(getActivity(), permissions, REQ_CODE_PERMISSION);
            }

        } catch (NullPointerException e) {
            e.getMessage();
            Toast.makeText(getContext(), "위치정보가 확인되지 않습니다.", Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
        }

    }

    public void showLineChart() { // LineChart 를 표시 하는 메소드

        String[] xAxis = new String[]
                {"", "1시", "2시", "3시", "4시",
                        "5시", "6시", "7시", "8시", "9시",
                        "10시", "11시", "12시", "13시", "14시",
                        "15시", "16시", "17시", "18시", "19시",
                        "20시", "21시", "22시", "23시", "24시"};


        int cnt = 24 - calendar;
        Log.i(TAG, "calendar : " + calendar);
        Log.i(TAG, "calendar : " + cnt);

        if (cnt == 23) {
            for (int x = 0; x < 1; x++) {
                for (int k = 1; k < 25; k++) {
                    if (k == 1) {
                        xAxis[24] = xAxis[k];

                    } else {
                        xAxis[k - 1] = xAxis[k];
                    }
                }
            }

        } else {
            for (int x = 0; x < cnt; x++) {
                for (int k = 25; k >= 2; k--) {
                    if (k == 25) {
                        xAxis[1] = xAxis[k - 1];

                    } else {
                        xAxis[k] = xAxis[k - 1];
                    }
                }
            }
        }

        ArrayList<ILineDataSet> lines = new ArrayList<ILineDataSet>();
        ArrayList<Entry> dataset1 = new ArrayList<Entry>();
        ArrayList<Entry> dataset2 = new ArrayList<Entry>();

        LineDataSet lineDataSet1 = new LineDataSet(dataset1, "미세먼지");
        lineDataSet1.setColor(Color.parseColor("#cb1ad6"));
        lineDataSet1.setCircleColor(Color.parseColor("#cb1ad6"));
        lineDataSet1.setValueTextColor(Color.WHITE);
        lineDataSet1.setDrawCubic(true);
        lines.add(lineDataSet1);

        LineDataSet lineDataSet2 = new LineDataSet(dataset2, "초미세먼지");
        lineDataSet2.setColor(Color.parseColor("#0deaf0"));
        lineDataSet2.setCircleColor(Color.parseColor("#0deaf0"));
        lineDataSet2.setValueTextColor(Color.WHITE);
        lineDataSet2.setDrawCubic(true);
        lines.add(lineDataSet2);

        if (!bluetoothOn) { //bluetoothOn 연결이 되어 있지 않을 때
            for (int k = 0; k < 24; k++) { // 해당 시간대까지 그래프를 출력하도록 합니다
                if (list_pm10value[k] == -1) {
                    list_pm10value[k] = 0;

                } else {
                    dataset1.add(new Entry(list_pm10value[k], k + 1));
                }
            }

            for (int i = 0; i < 24; i++) { // 해당 시간대까지 그래프를 출력하도록 합니다
                if (list_pm25value[i] == -1) {
                    list_pm25value[i] = 0;

                } else {
                    dataset2.add(new Entry(list_pm25value[i], i + 1));
                }
            }

        } else { // bluetoothOn 연결이 되어 있을 때

            ArrayList<Entry> blueDataSet = new ArrayList<Entry>();

            int cnt_bluetooth = 24 - calendar;
            for (int x = 0; x < cnt_bluetooth; x++) {
                for (int k = 25; k >= 2; k--) {
                    if (k == 25) {
                        reciveData.split("/")[1] = reciveData.split("/")[k - 1];
                    } else {
                        reciveData.split("/")[k] = reciveData.split("/")[k - 1];
                    }
                }
            }

            try {
                for (int i = 1; i < 25; i++)
                    blueDataSet.add(new Entry(Float.parseFloat(reciveData.split("/")[i]), i));
                    int idx = reciveData.split("/")[24].indexOf(".");

                    textShowValuePm25.setText("초미세먼지\n" + reciveData.split("/")[24].substring(0, idx) + " ㎍/㎥"); // 초미세먼지(PM2.5)
                    textLocation.setText("블루투스 측정");
                    textShowValue.setText(" ");
                    textValueGrade.setText(" ");
                    textValueGrade.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

            } catch (NumberFormatException e) {
                e.getMessage();
            }

            LineDataSet lineDataSet = new LineDataSet(blueDataSet, "초미세먼지");
            lineDataSet.setColor(Color.parseColor("#0deaf0"));
            lineDataSet.setCircleColor(Color.parseColor("#0deaf0"));
            lineDataSet.setDrawCubic(true);
            lines.add(lineDataSet);

        }

        lineChart.setData(new LineData(xAxis, lines));
        lineChart.animateY(1500);
        lineChart.setScaleEnabled(false);
        lineChart.getAxisLeft().setTextColor(Color.WHITE);
        lineChart.getAxisRight().setTextColor(Color.WHITE);
        lineChart.getData().setHighlightEnabled(false);
        lineChart.getXAxis().getSpaceBetweenLabels();
        lineChart.getXAxis().setTextColor(Color.parseColor("#FFFFFF"));

        Legend legend = lineChart.getLegend();
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_RIGHT);
        legend.setTextColor(Color.parseColor("#FFFFFF"));
    }

    // GPS 위도 경도 값 불러오기
    public void getAddress() {
        list = GeoCoding.getlatitude(latitude, longtitude, getContext());
    }

    public class AddressSearch extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) { // 인터넷 사용을 위한 쓰레드

            if (list.get(0).getThoroughfare() == null) { // 이름이 없을 시 "구" 로 검색

                dustDrugDAOImple.changeAddressTmConvert(list);
                dustDrugDAOImple.getStationName(dustDrugDAOImple.data.getTmX(), dustDrugDAOImple.data.getTmY());
                dustDrugDAOImple.measurementCallGetData(dustDrugDAOImple.data.getStationName());
            } else { // "동" 으로 검색

                dustDrugDAOImple.changeAddressTmConvert(list);
                dustDrugDAOImple.getStationName(dustDrugDAOImple.data.getTmX(), dustDrugDAOImple.data.getTmY());
                dustDrugDAOImple.measurementCallGetData(dustDrugDAOImple.data.getStationName());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            updatingData(); // 데이터 수치를 갱신 해주는 메소드
        }

    } // end class SexyAss extends AsyncTask

    public void getSearchFragmentAddress(List<Address> list) {
        this.list = list;
        Log.i("async", "MainFragment #553");
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

            Toast.makeText(getActivity(), "블루투스를 지원하지 않습니다.", Toast.LENGTH_SHORT).show();
            return false;
        } else {

            Toast.makeText(getActivity(), "블루투스를 지원 합니다.", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "requestCode 는" + requestCode);
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

                    Toast.makeText(getContext(), "블루투스를 활성화 시켜주세요.", Toast.LENGTH_SHORT).show();
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

    private synchronized void setState(int state) {

        Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;

        if (state == STATE_CONNECTED) { // 3

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

    // ConnectThread 초기화 device 의 모든 연결 제거
    public synchronized void connect(BluetoothDevice device) {
        Log.d(TAG, "connect to: " + device);

        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {

            if (mConnectThread != null) {

                mConnectThread.cancel();
                mConnectThread = null;
            } else {

            }
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {

            mConnectedThread.cancel();
            mConnectedThread = null;
        } else {

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
        if (mConnectThread != null) {

            mConnectThread.cancel();
            mConnectThread = null;
        } else {

        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {

            mConnectedThread.cancel();
            mConnectedThread = null;
        } else {

        }

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);

        if (mConnectedThread.isAlive()) {

            mConnectedThread.interrupt();
        } else {

            mConnectedThread.start();
        }

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
            if (mState != STATE_CONNECTED) {

                return;
            }

            r = mConnectedThread;
        }

        // Perform the write unSynchronized
//         r.write(out);

    }

    // 연결실패 했을때
    private void connectionFailed() {
        setState(STATE_LISTEN);
        Log.d(TAG, "BluetoothService - stop");
    }

    // 연결을 잃었을 때
    private void connectionLost() {
        setState(STATE_LISTEN);
        Log.d(TAG, "BluetoothService - stop");
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
                // BluetoothSocket 연결 시도에 대한 return 값은 success 또는 exception 이다.
                mmSocket.connect();
                Log.d(TAG, "Connect Success");

            } catch (IOException e) {
                connectionFailed(); // 연결 실패시 불러오는 메소드
                Log.d(TAG, "Connect Fail");

                // socket 을 닫는다.
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() socket during connection failure", e2);
                }
                // 연결 중 혹은 연결 대기상태인 메소드를 호출한다.
                if (isInterrupted()) {
                    start();

                } else {
                    interrupt();
                }
                return;
            }

            // ConnectThread 클래스를 reset 한다.
            synchronized (this) {
                mConnectThread = null;
            }

            // ConnectThread 를 시작한다.
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

            // BluetoothSocket 의 InputStream 과 OutputStream 을 얻는다.
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            readBuffer = new byte[1024];   // 수신 버퍼
            readBufferPosition = 0;      // 버퍼 내 수신 문자 저장 위치
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;
            String send = "z";
            send += mStrDelimiter + mStrDelimiter;
            Log.i(TAG, "Send : " + send);

            try {
                mmOutStream.write(send.getBytes());
                Log.i(TAG, "데이터 보냄 성공1");

                mmOutStream.write(send.getBytes());
                Log.i(TAG, "데이터 보냄 성공2");

            } catch (IOException e) {
                e.printStackTrace();
                Log.i(TAG, "데이터 보냄 실패" + e.getMessage());
            }

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Log.i(TAG, "받기 시작2");
                    int bytesAvailable = mmInStream.available();   // 수신 데이터 확인
                    Log.i(TAG, "bytesAvailable : " + bytesAvailable);

                    if (bytesAvailable > 0) {      // 데이터가 수신된 경우
                        Log.i(TAG, "bytesAvailable 뭘까?" + bytesAvailable);
                        byte[] packetBytes = new byte[bytesAvailable];
                        mmInStream.read(packetBytes);

                        for (int i = 0; i < bytesAvailable; i++) {
                            Log.i(TAG, "For 문이여 돌아라~~");
                            byte b = packetBytes[i];

                            if (b == mCharDelimiter) {
                                byte[] encodedBytes = new byte[readBufferPosition];
                                System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                final String data = new String(encodedBytes, "UTF-8");
                                readBufferPosition = 0;
                                Log.i(TAG, "DATA : " + data);

                                if (!data.equals("")) {
                                    reciveData = data;
                                    Log.i(TAG, "reciveData : " + reciveData);
//                                    Toast.makeText(getContext(), "수신 완료", Toast.LENGTH_SHORT).show();
                                    refresh();
                                    interrupt();
                                }

                            } else {
                                readBuffer[readBufferPosition++] = b;
                            }
                        }
                    }
                    Log.i(TAG, "받기 끝");
//                    try {
//                        sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
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

    public void updatingData() { // 데이터 수치 갱신
        try {
            textLocation.setText(dustDrugDAOImple.data.getLocality().toString()); // 시, 도
            textLocation.append(" ");
            textLocation.append(dustDrugDAOImple.data.getSubLocality().toString()); // 구,군

            resourceDataInfo(); // 자료제공 내용

            year = Integer.parseInt(dustDrugDAOImple.data.getDetailData().get(0).getDataTime().substring(0, 4)); // 년
            month = Integer.parseInt(dustDrugDAOImple.data.getDetailData().get(0).getDataTime().substring(5, 7)); // 월
            date = Integer.parseInt(dustDrugDAOImple.data.getDetailData().get(0).getDataTime().substring(8, 10)); // 일

            calendar = Integer.parseInt(dustDrugDAOImple.data.getDetailData().get(0).getDataTime().substring(11, 13)); // 시(時)
            textTime.setText("※ " + year + "년 " + month + "월 " + date + "일 " + calendar + "시 기준"); // 날짜, 시간 출력

            if (Integer.parseInt(dustDrugDAOImple.data.getDetailData().get(0).getPm10Value()) == -1) {
                textShowValue.setText("미세먼지\n데이터를\n찾을 수 없습니다"); // 미세먼지(PM10)

            } else {
                textShowValue.setText("미세먼지\n" + dustDrugDAOImple.data.getDetailData().get(0).getPm10Value() + " ㎍/㎥"); // 미세먼지(PM10)
            }

            String gradePm10 = dustDrugDAOImple.data.getDetailData().get(0).getPm10Grade1h().toString(); // 미세먼지 등급(PM2.5)

            mainActivity.sharedPrefSave(dustDrugDAOImple.data.getLocality(), dustDrugDAOImple.data.getSubLocality(), dustDrugDAOImple.data.getThoroughfare()); // SharedPreference에 저장

            if (gradePm10.equals("1")) {

                textValueGrade.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.grade_good, 0);
                textValueGrade.setCompoundDrawablePadding(10); // 미세먼지 등급 이미지 변경
                textValueGrade.setTextColor(Color.parseColor("#3785c3")); // 글자색 변경(이미지 색상과 동일)
                textValueGrade.setText("좋음");
            } else if (gradePm10.equals("2")) {

                textValueGrade.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.grade_usually, 0);
                textValueGrade.setCompoundDrawablePadding(10);
                textValueGrade.setTextColor(Color.parseColor("#66bb46"));
                textValueGrade.setText("보통");
            } else if (gradePm10.equals("3")) {

                textValueGrade.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.grade_bad, 0);
                textValueGrade.setCompoundDrawablePadding(10);
                textValueGrade.setTextColor(Color.parseColor("##d88829"));
                textValueGrade.setText("나쁨");
            } else if (gradePm10.equals("4")) {

                textValueGrade.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.grade_verybad, 0);
                textValueGrade.setCompoundDrawablePadding(10);
                textValueGrade.setTextColor(Color.parseColor("#da4f4a"));
                textValueGrade.setText("매우나쁨");
            } else {

                textValueGrade.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.grade_no, 0);
                textValueGrade.setCompoundDrawablePadding(10);
                textValueGrade.setTextColor(Color.parseColor("#FFFFFF"));
                textValueGrade.setText("등급 확인 불가"); // 미세먼지(PM10)

                if (Integer.parseInt(dustDrugDAOImple.data.getDetailData().get(0).getPm10Value()) < 30 && Integer.parseInt(dustDrugDAOImple.data.getDetailData().get(0).getPm10Value()) > 0) {

                    textValueGrade.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.grade_good, 0);
                    textValueGrade.setCompoundDrawablePadding(10);
                    textValueGrade.setTextColor(Color.parseColor("#3785c3"));
                    textValueGrade.setText("좋음");
                } else if (Integer.parseInt(dustDrugDAOImple.data.getDetailData().get(0).getPm10Value()) < 80 && Integer.parseInt(dustDrugDAOImple.data.getDetailData().get(0).getPm10Value()) > 31) {

                    textValueGrade.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.grade_usually, 0);
                    textValueGrade.setCompoundDrawablePadding(10);
                    textValueGrade.setTextColor(Color.parseColor("#66bb46"));
                    textValueGrade.setText("보통");
                } else if (Integer.parseInt(dustDrugDAOImple.data.getDetailData().get(0).getPm10Value()) < 150 && Integer.parseInt(dustDrugDAOImple.data.getDetailData().get(0).getPm10Value()) > 81) {

                    textValueGrade.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.grade_bad, 0);
                    textValueGrade.setCompoundDrawablePadding(10);
                    textValueGrade.setTextColor(Color.parseColor("##d88829"));
                    textValueGrade.setText("나쁨");
                } else if (Integer.parseInt(dustDrugDAOImple.data.getDetailData().get(0).getPm10Value()) > 150) {

                    textValueGrade.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.grade_verybad, 0);
                    textValueGrade.setCompoundDrawablePadding(10);
                    textValueGrade.setTextColor(Color.parseColor("#da4f4a"));
                    textValueGrade.setText("매우나쁨");
                } else {

                    textValueGrade.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.grade_no, 0);
                    textValueGrade.setCompoundDrawablePadding(10);
                    textValueGrade.setTextColor(Color.parseColor("#FFFFFF"));
                    textValueGrade.setText("등급 확인 불가"); // 미세먼지(PM10)
                }

            }

            if (Integer.parseInt(dustDrugDAOImple.data.getDetailData().get(0).getPm25Value()) == -1) {
                textShowValuePm25.setText("초미세먼지\n데이터를\n찾을 수 없습니다"); // 초미세먼지(PM2.5)
            } else {
                textShowValuePm25.setText("초미세먼지\n" + dustDrugDAOImple.data.getDetailData().get(0).getPm25Value() + " ㎍/㎥"); // 초미세먼지(PM2.5)
            }

            for (int i = 0; i < 24; i++) { // 그래프 수치
                list_pm10value[i] = Integer.parseInt(dustDrugDAOImple.data.getDetailData().get(23 - i).getPm10Value());
            }

            for (int i = 0; i < 24; i++) { // 그래프 수치 pm25
                list_pm25value[i] = Integer.parseInt(dustDrugDAOImple.data.getDetailData().get(23 - i).getPm25Value());
                Log.i("TAG", "현재 수치 : " + list_pm25value[i]);
            }

            showLineChart();

        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void getAddressInfo() { // 시, 도, 구, 군의 API 정보를 가져올 때 사용

        try {
            if (list.size() > 0) {
                textLocation.setText(list.get(0).getLocality()); // 시,도 정보
                textLocation.append(" ");
                textLocation.append(list.get(0).getSubLocality()); // 구,군 정보

                Log.i("async", "MainFragment #1063");
                AddressSearch addressSearch = new AddressSearch();
                addressSearch.execute();

            } else {
                Toast.makeText(getContext(), "위치정보가 확인되지 않습니다.", Toast.LENGTH_SHORT).show();
            }

            bluetoothOn = false;

        } catch (NullPointerException e) {
            e.getMessage();
        } // end try-catch
    }

    public void resourceDataInfo() { // 자료제공 정보 메소드
        textInfo.setText(" ※ DustDrug는 본인과 가장 근접한 위치에 있는" + "\n");
        textInfo.append("   측정소의 실시간 정보를 보여 드립니다." + "\n");
        textInfo.append("   해당 자료는 한국환경공단(AirKorea)과 기상청에서 제공하는" + "\n");
        textInfo.append("   실시간 측정자료 이며, 실제 대기농도 수치와 다를 수 있습니다.");
    }
}