package com.example.alex.helloworld.Friends;

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
import com.google.gson.Gson;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;

public class GroupsListFragment extends Fragment {
    private DBconnection dBconnection;
    private ArrayList<Information> friends;

    RecyclerView recyclerView;
    GroupListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.groups_fragment,container,false);
        recyclerView = (RecyclerView) v.findViewById(R.id.group_recycler_view);
        friends = new ArrayList<>();
        updateFriendsList("Korbi@Korbi.de");

        adapter = new GroupListAdapter(getContext(), friends);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return v;
    }

    private void updateFriendsList(String email) {
        String[] params = {"/IndexGroups.php", "function", "getGroups"};
        dBconnection = (DBconnection) new DBconnection(new AsyncResponse() {
            @Override
            public void processFinish(String output) throws ParseException, JSONException {
                parseToArrayList(output);
            }
        }).execute(params);


    }
    private ArrayList<Information> parseToArrayList(String jsonObjectSring) throws JSONException {
       if(jsonObjectSring!=null) {
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
           adapter = new GroupListAdapter(getContext(), data);
           recyclerView.setAdapter(adapter);
           recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
           friends = data;
           return data;
       }else
           return null;

    }
}