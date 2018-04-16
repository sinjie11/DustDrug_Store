package edu.android.dustdrug;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    private MainActivity mainActivity;

    private MainFragment mainFragment;
    private LineChart lineChart; // 그래프(jar 파일 사용)

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        lineChart = view.findViewById(R.id.chartValueEveryHour);

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
        dataset.setDrawFilled(true); //그래프 밑부분 색칠

        lineChart.setData(data);
        lineChart.animateY(5000);

        view.setFocusableInTouchMode(true);
        view.requestFocus();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
