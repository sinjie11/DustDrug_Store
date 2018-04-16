package edu.android.dustdrug;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class FirstActivity extends AppCompatActivity {

    public int cnt = 0;
    private TextView textView;
    private Thread loadingThread;

    public FirstActivity() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        textView = findViewById(R.id.textView);
        loadingThread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    if (cnt == 5)
                        break;
                    else {
                        if (cnt % 4 == 0) {
                            textView.setText("Loading   ");
                            cnt++;
                        } else if (cnt % 4 == 1) {
                            textView.setText("Loading.  ");
                            cnt++;
                        } else if (cnt % 4 == 2) {
                            textView.setText("Loading.. ");
                            cnt++;
                        } else {
                            textView.setText("Loading...");
                            cnt++;
                        }
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Intent intent = new Intent(FirstActivity.this, MainActivity.class);
                startActivity(intent);
            }
        };
        loadingThread.start();
    }
}
