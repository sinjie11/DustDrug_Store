package edu.android.dustdrug;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import static edu.android.dustdrug.MainActivity.TAG;

public class SearchFragment extends Fragment {

    private Spinner spinner;
    private Spinner spinner1;
    private String sido; // 서울(특별시), 광역시, 전국8도
    private String sigu; // i) 서울, 광역시에 각각 배속된 구 ii)도에 배속된 중소도시

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance() {
        SearchFragment searchFragment = new SearchFragment();
        return searchFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        spinner = view.findViewById(R.id.spinner);
        spinner1 = view.findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.sido_array, android.R.layout.simple_spinner_item);
        spinner.setPrompt("시.도를 선택하십시오.");
        spinner.setAdapter(new NothingSelectedSpinnerAdapter(
                adapter, R.layout.sido_spinner_row_nothing_selected, getContext()
        ));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sido = ((TextView) view).getText().toString();
                Log.i(TAG, "sido : " + sido);
                if(sido.equals("서울")) {
                    ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getContext(), R.array.seoul_gu_array, android.R.layout.simple_spinner_item);
                    spinner1.setPrompt("구를 선택하십시오.");
                    spinner1.setAdapter(new NothingSelectedSpinnerAdapter(
                            adapter1, R.layout.sigu_spinner_row_nothing_selected, getContext()
                    ));

                    spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            sigu = ((TextView) view).getText().toString();
                            Log.i(TAG, "sigu : " + sigu);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                }

                else if(sido.equals("광주")) {
                    ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getContext(), R.array.gwangju_gu_array, android.R.layout.simple_spinner_item);
                    spinner1.setPrompt("구를 선택하십시오.");
                    spinner1.setAdapter(new NothingSelectedSpinnerAdapter(
                            adapter1, R.layout.sigu_spinner_row_nothing_selected, getContext()
                    ));

                    spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            sigu = ((TextView) view).getText().toString();
                            Log.i(TAG, "sigu : " + sigu);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


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

//    public void showGuSpinner(int textArrayResId) {  // 해당 string-array ID를 파라미터로 받아 고유의 스피너 생성하는 메소드 (구)
//        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getContext(), textArrayResId, android.R.layout.simple_spinner_item);
//        spinner1.setPrompt("구를 선택하십시오.");
//        spinner1.setAdapter(new NothingSelectedSpinnerAdapter(
//                adapter1, R.layout.sido_spinner_row_nothing_selected, getContext()
//        ));
//
//        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                sigu = ((TextView) view).getText().toString();
//                Log.i(TAG, "sigu : " + sigu);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
//    }

//    public void showSiSpinner(int textArrayResId) {  // 해당 string-array ID를 파라미터로 받아 고유의 스피너 생성하는 메소드 (전국8도에 각각 배속된 중소도시)
//        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getContext(), textArrayResId, android.R.layout.simple_spinner_item);
//        spinner1.setPrompt("시를 선택하십시오.");
//        spinner1.setAdapter(new NothingSelectedSpinnerAdapter(
//                adapter1, R.layout.sido_spinner_row_nothing_selected, getContext()
//        ));
//
//        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                sigu = ((TextView) view).getText().toString();
//                Log.i(TAG, "sigu : " + sigu);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
//    }

}
