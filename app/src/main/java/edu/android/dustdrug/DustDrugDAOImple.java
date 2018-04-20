package edu.android.dustdrug;

import android.content.Context;

public class DustDrugDAOImple {
    private static DustDrugDAOImple instance = null;
    private MainActivity mainActivity ;
    private DustDrugDAOImple(Context context) {
        mainActivity = (MainActivity)context;
    }

    public static DustDrugDAOImple getInstence(Context context) {
        if (instance == null) {
            instance = new DustDrugDAOImple(context);
        }return instance;
    }





    //데이터 생성중에 mainActivity.lodingUpdate(); 를 넣어줘야함

}
