package edu.android.blueprint_project02;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements LocationFragment.LocationDeliverCallback {
    private TextView textLocation;
    private TextView textTime;
    private LineChart chart;
    private BackPressClose backPressClose;
    LocationFragment locationFragment = new LocationFragment();
    FrameLayout fragment;
    SwipeRefreshLayout layout;
    UsedAsync asyncTask;
    ProgressHandler handler;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초");
    String time;


    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textLocation = findViewById(R.id.textLocation);
        textTime = findViewById(R.id.textTime);
        chart = (LineChart) findViewById(R.id.chartValueEveryHour);

        layout = (SwipeRefreshLayout) findViewById(R.id.layout);
        layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                layout.setRefreshing(false);
            }
        });

        handler = new ProgressHandler();

        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(40f, 0));
        entries.add(new Entry(80f, 1));
        entries.add(new Entry(60f, 2));
        entries.add(new Entry(20f, 3));
        entries.add(new Entry(180f, 4));
        entries.add(new Entry(90f, 5));
        entries.add(new Entry(160f, 6));
        entries.add(new Entry(50f, 7));
        entries.add(new Entry(30f, 8));
        entries.add(new Entry(70f, 10));
        entries.add(new Entry(90f, 11));
        entries.add(new Entry(40f, 12));
        entries.add(new Entry(80f, 13));
        entries.add(new Entry(60f, 14));
        entries.add(new Entry(20f, 15));
        entries.add(new Entry(180f, 16));
        entries.add(new Entry(90f, 17));
        entries.add(new Entry(160f, 18));
        entries.add(new Entry(50f, 19));
        entries.add(new Entry(30f, 20));
        entries.add(new Entry(70f, 21));
        entries.add(new Entry(90f, 22));
        entries.add(new Entry(70f, 23));


        LineDataSet dataset = new LineDataSet(entries, "");

        ArrayList<String> labels = new ArrayList<String>();
        for (int i = 0; i < 10; i++)
            labels.add("0" + i + "시");
        for (int i = 10; i < 24; i++)
            labels.add(i + "시");


        LineData data = new LineData(labels, dataset);
        dataset.setDrawCubic(true); //선 둥글게 만들기
        dataset.setDrawFilled(true); //그래프 밑부분 색칠*/

        chart.setData(data);
        chart.animateY(5000);

        backPressClose = new BackPressClose(this);

//        Intent intent = getIntent();
//        String loc = intent.getStringExtra("location");
//        if (loc != null) {
//            textLocation.setText(loc);
//        }

        String location = "Gangnam-gu Yeoksam-dong";
        Bundle bundle = locationFragment.getArguments();
        if (bundle != null)
            location = bundle.getString("location");
        textLocation.setText(location);

        runTime();

    }

    private void runTime() {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        time = sdf.format(new Date(System.currentTimeMillis()));
                        Message message = handler.obtainMessage();
                        handler.sendMessage(message);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }
            }
        });
        th.start();
        asyncTask = new UsedAsync();
        asyncTask.execute();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragment = getSupportFragmentManager();
        FragmentTransaction transaction = fragment.beginTransaction();
        if (fragment.getBackStackEntryCount() == 1) {
            fragment.popBackStack();
            transaction.commit();
        } else {
            backPressClose.onBackPressed();
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment).addToBackStack(null).commit();

    }

    public void showLocationFragment(View view) {
        replaceFragment(locationFragment);
    }

    @Override
    public void onLocationDeliver(String location) {
        textLocation.setText(location);
    }


    private class UsedAsync extends AsyncTask<Integer, Integer, Integer> {
        Calendar calendar;
        String timeGre;

        @Override
        protected Integer doInBackground(Integer... integers) {
            while(isCancelled() == false) {
                calendar = new GregorianCalendar();
                TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");
                calendar.setTimeZone(timeZone);
                timeGre = String.format("%d %d %d %d %d %d", calendar.get(calendar.YEAR), calendar.get(calendar.MONTH)+1, calendar.get(calendar.DAY_OF_MONTH),calendar.get(calendar.HOUR),calendar.get(calendar.MINUTE),calendar.get(calendar.SECOND));

                publishProgress();
                try {
                    Thread.sleep(1000);
                } catch(InterruptedException e) {
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            calendar = new GregorianCalendar();
            timeGre = String.format("%d %d %d %d %d %d", calendar.get(calendar.YEAR), calendar.get(calendar.MONTH)+1, calendar.get(calendar.DAY_OF_MONTH),calendar.get(calendar.HOUR),calendar.get(calendar.MINUTE),calendar.get(calendar.SECOND));

        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }

    private class ProgressHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            textTime.setText(time);
            super.handleMessage(msg);
        }
    }
}