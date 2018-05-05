package edu.android.dustdrug;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "edu.android";

    private static final int REQUEST_ENABLE_BLUETOOTH = 3;
    private BluetoothAdapter bluetoothAdapter;
    private FirstFragment firstFragment;
    private MainFragment mainFragment;
    private long lastTimeBackPressed = 0;
    private SearchFragment searchFragment;
    Geocoder geocoder = null;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "MainActivity - onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        geocoder = new Geocoder(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "MainActivity - onStart");
    }

    // TODO:


    /* Back 버튼 누를 시 App 종료 기능 */
    @Override
    public void onBackPressed() {

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (fragment instanceof SearchFragment) {

            int result = ((SearchFragment) fragment).backIsClick();

            if (result == 1) {
                sharedPreferences = getPreferences(Context.MODE_PRIVATE);
                String time = sharedPreferences.getString("time", "");

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, mainFragment);
                transaction.addToBackStack(null);
                transaction.commit();

                if (System.currentTimeMillis() > lastTimeBackPressed + 2000) {
                    lastTimeBackPressed = System.currentTimeMillis();
                    Toast.makeText(this, "뒤로 버튼 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();

                } else { // back 키 2번 누르면 앱 종료
                    finish();
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(0);
                }

            } else if (result == 0) {

            }

        } else if (fragment instanceof MainFragment) {
            if (System.currentTimeMillis() > lastTimeBackPressed + 2000) {
                lastTimeBackPressed = System.currentTimeMillis();
                Toast.makeText(this, "뒤로 버튼 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();

            } else { // back 키 2번 누르면 앱 종료
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);

            }
        }


    }

    // 블루투스 승인 요청 코드 (fragment_main 에 btn_onclick 사용)
    public void blueToothPairing(View view) {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);
    }

    public void backMainFragment(List<Address> addressList) {//서치 에서 메인프레그 먼트로 주소를 보내줄때 사용
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, mainFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        mainFragment.getSearchFragmentAddress(addressList);

    }


    public void addressConvert(View view) {
        Log.i(TAG, "MainActivity - addressConvert");
    }

    public Object getMainfragment() {//메인 프레그 먼트 생성
        Log.i(TAG, "MainActivity - getMainfragment");
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.fragment_container);
        if (fragment != mainFragment) {
            FragmentTransaction transaction = manager.beginTransaction();
            mainFragment = MainFragment.newInstance();
            transaction.replace(R.id.fragment_container, mainFragment);
            transaction.commit();
            Log.i(TAG, "main fragment call");
        }
        return mainFragment;
    }

    // SharedPreference 에 저장
    public void sharedPrefSave(String si, String gu, String gun) {
        SharedPreferences pref = getPreferences(MODE_PRIVATE);
        boolean result1 = pref.edit().putString("si", si).commit(); // SharedPreference 에 "시" 데이터 저장
        boolean result2 = pref.edit().putString("gu", gu).commit(); // SharedPreference 에 "구" 데이터 저장
        boolean result3 = pref.edit().putString("gun", gun) // SharedPreference 에 "군" 데이터 저장
                .commit();
    }

    // 저장된 SharedPreference 읽기
    List<Address> sharedPrefRead() {
        SharedPreferences pref = getPreferences(MODE_PRIVATE);
        String si = pref.getString("si", null);
        String gu = pref.getString("gu", null);
        String gun = pref.getString("gun", null);

        List<Address> list = new ArrayList<>();
        Address address = new Address(null);

        address.setLocality(si);
        address.setSubLocality(gu);
        address.setThoroughfare(gun);
        list.add(address);
        return list;
    }

    // TODO: 블루투스 페어링
}

