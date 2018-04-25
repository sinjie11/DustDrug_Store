package edu.android.dustdrug;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FirstFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class FirstFragment extends Fragment {

    private TextView textView;
    private Thread loadingThread;
    private DustDrugDAOImple daoImple;
    private OnFragmentInteractionListener mListener;
    public int longtitude;
    public int latitude;
    private MainActivity mainActivity;
    private LocationManager locationManager;
    private Location location;
    private Handler handler = new Handler();

    public FirstFragment() {
        // Required empty public constructor

    }



    public static final String TAG = "edu.android";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        textView = view.findViewById(R.id.textView);
        Log.i(TAG, "FirstFragment - 스레드 전");
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FackMB fackMB = new FackMB();
        fackMB.execute();
        Log.i(TAG, "FirstFragment - fackMB execute");

//        loadingThread.start();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
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
        daoImple.getInstence(context);
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    public void onLoding0() {
        textView.setText("Loading");
        Log.i(TAG, "FirstFragment - onLoding0");
    }

    public void onLoding1() {
        textView.setText("Loading.");
        Log.i(TAG, "FirstFragment - onLoding1");
    }

    public void onLoding2() {
        textView.setText("Loading..");
        Log.i(TAG, "FirstFragment - onLoding2");
    }

    public void onLoding3() {
        textView.setText("Loading...");
        Log.i(TAG, "FirstFragment - onLoding3");
    }

    public void onLoding4() {
        textView.setText("Complete");
        Log.i(TAG, "FirstFragment - onLoding4");
    }

    public void endLoding() {
        mainActivity = (MainActivity) getActivity();
        mainActivity.getMainfragment();
        Log.i(TAG, "FirstFragment - endLoding");
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
            Log.i(TAG, "FirstFragment - onLocationChanged, location : " + location);
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
    public interface OnFragmentInteractionListener{
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    class FackMB  extends AsyncTask<Void, Integer , Void>{

        @Override
        protected Void doInBackground(Void... voids) {// 예시 이부분에 imple 을 생성하면됨

            try {
                Thread.sleep(1000);
                Log.i(TAG, "FirstFragment - 1초");
                publishProgress(0);
                Thread.sleep(1000);
                Log.i(TAG, "FirstFragment - 2초");
                publishProgress(1);
                Thread.sleep(1000);
                Log.i(TAG, "FirstFragment - 3초");
                publishProgress(2);
                Thread.sleep(1000);
                Log.i(TAG, "FirstFragment - 4초");
                publishProgress(3);
                Thread.sleep(1000);
                Log.i(TAG, "FirstFragment - 5초");
                publishProgress(4);
                endLoding();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            // doInBackground() 시작하기 전에 UI 업데이트
            daoImple  = DustDrugDAOImple.getInstence(getContext());
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // doInBackground()가 끝났을 때 UI 업데이트
//            try {
//                Thread.sleep(1000);
//                Log.i(TAG, "FirstFragment - 1초");
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            onLoding0();
//            try {
//                Thread.sleep(1000);
//                Log.i(TAG, "FirstFragment - 2초");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }onLoding1();
//            try {
//                Thread.sleep(1000);
//                Log.i(TAG, "FirstFragment - 3초");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }onLoding2();
//            try {
//                Thread.sleep(1000);
//                Log.i(TAG, "FirstFragment - 4초");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }onLoding3();
//            try {
//                Thread.sleep(1000);
//                Log.i(TAG, "FirstFragment - 5초");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }onLoding4();
//            try {
//                Thread.sleep(1000);
//                Log.i(TAG, "FirstFragment - 6초");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            endLoding();

        }

        @Override
        protected void onProgressUpdate(Integer... values) {//publishProgress(4); 를 호출 했을때 UI 작업
            switch (values[0]) {
                case 0:
                    onLoding0();
                    break;
                case 1:
                    onLoding1();
                    break;
                case 2:
                    onLoding2();
                    break;
                case 3:
                    onLoding3();
                    break;
                case 4:
                    onLoding4();
                    break;
            }
        }
    } // 문제 발생 기술적으로 구현 불가능합니다..


}
