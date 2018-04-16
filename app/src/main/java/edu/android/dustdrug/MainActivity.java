package edu.android.dustdrug;

import android.content.pm.PackageManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private long backKeyPressedTime = 0;
    private long pressedTime;
    private MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainFragment = new MainFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, mainFragment).addToBackStack(null).commit();
    }

    @Override
    public void onBackPressed() {
        if(isAfter2Seconds()) {
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "한 번 더 눌러 앱을 종료합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(isBefore2Seconds()) {
            programShutDown();
        }
    }

    private Boolean isAfter2Seconds() {
        return System.currentTimeMillis() > backKeyPressedTime + 2000;
    }

    private Boolean isBefore2Seconds() {
        return System.currentTimeMillis() <= backKeyPressedTime + 2000;
    }

    public void programShutDown() {
        moveTaskToBack(true);
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
}