package com.hackust.taxi.taxihitchhike;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by cameron on 4/17/2016.
 */
public class Plan extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "cost";

    // TODO: Rename and change types of parameters
    private double time;
    private double total_cost;
    private double cost;


    private onPlanSelectionListener mListener;
    public interface onPlanSelectionListener{
        public void onPlanSelected();
    };

    public Plan() {
        // Required empty public constructor
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        time = args.getDouble("Time");
        total_cost = args.getDouble("Total_Cost");
        cost = args.getDouble("Cost");
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param cost Parameter 1.
     * @return A new instance of fragment NoPlan.
     */
    // TODO: Rename and change types and number of parameters
    public static NoPlan newInstance(String cost) {
        NoPlan fragment = new NoPlan();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, cost);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_plan, container, false);
        TextView totalTime = (TextView) v.findViewById(R.id.total_time);
        //totalTime.setText(Integer.toString((int)(time/60)));
        TextView saveCost = (TextView) v.findViewById(R.id.cost_save);
        //saveCost.setText("$" + Integer.toString((int)cost));
        TextView totalCost = (TextView) v.findViewById(R.id.save_cost);
        //totalCost.setText("$" + Integer.toString((int)total_cost));

        return v;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onPlanSelectionListener) {
            mListener = (onPlanSelectionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction();
    }

}
