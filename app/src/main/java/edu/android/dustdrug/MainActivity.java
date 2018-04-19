package edu.android.dustdrug;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "mainactivity";
    private static final int REQUEST_ENABLE_BLUETOOTH = 3;
    private BluetoothAdapter bluetoothAdapter;
    private FirstFragment firstFragment;
    private long lastTimeBackPressed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.fragment_container);
        if(fragment == null) {
            FragmentTransaction transaction = manager.beginTransaction();
            firstFragment = FirstFragment.newInstance();
            transaction.replace(R.id.fragment_container, firstFragment);
            transaction.commit();
            Log.i(TAG, "first fragment call");
        }

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null) {
            Toast.makeText(this, "블루투스를 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
//            finish();
//            return;
        }
        //TODO:
//        public boolean getDeviceState() {
//            Log.d(TAG, "Check the Bluetooth support");
//            if(bluetoothAdapter == null) {
//                Log.d(TAG, "Bluetooth is not available");
//                return false;
//            } else { Log.d(TAG, "Bluetooth is available");
//                return true;
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        if(bluetoothAdapter == null) {
//            Toast.makeText(this, "블루투스를 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
//        }
        if(!bluetoothAdapter.isEnabled()) { // insert ! in front of bluetoothadapter.isENnabled
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
        }
    }
    
    
    // TODO: 
//     Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//     discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
//     startActivity(discoverableIntent);
    
    
    /* ↓ Back 버튼 누를 시 앱 종료 기능 */
    @Override
    public void onBackPressed() {

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

}
