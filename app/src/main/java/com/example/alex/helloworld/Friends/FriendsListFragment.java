package com.example.alex.helloworld.Friends;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alex.helloworld.Information;
import com.example.alex.helloworld.R;
import com.example.alex.helloworld.databaseConnection.AsyncResponse;
import com.example.alex.helloworld.databaseConnection.DBconnection;
import com.example.alex.helloworld.databaseConnection.UIthread;
import com.google.gson.Gson;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;

public class FriendsListFragment extends Fragment implements UIthread{

    private ArrayList<Information> friends;
    RecyclerView recyclerView;
    FriendsListAdapter adapter;
    private Boolean selectionMode;
    Friends activity;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.friends_fragment,container,false);
        recyclerView = (RecyclerView) v.findViewById(R.id.friends_recycler_view);
        friends = new ArrayList<>();
        SharedPreferences sharedPrefs = getContext().getSharedPreferences("loginInformation", Context.MODE_PRIVATE);
        String email = sharedPrefs.getString("email", "");
        updateFriendsList(email); //Email from Shared Prefernces?
        activity = (Friends) getActivity();
        selectionMode = activity.getSelectionMode();
        activity.setReference(this);

        //Sets empty adapter to prevent Errors
        adapter = new FriendsListAdapter(getContext(), friends, this, selectionMode);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.notifyDataSetChanged();

        return v;
    }
    public void activateSelectionMode(Boolean bool,int count){
        Friends activity = (Friends) getActivity();
        activity.activateSelectionMode(bool,count);
    }

    public void declineSelection(){
        adapter = new FriendsListAdapter(getContext(), friends, this, selectionMode);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.notifyDataSetChanged();
    }

    public void toggle(int pos) {
        friends.get(pos).selected ^= true;
        activity.setData(friends);
    }

    //Needs to be adapted to Alex DatabaseConnection
    private void updateFriendsList(String email) {
        String[] params = {"/IndexFriendship.php", "function", "getFriends", "email", email};
        new DBconnection(new AsyncResponse() {
            @Override
            public void processFinish(String output) throws ParseException, JSONException {
                parseToArrayList(output);
            }
        }).execute(params);


    }

    //Not needed for Alex Database Connection
    private ArrayList<Information> parseToArrayList(String jsonObjectSring) throws JSONException {
        if (jsonObjectSring != null) {
            ArrayList<Information> data = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(jsonObjectSring);

            int i = 0;
            while (jsonObject.has("" + i)) {
                String meetingString = jsonObject.get("" + i).toString();
                Gson gson = new Gson();
                Information current = gson.fromJson(meetingString, Information.class);
                data.add(current);
                i++;
            }
            adapter = new FriendsListAdapter(getContext(), data, this, selectionMode);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            Friends activity = (Friends) getActivity();
            activity.setData(data);
            friends = data;
            adapter.notifyDataSetChanged();
            return data;
        } else
            return null;

    }


    public void updateCount(int count) {
        activity.updateCount(count);
    }

    @Override
    public void updateUI() {

    }

    @Override
    public void updateUI(String answer) {

    }
}