package com.android.brogrammers.sportsm8.SocialTab;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.brogrammers.sportsm8.DataBaseConnection.ApiServices.GroupsApiService;
import com.android.brogrammers.sportsm8.DataBaseConnection.ApiServices.TeamsApiService;
import com.android.brogrammers.sportsm8.R;
import com.android.brogrammers.sportsm8.UserClasses.LoginScreen;
import com.android.brogrammers.sportsm8.DataBaseConnection.ApiServices.APIService;
import com.android.brogrammers.sportsm8.DataBaseConnection.APIUtils;
import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.Sport;
import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.UserInfo;
import com.mypopsy.maps.StaticMap;
import com.schibstedspain.leku.LocationPickerActivity;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;
import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.observers.DisposableSingleObserver;

/**
 * Created by Korbi on 19.12.2016.
 */

public class CreateTeamGroup extends android.support.v4.app.DialogFragment {
    private List<UserInfo> selection;
    private List<Sport> sports;
    private String name;
    private GroupsApiService groupsApiService = APIUtils.getGroupsAPIService();
    private TeamsApiService teamsApiService = APIUtils.getTeamsAPIService();
    private APIService apiService = APIUtils.getAPIService();
    private boolean isCreatingTeam;
    @BindView(R.id.editText_new_group_team)
    EditText nameEditText;
    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.imageView)
    ImageView map;
    @BindView(R.id.teamButtonID)
    Button teamBtn;
    @BindView(R.id.groupButton)
    Button groupBtn;
    private int sportID = -1;
    private double longitude;
    private double latitude;
    private List<Sport> info;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_create_group, null);
        ButterKnife.bind(this, view);
        builder.setView(view);
        //Declaration Variables
        Bundle bundle = getArguments();
        selection = (ArrayList<UserInfo>) bundle.getSerializable("GroupList");
        //Init Listeners
        nameEditText.setSingleLine(true);
        spinner.setEnabled(false);
        apiService.getSports()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<Sport>>() {
                    @Override
                    public void onSuccess(@NonNull List<Sport> sportList) {
                        sports=sportList;
                        setUpSpinner(sports);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }
                });
        loadPicture(48, 11);
        return builder.create();
    }

    private void setUpSpinner(List<Sport> sports) {
        info = sports;
        String[] array = new String[sports.size()];
        for (int i = 0; i < sports.size(); i++) {
            array[i] = sports.get(i).sportname;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, array);
        spinner.setAdapter(adapter);
    }


    @OnClick(R.id.imageView)
    public void pickLocation() {
        Intent intent = new LocationPickerActivity.Builder()
                .withSearchZone("de_DE")
                .withStreetHidden()
                .withCityHidden()
                .withZipCodeHidden()
                .build(getContext());
        startActivityForResult(intent, 5);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            longitude = data.getDoubleExtra(LocationPickerActivity.LONGITUDE, 0);
            latitude = data.getDoubleExtra(LocationPickerActivity.LATITUDE, 0);
            loadPicture(longitude, latitude);
        }
    }

    private void loadPicture(double longitude, double latitude) {
        StaticMap staticMap = new StaticMap().type(StaticMap.Type.SATELLITE).size(320, 320).zoom(18).marker(StaticMap.Marker.Style.RED, new StaticMap.GeoPoint(latitude, longitude));
        String url = null;
        try {
            url = staticMap.toURL().toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Picasso.with(getContext())
                .load(url)
                .error(R.drawable.dickbutt)
                // .memoryPolicy(MemoryPolicy.NO_CACHE,MemoryPolicy.NO_STORE)
                .into(map);
    }


    @OnClick(R.id.button_create_group_team)
    public void createGroupTeam() {
        String email = LoginScreen.getRealEmail();
        Map<String, String> members = new HashMap<>();
        members.put("member", email);
        for (int i = 0; i < selection.size(); i++) {
            members.put("member" + i, selection.get(i).email);
        }

        if (isCreatingTeam) {
            if(selection.size()+1==sports.get(sportID).teamSize||sportID==8008) {
                teamsApiService.createTeam(name,longitude,latitude,sportID,members)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action() {
                            @Override
                            public void run() throws Exception {
                                //Toasty.info(getActivity(),"Team erstellt",Toast.LENGTH_SHORT).show();
                            }
                        });
            }else {
                 Toasty.error(getContext(), "Du brauchst genau "+sports.get(sportID).teamSize+" Teilnehmer für dein Team", Toast.LENGTH_SHORT).show();
            }
        } else {
            groupsApiService.createGroup(name,members)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action() {
                        @Override
                        public void run() throws Exception {

                        }
                    });
        }
        dismiss();
    }

    @OnTextChanged(R.id.editText_new_group_team)
    public void onTextChanged(CharSequence charSequence) {
        name = charSequence.toString();
    }

    @OnClick({R.id.groupButton, R.id.teamButtonID})
    public void toggleButtons(View view) {
        switch (view.getId()) {
            case R.id.groupButton:
                teamBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.grey));
                groupBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                spinner.setEnabled(false);
                break;
            case R.id.teamButtonID:
                groupBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.grey));
                teamBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                spinner.setEnabled(true);
                break;
        }
        isCreatingTeam = !isCreatingTeam;

    }

    @OnItemSelected(R.id.spinner)
    public void setSportID(int position){
        sportID = info.get(position).sportID;
    }

}
