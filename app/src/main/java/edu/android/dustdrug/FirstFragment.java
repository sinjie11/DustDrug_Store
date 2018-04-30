package edu.android.dustdrug;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FirstFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class FirstFragment extends Fragment {

    private TextView textView, textView2;
    private Thread loadingThread;
    private ProgressBar progressBar;
    private int progress = 0;

    private MainFragment mainFragment;
    private OnFragmentInteractionListener mListener;

    public int longtitude;
    public int latitude;

    private LocationManager locationManager;
    private Location location;

    public FirstFragment() {
        // Required empty public constructor
    }


    public static final String TAG = "edu.android";


    // Handler 클래스를 상속받는 클래스를 정의 및 생성 (ProgressBar)

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // Message 에서 데이터를 읽어서 ProgressBar 와 TextView 를 업데이트
            Bundle data = msg.getData();
            int progress = data.getInt(TAG);
            progressBar.setProgress(progress);
            textView2.setText(String.valueOf(progress) + "%");
            if (progress < 10) {
                textView.setText("Loading...");
            } else if (progress < 20) {
                textView.setText("GPS 정보를 수신중입니다.");
            } else if (progress < 30) {
                textView.setText("블루투스 정보를 수신 중입니다..");
            } else if (progress < 40) {
                textView.setText("미세먼지 정보를 수신 중입니다..");
            } else if (progress < 50) {
                textView.setText("주소 정보를 수신 중입니다...");
            } else {
                textView.setText("Loading...");
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        textView = view.findViewById(R.id.textView);
        textView2 = view.findViewById(R.id.textView2);
        progressBar = view.findViewById(R.id.progressBar);

        Log.i(TAG, "스레드 전");

        if (loadingThread == null) {

            loadingThread = new Thread() {
                @Override
                public void run() {

                    try {

                        while (progress <= 100) {
                            // Handler 의 Message 를 사용해서 progress 정보를 보냄
                            // progress 를 증가 -> 잠깐 대기
                            Message msg = handler.obtainMessage();
                            Bundle data = new Bundle();
                            data.putInt(TAG, progress);
                            msg.setData(data);
                            handler.sendMessage(msg);

                            progress += 2;

                            synchronized (this) {
                                wait(100); // 100 ms = 0.1초

                                if (progress > 100) {
                                    loadingThread = null;
                                    progress = 0;
                                    break;
                                }
                            }
                        } // end while
                    } catch (InterruptedException e) {
                        // 쓰레드를 interrupt 해서 종료시킬 때

                    } // end try-catch

                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    mainFragment = MainFragment.newInstance();
                    transaction.replace(R.id.fragment_container, mainFragment);
                    transaction.commit();

                } // end run()

            };
            startLocationService();
        } // end if (loadingThread)

        return view;

    } // end onCreateView

    @Override
    public void onStart() {
        super.onStart();
        loadingThread.start();
    }

    public static FirstFragment newInstance() {
        FirstFragment firstFragment = new FirstFragment();
        return firstFragment;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void startLocationService() {
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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.i(TAG, "onLocationChanged, location : " + location);
            longtitude = (int) location.getLongitude();
            latitude = (int) location.getLatitude();
            //TODO : 저장;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
