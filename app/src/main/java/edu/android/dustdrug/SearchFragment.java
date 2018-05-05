package edu.android.dustdrug;

import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    public static final String TAG = "edu.android";

    private OnFragmentInteractionListener mListener;
    private DustDrugDAOImple dustDrugDAOImple;
    private List<Address> list;
    public MainFragment mainFragment;
    private Address address;
    private MainActivity mainActivity;
    RecyclerView recyclerView;
    ArrayList<CityList> cityLists;
    ArrayList<CityList2> cityList2s;
    ArrayList<CityList3> cityList3s;
    ArrayList<AirQulity_API.GetAPIGsonTM.List> lists;
    String param1;
    String param2;
    String param3;
    EditText editText;
    AirQulity_API airQulity_api;
    Button button;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainActivity = (MainActivity) getActivity();
        dustDrugDAOImple = dustDrugDAOImple.getInstence();
        list = new ArrayList<>();

        View view = inflater.inflate(R.layout.fragment_search, container, false);
        editText = view.findViewById(R.id.editText);
        recyclerView = view.findViewById(R.id.recyclerView);

        editText.setTextColor(Color.parseColor("#FFFFFF"));

        recyclerView.setHasFixedSize(true);
        Context context = view.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), 1)); // 시 구분선

        Si si = new Si();
        si.execute();

        button = view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllItemSearch allItemSearch = new AllItemSearch();
                allItemSearch.execute();
            }
        });

        //살리고 싶지만 트래픽 때문에 어쩔수 없이 죽인 채팅 치는 와중에 검색되는 리스너
//        editText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (s.toString().length() >= 2) {
//                    IsFuckingCandy isFuckingCandy = new IsFuckingCandy();
//                    isFuckingCandy.execute();
//                }
//            }
//        });
        return view;
    }

    public ArrayList<CityList> getXmlData1() {// 도시 이름 데이터 가져오기
        ArrayList<CityList> lists = new ArrayList<>();
        
        String api1 = "http://openapi.epost.go.kr/postal/retrieveLotNumberAdressAreaCdService/retrieveLotNumberAdressAreaCdService/getBorodCityList?ServiceKey=2WjM1G6ETI%2F3HKoHrAC9MhjgY3PufrijH35VWAgVnh3A5ZrEkBkXovDVizsiQoKm7FDHO2AmW4LG%2FA2oiF8new%3D%3D";

        try {
            URL url = new URL(api1); // 문자열로 된 요청 totalUrl 을 URL 객체로 생성.

            InputStream is = url.openStream(); // url 위치로 InputStream 연결

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();

            xpp.setInput(new InputStreamReader(is)); // InputStream 으로부터 xml 입력받음

            String tag = null;

            xpp.next();

            int eventType = xpp.getEventType();
            CityList cityList = new CityList();

            while (eventType != XmlPullParser.END_DOCUMENT) {

                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT: // Parsing Start
                        break;

                    case XmlPullParser.START_TAG:
                        tag = xpp.getName(); // tag 이름 얻어오기

                        if (tag.equals("brtcNm")) {
                            xpp.next();
                            String cityName = changeCityName(xpp.getText());
                            cityList.setBrtcNm(cityName);

                        } else if (tag.equals("brtcCd")) {
                            xpp.next();
                            cityList.setBrtcCd(xpp.getText());
                        }

                        break;
                    case XmlPullParser.TEXT:
                        break;

                    case XmlPullParser.END_TAG:
                        tag = xpp.getName();

                        if (tag.equals("borodCity")) {
                            lists.add(cityList);
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

    private class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> { // RecyclerView 내용추가

        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { // ViewHolder 생성
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View itemView = inflater.inflate(
                    android.R.layout.simple_list_item_1,
                    parent, false);

            ItemViewHolder holder = new ItemViewHolder(itemView);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) { // ViewHolder 객채에 적용될 UI
            holder.textView.setText(cityLists.get(position).brtcNm);
            holder.textView.setTextColor(Color.WHITE);
        }

        @Override
        public int getItemCount() {//리스트 아이탬의 갯수
            return cityLists.size();
        }

        class ItemViewHolder extends RecyclerView.ViewHolder { // ViewHolder 만듬
            TextView textView;

            public ItemViewHolder(final View itemView) {
                super(itemView);
                textView = (TextView) itemView;
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { // 리스트 에서 적용
                        recyclerView.addItemDecoration(new DividerItemDecoration(itemView.getContext(), 1)); // recyclerView 구,동 구분선
                        param1 = textView.getText().toString();
                        address = new Address(null);

                        if (param1.equals("세종특별자치시")) {
                            EupMyeonDong eupMyeonDong = new EupMyeonDong();
                            String[] fuckYours = {param1, ""};
                            eupMyeonDong.execute(fuckYours);
                            address.setLocality(param1);
                            address.setSubLocality("세종시");

                        } else {
                            Gugun Gugun = new Gugun();
                            Gugun.execute(param1);
                            address.setLocality(param1);
                        }
                    }
                });

            }
        }
    } // RecyclerView 내용추가 끝

    class Si extends AsyncTask<Void, Void, Void> { // "시" AsyncTask 클래스

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
    } // end Si AsyncTask

    public ArrayList<CityList2> getXmlData2(String cityname) { // 시, 도 xml 받아오기
        String cityName = changeCityName(cityname);
        
        cityName = uftConvertStr(cityName);
        
        String api2 = "http://openapi.epost.go.kr/postal/retrieveLotNumberAdressAreaCdService/retrieveLotNumberAdressAreaCdService/getSiGunGuList?ServiceKey=2WjM1G6ETI%2F3HKoHrAC9MhjgY3PufrijH35VWAgVnh3A5ZrEkBkXovDVizsiQoKm7FDHO2AmW4LG%2FA2oiF8new%3D%3D&brtcCd=" + cityName;
        
        ArrayList<CityList2> cityList2s = new ArrayList<>();
        
        try {
            URL url = new URL(api2); // 문자열로 된 요청 totalUrl 을 URL 객체로 생성.

            InputStream is = url.openStream(); // url 위치로 InputStream 연결
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();

            xpp.setInput(new InputStreamReader(is)); // InputStream 으로부터 xml 입력받음

            String tag = null;

            xpp.next();

            int eventType = xpp.getEventType();

            CityList2 cityList2 = new CityList2();

            int x = 0;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                x++;
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT: // Parsing 시작
                        break;

                    case XmlPullParser.START_TAG:
                        tag = xpp.getName(); // tag 이름 얻어오기
                        if (tag.equals("signguCd")) {
                            xpp.next();
                            cityList2.setName(xpp.getText());
                        }
                        break;

                    case XmlPullParser.TEXT:
                        break;

                    case XmlPullParser.END_TAG:
                        tag = xpp.getName();

                        if (tag.equals("siGunGuList")) {
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
    } // 시, 도 xml 받아오기끝

    class Gugun extends AsyncTask<String, Void, Void> { // 받아 온 list 쓰기

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
        public ItemAdapter2.ItemViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { // ViewHolder 생성
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View itemView = inflater.inflate(
                    android.R.layout.simple_list_item_1,
                    parent, false);

            ItemAdapter2.ItemViewHolder2 holder = new ItemAdapter2.ItemViewHolder2(itemView);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ItemAdapter2.ItemViewHolder2 holder, int position) { // List 각 객채 추가
            holder.textView.setText(cityList2s.get(position).getName());
            holder.textView.setTextColor(Color.WHITE);
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
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EupMyeonDong eupMyeonDong = new EupMyeonDong();
                        param2 = textView.getText().toString();
                        String[] fuckYours = {param1, param2};
                        eupMyeonDong.execute(fuckYours);
                        address.setSubLocality(param2);
                    }
                });

            }
        }
    }

    public ArrayList<CityList3> getXmlData3(String cityname, String sigunguname) {//동 xml 받아오기
        String cityName = changeCityName(cityname);
        cityName = uftConvertStr(cityName);
        sigunguname = uftConvertStr(sigunguname);

        String api3 = "http://openapi.epost.go.kr/postal/retrieveLotNumberAdressAreaCdService/retrieveLotNumberAdressAreaCdService/getEupMyunDongList?ServiceKey=2WjM1G6ETI%2F3HKoHrAC9MhjgY3PufrijH35VWAgVnh3A5ZrEkBkXovDVizsiQoKm7FDHO2AmW4LG%2FA2oiF8new%3D%3D&brtcCd=" + cityName + "&signguCd=" + sigunguname;

        ArrayList<CityList3> cityList3s = new ArrayList<>();

        try {
            URL url = new URL(api3); // 문자열로 된 요청 totalUrl 을 URL 객체로 생성.

            InputStream is = url.openStream(); // url 위치로 InputStream 연결
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();

            xpp.setInput(new InputStreamReader(is)); //InputStream 으로부터 xml 입력받음

            String tag = null;

            xpp.next();

            int eventType = xpp.getEventType();

            CityList3 cityList3 = new CityList3();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT: // Parsing Start
                        break;

                    case XmlPullParser.START_TAG:
                        tag = xpp.getName(); // tag 이름 얻어오기

                        if (tag.equals("emdCd")) {
                            xpp.next();
                            cityList3.setName(xpp.getText());
                        }
                        break;
                    case XmlPullParser.TEXT:
                        break;

                    case XmlPullParser.END_TAG:
                        tag = xpp.getName();

                        if (tag.equals("eupMyunDongList")) {
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
    } //읍/면/동 xml 받아오기끝

    class EupMyeonDong extends AsyncTask<String[], Void, Void> { // 받아 온 list 쓰기

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
        public ItemAdapter3.ItemViewHolder3 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { // ViewHolder 생성
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View itemView = inflater.inflate(
                    android.R.layout.simple_list_item_1,
                    parent, false);

            ItemAdapter3.ItemViewHolder3 holder = new ItemAdapter3.ItemViewHolder3(itemView);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ItemAdapter3.ItemViewHolder3 holder, int position) { // List 에 각 객체 추가
            holder.textView.setText(cityList3s.get(position).getName());
            holder.textView.setTextColor(Color.WHITE);
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
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        param3 = textView.getText().toString();
                        address.setThoroughfare(param3);
                        Log.i(TAG, address.getLocality());

                        list.add(address);
                        mainActivity.backMainFragment(list);
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

    String changeCityName(String name) {
        if (name.equals("서울")) {
            name = "서울특별시";
        } else if (name.equals("서울특별시")) {
            name = "서울";
        } else if (name.equals("세종")) {
            name = "세종특별자치시";
        } else if (name.equals("세종특별자치시")) {
            name = "세종";
        } else if (name.equals("전남")) {
            name = "전라남도";
        } else if (name.equals("전라남도")) {
            name = "전남";
        } else if (name.equals("전북")) {
            name = "전라북도";
        } else if (name.equals("전라북도")) {
            name = "전북";
        } else if (name.equals("충남")) {
            name = "충청남도";
        } else if (name.equals("충청남도")) {
            name = "충남";
        } else if (name.equals("충북")) {
            name = "충청북도";
        } else if (name.equals("충청북도")) {
            name = "충북";
        } else if (name.equals("강원")) {
            name = "강원도";
        } else if (name.equals("강원도")) {
            name = "강원";
        } else if (name.equals("인천")) {
            name = "인천광역시";
        } else if (name.equals("인천광역시")) {
            name = "인천";
        } else if (name.equals("경기")) {
            name = "경기도";
        } else if (name.equals("경기도")) {
            name = "경기";
        } else if (name.equals("경남")) {
            name = "경상남도";
        } else if (name.equals("경상남도")) {
            name = "경남";
        } else if (name.equals("경북")) {
            name = "경상북도";
        } else if (name.equals("경상북도")) {
            name = "경북";
        } else if (name.equals("대전")) {
            name = "대전광역시";
        } else if (name.equals("대전광역시")) {
            name = "대전";
        } else if (name.equals("대구")) {
            name = "대구광역시";
        } else if (name.equals("대구광역시")) {
            name = "대구";
        } else if (name.equals("울산")) {
            name = "울산광역시";
        } else if (name.equals("울산광역시")) {
            name = "울산";
        } else if (name.equals("광주")) {
            name = "광주광역시";
        } else if (name.equals("광주광역시")) {
            name = "광주";
        } else if (name.equals("부산")) {
            name = "부산광역시";
        } else if (name.equals("부산광역시")) {
            name = "부산";
        } else if (name.equals("제주")) {
            name = "제주특별자치도";
        } else if (name.equals("제주특별자치도")) {
            name = "제주";
        }
        return name;
    }

    public int backIsClick() { // back 키 눌렀을때 이전단계로 돌려주는 메서드

        if (param1 == null) {
            return 1;
        } else if (param1 != null && param2 == null) {
            Si si = new Si();
            si.execute();
            param1 = null;
            return 0;

        } else if (param2 != null) {
            Gugun Gugun = new Gugun();
            Gugun.execute(param1);
            param2 = null;
            return 0;

        } else {
            return 0;
        }
    }

    public String uftConvertStr(String cityName) { // String 변환 , UTF-8 로 변환 하는 메소드
        String cityNameEncoded = null;
        try {
            cityNameEncoded = URLEncoder.encode(cityName, "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return cityNameEncoded;
    }

    public ArrayList<AirQulity_API.GetAPIGsonTM.List> searchListCity(String listCity) {//검색 "시" 리스트 받아옴
        airQulity_api = new AirQulity_API();
        ArrayList<AirQulity_API.GetAPIGsonTM.List> lists = airQulity_api.getChangeTm(listCity);
        return lists;
    }

    class AllItemSearch extends AsyncTask { // 검색 item 클래스

        @Override
        protected Object doInBackground(Object[] objects) {
            lists = searchListCity(editText.getText().toString());
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            SearchItem searchItem = new SearchItem();
            recyclerView.setAdapter(searchItem);
        }
    }

    class SearchItem extends RecyclerView.Adapter<SearchItemViewHolder> { // 검색 RecyclerView Adapter

        @NonNull
        @Override
        public SearchItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { // ViewHolder 생성
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View itemView = inflater.inflate(
                    android.R.layout.simple_list_item_1,
                    parent, false);

            SearchItemViewHolder searchItemViewHolder = new SearchItemViewHolder(itemView);
            return searchItemViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull SearchItemViewHolder holder, int position) { // List 에 객체 추가
            holder.textView.setText(lists.get(position).getSidoName() + " " + lists.get(position).getSggName() + " " + lists.get(position).getUmdName());
            holder.textView.setTextColor(Color.WHITE);
        }

        @Override
        public int getItemCount() {//리스트 사이드
            return lists.size();
        }
    }

    private class SearchItemViewHolder extends RecyclerView.ViewHolder { // 검색 ItemViewHolder
        TextView textView;

        public SearchItemViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView;
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    address = new Address(null);
                    address.setLocality(lists.get(getAdapterPosition()).sidoName);
                    address.setSubLocality(lists.get(getAdapterPosition()).sggName);
                    address.setThoroughfare(lists.get(getAdapterPosition()).umdName);
                    list.add(address);
                    mainActivity.backMainFragment(list);
                }
            });

        }
    }
}
