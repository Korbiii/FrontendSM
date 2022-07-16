package com.android.brogrammers.sportsm8.ZZOldClassers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import com.android.brogrammers.sportsm8.CalendarTab.CreateNewMeeting;
import com.android.brogrammers.sportsm8.R;

import org.json.JSONException;
import org.json.simple.parser.ParseException;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ActivitiesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ActivitiesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActivitiesFragment extends Fragment implements UIthread {

    Activity parentActivity;
    List<Information> sportIDs;
    int sportID;
    String[] sportArten;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;


    Spinner sportsSpinner;
    int i = 0;


    private OnFragmentInteractionListener mListener;

    public ActivitiesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ActivitiesFragment.
     */
    public static ActivitiesFragment newInstance(String param1, String param2) {
        return new ActivitiesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_activities, container, false);
        //toolbar problem needs to be solved first
        ButterKnife.bind(this,rootView);
       // sportsSpinner = (Spinner) getActivity().findViewById(R.id.spinner2);
        //createList();
        return rootView;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


    @OnClick(R.id.button_create_meeting)
    public void createMeeting(View v) {
        Bundle b = new Bundle();
     //   b.putInt("sportID", Integer.valueOf(sportIDs.get(sportID).sportID));
        b.putInt("sportID",8008);
        b.putBoolean("liga", false);
        Intent intent = new Intent(parentActivity, CreateNewMeeting.class);
        intent.putExtras(b);
        startActivity(intent);
    }

    private void initSpinner() {
        if (sportArten != null) {
            ArrayAdapter<String> myAdapater = new ArrayAdapter<>(parentActivity, R.layout.item_custom_spinner, sportArten);
            myAdapater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
             sportsSpinner.setAdapter(myAdapater);

            sportsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    update(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


        }
    }

    public void update(int sID) {
        sportID = sID;
        Button fSpiel = (Button) parentActivity.findViewById(R.id.button_create_meeting);
        ImageView iV = (ImageView) parentActivity.findViewById(R.id.imageView_sportart);

        Information temp = sportIDs.get(sID);
        Resources res = parentActivity.getResources();
        TypedArray draws = res.obtainTypedArray(R.array.sportDrawables);
        if (Integer.valueOf(temp.sportID) != 8008) {
            iV.setImageDrawable(draws.getDrawable(Integer.parseInt(temp.sportID)));
        } else {
            iV.setImageDrawable(getResources().getDrawable(R.drawable.custommeeting));
        }
        draws.recycle();
        fSpiel.setVisibility(View.GONE);
        if (Integer.valueOf(sportIDs.get(sID).team) == 1) {
            fSpiel.setVisibility(View.VISIBLE);
        } else {
            fSpiel.setVisibility(View.VISIBLE);
        }

    }

    private void createList() {
        String[] params = {"IndexSports.php", "function", "getData"};
        //must parent Activity implement UIThread?
        Database db = new Database(this, parentActivity.getBaseContext());
        db.execute(params);
        updateUI("");
    }

    @Override
    public void updateUI() {

    }

    @Override
    public void updateUI(String answer) {
        SharedPreferences sharedPrefs = parentActivity.getSharedPreferences("IndexSports", Context.MODE_PRIVATE);
        String meetingJson = sharedPrefs.getString("IndexSportsgetDataJSON", "");
        try {
            sportIDs = Database.jsonToArrayList(meetingJson);
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
        if (sportIDs != null) {
            sportArten = new String[sportIDs.size()];
            for (int j = 0; j < sportIDs.size(); j++) {
                sportArten[j] = sportIDs.get(j).sportname;
            }
        }
        initSpinner();
    }
}
