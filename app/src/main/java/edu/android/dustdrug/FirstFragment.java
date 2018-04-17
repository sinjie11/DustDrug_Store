package edu.android.dustdrug;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FirstFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class FirstFragment extends Fragment {

    public int cnt = 0;
    private TextView textView;
    private Thread loadingThread;
    private MainFragment mainFragment;
    private OnFragmentInteractionListener mListener;

    public FirstFragment() {
        // Required empty public constructor
    }


    public static final String TAG = "FirstFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        textView = view.findViewById(R.id.textView);
        Log.i(TAG, "스레드 전");

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
                        Thread.sleep(1000);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                FragmentManager manager = getActivity().getSupportFragmentManager();
                Fragment fragment = manager.findFragmentById(R.id.fragment_container);
                FragmentTransaction transaction = manager.beginTransaction();
                mainFragment = MainFragment.newInstance();
                transaction.replace(R.id.fragment_container, mainFragment);
                transaction.commit();


            }

        };
//        startLocationService();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadingThread.start();
    }

    public static FirstFragment newInstance() {
        FirstFragment firstFragment = new FirstFragment();
        return firstFragment;
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
}
