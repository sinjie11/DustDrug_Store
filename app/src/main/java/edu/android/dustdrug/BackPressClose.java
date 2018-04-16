package edu.android.dustdrug;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.widget.Toast;

public class BackPressClose {

    private long backKeyPressedTime = 0;
    private Toast toast;
    private Fragment fragment;
    private Activity activity;

    public BackPressClose(Activity activity) {
        this.activity = activity;

    }

    public void onBackPressed() {

        if (isAfter1Second()) {
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(fragment.getContext(), "한 번 더 눌러 앱을 종료합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isBefore1Second()) {
            programShutDown();
            toast.cancel();
        }
    }

    private Boolean isAfter1Second() {
        return System.currentTimeMillis() > backKeyPressedTime + 1000;
    }

    private Boolean isBefore1Second() {
        return System.currentTimeMillis() <= backKeyPressedTime + 1000;
    }

    private void programShutDown() {
        activity.moveTaskToBack(true);
        activity.finish();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
}
