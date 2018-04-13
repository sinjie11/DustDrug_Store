package edu.android.dustdrug;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class LocationFragment extends Fragment {
    public static interface LocationDeliverCallback {
        public void onLocationDeliver(String location);
    }

    public LocationDeliverCallback callback;

    public static final String TAG = "edu.android";
    private ListView listView;
    private MainActivity mainActivity;
    private OnFragmentInteractionListener mListener;

    public LocationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_location, container, false);
        listView = view.findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = MainActivity.newIntent(getContext());
//                String location = ((TextView) view).getText().toString();
//                intent.putExtra("location", location);
//                startActivity(intent);
//                Log.i(TAG, "location : " + location);
//                callback.onLocationDeliver(location);
                removeFragment(LocationFragment.this);
                String location = ((TextView) view).getText().toString();
                Bundle bundle = new Bundle();
                bundle.putString("location", location);
                LocationFragment.this.setArguments(bundle);
                callback.onLocationDeliver(location);
            }
        });

        view.setFocusableInTouchMode(true);
        view.requestFocus();

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        mainActivity = (MainActivity) context;
        super.onAttach(context);
        if (context instanceof LocationDeliverCallback) {
            callback = (LocationDeliverCallback) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name


        void onFragmentInteraction(Uri uri);
    }

    public void removeFragment(Fragment fragment) {
        FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
        FragmentTransaction transaction = mainActivity.getSupportFragmentManager().beginTransaction();
        if(fragmentManager.findFragmentById(R.id.fragment_container) != null) {
            transaction.remove(fragmentManager.findFragmentById(R.id.fragment_container)).commit();
        }
    }
}
