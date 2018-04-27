package edu.android.dustdrug;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.ServiceConfigurationError;
import java.util.Set;


public class MainActivity extends AppCompatActivity {
    private Toast toast;
    public static final String TAG = "edu.android";
    //    private static final int REQUEST_ENABLE_BLUETOOTH = 3;
//    private BluetoothAdapter bluetoothAdapter;
    private Fragment fragment;
    private long lastTimeBackPressed = 0;
    private MainFragment mainFragment = new MainFragment();
    private SearchFragment searchFragment = new SearchFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        FragmentManager manager = getSupportFragmentManager();
//        Fragment fragment = manager.findFragmentById(R.id.fragment_container);
//        if (fragment != null) {
//            FragmentTransaction transaction = manager.beginTransaction();
//            firstFragment = FirstFragment.newInstance();
//            transaction.replace(R.id.fragment_container, firstFragment);
//            transaction.commit();
//            Log.i(TAG, "first fragment call");
//        }

//        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        if (bluetoothAdapter == null) {
//            Toast.makeText(this, "블루투스를 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
//
//            /**  에뮬레이터 죽음  그래서 아래코드 주석처리함*/
//            finish();
//            return;
//        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        // 필요없음.........;;;;
        // public void blueToothPairing(View view) 쓰면 권한 주는 단계 뛰어 넘어짐
//        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        if(bluetoothAdapter == null) {
//            Toast.makeText(this, "블루투스를 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
//        }
//
//        if(!bluetoothAdapter.isEnabled()) { // insert ! in front of bluetoothadapter.isENnabled
//            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
//        }
    }

    public void switchFragment(Fragment fragment) {
        //        FragmentManager manager = getSupportFragmentManager();
//        Fragment fragment = manager.findFragmentById(R.id.fragment_container);
//        if (fragment != null) {
//            FragmentTransaction transaction = manager.beginTransaction();
//            firstFragment = FirstFragment.newInstance();
//            transaction.replace(R.id.fragment_container, firstFragment);
//            transaction.commit();
//            Log.i(TAG, "first fragment call");
//        }
    }


    /**
     *  Back 버튼 누를 시 이전화면 및 앱 종료 기능
     */
    @Override
    public void onBackPressed() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, mainFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        if (System.currentTimeMillis() > lastTimeBackPressed + 2000) {
            lastTimeBackPressed = System.currentTimeMillis();
            Toast.makeText(this, "뒤로 버튼 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
            return;
        } else { // back 키 2번 누르면 앱 종료
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }

    }


    /**
     *   - 블루투스 승인 요청 코드
     *   - fragment_main에 btn_onclick 사용
     */
    public void blueToothPairing(View view) {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);
    }

}
