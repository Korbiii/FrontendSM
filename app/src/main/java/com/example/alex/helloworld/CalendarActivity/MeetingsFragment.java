package com.example.alex.helloworld.CalendarActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alex.helloworld.Information;
import com.example.alex.helloworld.R;
import java.util.ArrayList;

/**
 * CCreated by Korbi on 10/30/2016.
 */

public class MeetingsFragment extends Fragment {


    public static MeetingsFragment newInstance(int position, ArrayList<Information> meetingsOnDay){

        // how exactly to hand down meetingsOnDay to RecyclerViewAdapter?
        //#######################??
        MeetingsFragment meetingsFragment = new MeetingsFragment();
        Bundle args = new Bundle();
        args.putSerializable("meetingsOnDay", meetingsOnDay);
        //args.putParcelableArrayList("meetingsOnDay", meetingsOnDay);
        meetingsFragment.setArguments(args);
        return meetingsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.kalendar_activity_content, container, false);

        // needs to use the containers function since its not an activity

        //#############
        //It doesnt seem to find the recyclerView!?
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.meetings_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        RecyclerView.Adapter rvAdapter = new RecyclerViewAdapter(getContext(), (ArrayList<Information>)this.getArguments().getSerializable("meetingsOnDay"));
        recyclerView.setAdapter(rvAdapter);


        return view;
    }
}
