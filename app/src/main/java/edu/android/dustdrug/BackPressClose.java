package edu.android.dustdrug;

import android.app.Activity;
import android.widget.Toast;

public class BackPressClose {
    private long backKeyPressedTime = 0;
    private Toast toast;
    private Activity activity;
    private LocationFragment locationFragment;

    public BackPressClose(Activity activity) {
        this.activity = activity;
    }

    public void onBackPressed() {
        if(isAfter1Second()) {
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(activity, "\'뒤로\'버튼을 한 번 더 누르시면 종료됩니다", Toast.LENGTH_SHORT).show();
            return;
        }

        if(isBefore1Second()) {
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
