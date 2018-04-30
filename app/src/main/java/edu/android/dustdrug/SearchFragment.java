package edu.android.dustdrug;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import static edu.android.dustdrug.MainActivity.TAG;

public class SearchFragment extends Fragment {

    private OnFragmentInteractionListener mListener;


    public MainFragment mainFragment;
    RecyclerView recyclerView;
    ArrayList<CityList> cityLists;
    ArrayList<CityList2> cityList2s;
    ArrayList<CityList3> cityList3s;
    String param1;
    String param2;
    String param3;

    public SearchFragment() {
        // Required empty public constructor
    }

    public SearchFragment newInstance(String sido, String gugun, String eupmyeondong) {
        SearchFragment searchFragment = new SearchFragment();
        Bundle bundle = new Bundle(3);
        bundle.putString("sido", sido);
        bundle.putString("gugun", gugun);
        bundle.putString("eupmyeondong", eupmyeondong);
        MainFragment mainFragment = new MainFragment();
        mainFragment.setArguments(bundle);
        return searchFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        Context context = view.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), 1)); // 시 구분선
        Si si = new Si();
        si.execute();
        return view;
    }

    public ArrayList<CityList> getXmlData1() {// 도시 이름 데이터 가져오기
        ArrayList<CityList> lists = new ArrayList<>();
        String api1 = "http://openapi.epost.go.kr/postal/retrieveLotNumberAdressAreaCdService/retrieveLotNumberAdressAreaCdService/getBorodCityList?ServiceKey=2WjM1G6ETI%2F3HKoHrAC9MhjgY3PufrijH35VWAgVnh3A5ZrEkBkXovDVizsiQoKm7FDHO2AmW4LG%2FA2oiF8new%3D%3D";
        Log.i("s1", "트라이위");
        try {
            URL url = new URL(api1); // 문자열로 된 요청 totalUrl 을 URL 객체로 생성.
            Log.i("s1", api1);
            InputStream is = url.openStream(); // url 위치로 InputStream 연결

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new InputStreamReader(is)); //InputStream 으로부터 xml 입력받음
            String tag = null;
            xpp.next();
            int eventType = xpp.getEventType();
            CityList cityList = new CityList();
//            Log.i("s1","파싱S");
            while (eventType != XmlPullParser.END_DOCUMENT) {

//                Log.i("s1","와일");
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT://파싱 시작
//                        Log.i("s1","시작 ");
                        break;

                    case XmlPullParser.START_TAG:
                        tag = xpp.getName();//태그 이름 얻어오기

                        if (tag.equals("brtcNm")) {
                            Log.i("s1", "nm");
                            xpp.next();
                            Log.i("s1", xpp.getText() + "1");
                            cityList.setBrtcNm(xpp.getText());
                            Log.i("s1", cityList.getBrtcNm() + "2");


                        } else if (tag.equals("brtcCd")) {
                            Log.i("s1", "cd");
                            xpp.next();
                            Log.i("s1", xpp.getText() + "1");
                            cityList.setBrtcCd(xpp.getText());
                            Log.i("s1", cityList.getBrtcCd() + "2");
                        }
//                        Log.i("s1","Start Tag");
                        break;
                    case XmlPullParser.TEXT:
//                        Log.i("s1","pull Parser");
                        break;

                    case XmlPullParser.END_TAG:
                        Log.i("s1", "endTag");
//                        Log.i("s1",cityList.getBrtcCd());
                        tag = xpp.getName(); // 태그 이름 얻어오기
//                        Log.i("s1",tag);
                        if (tag.equals("borodCity")) {

                            Log.i("s1", "저장");
                            lists.add(cityList);
                            Log.i("s1", cityList.getBrtcCd());
                            Log.i("s1", cityList.getBrtcNm());
                            cityList = new CityList();
                        }
                        break;
                }
                eventType = xpp.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return lists;
    }


    private class CityList {
        String brtcNm;
        String brtcCd;

        public String getBrtcNm() {
            return brtcNm;
        }

        public void setBrtcNm(String brtcNm) {
            this.brtcNm = brtcNm;
        }

        public String getBrtcCd() {
            return brtcCd;
        }

        public void setBrtcCd(String brtcCd) {
            this.brtcCd = brtcCd;
        }
    } // 도시 이름 리스트 끝

    private class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {//리사이클러뷰 내용추가

        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {//뷰홀더 생성
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View itemView = inflater.inflate(
                    android.R.layout.simple_list_item_1,
                    parent, false);

            ItemViewHolder holder = new ItemViewHolder(itemView);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {//뷰홀더의 객채에 적용될 UI
            holder.textView.setText(cityLists.get(position).brtcNm);
        }

        @Override
        public int getItemCount() {//리스트 아이탬의 갯수
            return cityLists.size();
        }

        class ItemViewHolder extends RecyclerView.ViewHolder {// 뷰홀더 만듬
            TextView textView;

            public ItemViewHolder(final View itemView) {
                super(itemView);
                textView = (TextView) itemView;
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,25);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {//리스트에서 적용
                        recyclerView.addItemDecoration(new DividerItemDecoration(itemView.getContext(), 1)); // recyclerView 구,동 구분선
                        param1 = textView.getText().toString();
                        Gugun Gugun = new Gugun();
                        Gugun.execute(param1);

                    }
                });

            }
        }
    }//리사이클러뷰 내용추가 끝

    class Si extends AsyncTask<Void, Void, Void> {//어씽크 테스트 클래스

        @Override
        protected Void doInBackground(Void... voids) {
            cityLists = getXmlData1();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ItemAdapter adapter = new ItemAdapter();
            recyclerView.setAdapter(adapter);
        }
    }//어씽크 끝

    public ArrayList<CityList2> getXmlData2(String cityname) {//시도 xml 받아오기
        String api2 = "http://openapi.epost.go.kr/postal/retrieveLotNumberAdressAreaCdService/retrieveLotNumberAdressAreaCdService/getSiGunGuList?ServiceKey=2WjM1G6ETI%2F3HKoHrAC9MhjgY3PufrijH35VWAgVnh3A5ZrEkBkXovDVizsiQoKm7FDHO2AmW4LG%2FA2oiF8new%3D%3D&brtcCd=" + cityname;
        ArrayList<CityList2> cityList2s = new ArrayList<>();
        try {
            URL url = new URL(api2); // 문자열로 된 요청 totalUrl 을 URL 객체로 생성.
//            Log.i("s1",api2);
            InputStream is = url.openStream(); // url 위치로 InputStream 연결
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new InputStreamReader(is)); //InputStream 으로부터 xml 입력받음
            String tag = null;
            xpp.next();
            int eventType = xpp.getEventType();
            CityList2 cityList2 = new CityList2();
//            Log.i("s1","파싱S");
            while (eventType != XmlPullParser.END_DOCUMENT) {

//                Log.i("s1","와일");
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT://파싱 시작
//                        Log.i("s1","시작 ");
                        break;

                    case XmlPullParser.START_TAG:
                        tag = xpp.getName();//태그 이름 얻어오기

                        if (tag.equals("signguCd")) {
//                            Log.i("s1", "nm");
                            xpp.next();
                            cityList2.setName(xpp.getText());
                        }
                        break;
                    case XmlPullParser.TEXT:
//                        Log.i("s1","pull Parser");
                        break;

                    case XmlPullParser.END_TAG:
//                        Log.i("s1","endTag");
//                        Log.i("s1",cityList.getBrtcCd());
                        tag = xpp.getName(); // 태그 이름 얻어오기
//                        Log.i("s1",tag);
                        if (tag.equals("siGunGuList")) {
//                            Log.i("s1","저장");
                            cityList2s.add(cityList2);
                            cityList2 = new CityList2();
                        }
                        break;
                }
                eventType = xpp.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return cityList2s;
    }

    class CityList2 {
        String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }//시도 xml 받아오기끝

    class Gugun extends AsyncTask<String, Void, Void> {//받아온 list 쓰기

        @Override
        protected Void doInBackground(String... strings) {
            cityList2s = getXmlData2(strings[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ItemAdapter2 adapter2 = new ItemAdapter2();
            recyclerView.setAdapter(adapter2);

        }
    }

    private class ItemAdapter2 extends RecyclerView.Adapter<ItemAdapter2.ItemViewHolder2> {
        @NonNull
        @Override
        public ItemAdapter2.ItemViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {//뷰홀더 생성
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View itemView = inflater.inflate(
                    android.R.layout.simple_list_item_1,
                    parent, false);

            ItemAdapter2.ItemViewHolder2 holder = new ItemAdapter2.ItemViewHolder2(itemView);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ItemAdapter2.ItemViewHolder2 holder, int position) {// 리스트 각 객채 추가
            holder.textView.setText(cityList2s.get(position).getName());
        }

        @Override
        public int getItemCount() {//총리스트 수
            return cityList2s.size();
        }

        class ItemViewHolder2 extends RecyclerView.ViewHolder {
            TextView textView;
            public ItemViewHolder2(View itemView) {
                super(itemView);
                textView = (TextView) itemView;
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,25);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //TODO  눌렀을때 액션 추가
                        EupMyeonDong eupMyeonDong = new EupMyeonDong();
                        param2 = textView.getText().toString();
                        String[] fuckYours = {param1, param2};
                        eupMyeonDong.execute(fuckYours);
                    }
                });

            }
        }
    }

    public ArrayList<CityList3> getXmlData3(String cityname, String sigunguname) {//동 xml 받아오기
        String api3 = "http://openapi.epost.go.kr/postal/retrieveLotNumberAdressAreaCdService/retrieveLotNumberAdressAreaCdService/getEupMyunDongList?ServiceKey=2WjM1G6ETI%2F3HKoHrAC9MhjgY3PufrijH35VWAgVnh3A5ZrEkBkXovDVizsiQoKm7FDHO2AmW4LG%2FA2oiF8new%3D%3D&brtcCd=" + cityname + "&signguCd=" + sigunguname;
        Log.i("s1", api3);
        ArrayList<CityList3> cityList3s = new ArrayList<>();
        try {
            URL url = new URL(api3); // 문자열로 된 요청 totalUrl 을 URL 객체로 생성.
//            Log.i("s1",api2);
            InputStream is = url.openStream(); // url 위치로 InputStream 연결
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new InputStreamReader(is)); //InputStream 으로부터 xml 입력받음
            String tag = null;
            xpp.next();
            int eventType = xpp.getEventType();
            CityList3 cityList3 = new CityList3();
//            Log.i("s1","파싱S");
            while (eventType != XmlPullParser.END_DOCUMENT) {

//                Log.i("s1","와일");
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT://파싱 시작
//                        Log.i("s1","시작 ");
                        break;

                    case XmlPullParser.START_TAG:
                        tag = xpp.getName();//태그 이름 얻어오기

                        if (tag.equals("emdCd")) {
//                            Log.i("s1", "nm");
                            xpp.next();
                            cityList3.setName(xpp.getText());
                        }
                        break;
                    case XmlPullParser.TEXT:
//                        Log.i("s1","pull Parser");
                        break;

                    case XmlPullParser.END_TAG:
//                        Log.i("s1","endTag");
//                        Log.i("s1",cityList.getBrtcCd());
                        tag = xpp.getName(); // 태그 이름 얻어오기
//                        Log.i("s1",tag);
                        if (tag.equals("eupMyunDongList")) {
//                            Log.i("s1","저장");
                            cityList3s.add(cityList3);
                            cityList3 = new CityList3();
                        }
                        break;
                }
                eventType = xpp.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return cityList3s;
    }

    class CityList3 {
        String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }//읍/면/동 xml 받아오기끝

    class EupMyeonDong extends AsyncTask<String[], Void, Void> {//받아온 list 쓰기

        @Override
        protected Void doInBackground(String[]... strings) {
            //TODO
            cityList3s = getXmlData3(strings[0][0], strings[0][1]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ItemAdapter3 adapter3 = new ItemAdapter3();
            recyclerView.setAdapter(adapter3);

        }
    }

    private class ItemAdapter3 extends RecyclerView.Adapter<ItemAdapter3.ItemViewHolder3> {
        @NonNull
        @Override
        public ItemAdapter3.ItemViewHolder3 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {//뷰홀더 생성
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View itemView = inflater.inflate(
                    android.R.layout.simple_list_item_1,
                    parent, false);

            ItemAdapter3.ItemViewHolder3 holder = new ItemAdapter3.ItemViewHolder3(itemView);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ItemAdapter3.ItemViewHolder3 holder, int position) {// 리스트 각 객채 추가
            holder.textView.setText(cityList3s.get(position).getName());
        }

        @Override
        public int getItemCount() {//총리스트 수
            return cityList3s.size();
        }

        class ItemViewHolder3 extends RecyclerView.ViewHolder {
            TextView textView;

            public ItemViewHolder3(View itemView) {
                super(itemView);
                textView = (TextView) itemView;
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,25);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //TODO  눌렀을때 액션 추가
                        param3 = textView.getText().toString();

                    }
                });

            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
