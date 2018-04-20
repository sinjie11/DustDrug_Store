package edu.android.dustdrug;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    private MainFragment mainFragment;
    private FirstFragment firstFragment;
    private long lastTimeBackPressed = 0;
    int x = 0;// 쓰레드 테스트용
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


    }



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
    public void getMainfragment(){

        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.fragment_container);
        FragmentTransaction transaction = manager.beginTransaction();
        mainFragment = MainFragment.newInstance();
        transaction.replace(R.id.fragment_container, mainFragment);
        transaction.commit();

    }
    public void lodingUpdate(){// 로딩 넘버 올려줌
        int x = 0;
        if (x == 0) {
            firstFragment.onLoding0();
            x++;
        } else if (x == 1) {
            firstFragment.onLoding1();
            x++;
        } else if (x == 2) {
            firstFragment.onLoding2();
            x++;
        } else if (x == 3) {
            firstFragment.onLoding3();
            x++;
        } else if (x == 4) {
            firstFragment.onLoding4();
            x++;
        } else if (x == 5) {
            firstFragment.endLoding();
        }
    }

}