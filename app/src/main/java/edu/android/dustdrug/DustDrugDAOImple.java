package edu.android.dustdrug;

import android.content.Context;
import android.util.Log;

public class DustDrugDAOImple {
    public static final String TAG = "edu.android";
    private static DustDrugDAOImple instance = null;
    private MainActivity mainActivity ;
    private DustDrugDAOImple(Context context) {
        mainActivity = (MainActivity)context;
    }

    public static DustDrugDAOImple getInstence(Context context) {
        if (instance == null) {
            instance = new DustDrugDAOImple(context);
            Log.i(TAG,"DustDrugDAOImple - getInstence");
        }return instance;
    }





    //데이터 생성중에 mainActivity.lodingUpdate(); 를 넣어줘야함

}
