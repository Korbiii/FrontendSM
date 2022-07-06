package com.android.brogrammers.sportsm8.socialTab.teams.teamsDetailView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.brogrammers.sportsm8.calendarTab.meetingDetailMVP.adapter.MemberListAdapter;
import com.android.brogrammers.sportsm8.R;
import com.android.brogrammers.sportsm8.dataBaseConnection.databaseClasses.Team;
import com.android.brogrammers.sportsm8.dataBaseConnection.databaseClasses.UserInfo;
import com.android.brogrammers.sportsm8.dataBaseConnection.repositories.impl.DatabaseTeamsRepository;
import com.mypopsy.maps.StaticMap;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by Korbi on 20.06.2017.
 */

public class TeamDetailActivity extends AppCompatActivity implements TeamDetailView {

    Team thisTeam;
    MemberListAdapter adapter;
    List<UserInfo> teamMember;
    TeamDetailPresenter presenter;

    @BindView(R.id.team_list_dV)
    ListView memberList;
    @BindView(R.id.HomebaseMap)
    ImageView homeBaseImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_detail_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ButterKnife.bind(this);
        teamMember = new ArrayList<>();
        adapter = new MemberListAdapter(this, teamMember);
        memberList.setAdapter(adapter);
        thisTeam = (Team) getIntent().getSerializableExtra("Team");
        getSupportActionBar().setTitle(thisTeam.teamName);
        presenter = new TeamDetailPresenter(new DatabaseTeamsRepository(), this, AndroidSchedulers.mainThread());
        presenter.loadTeamMembers(thisTeam);
        loadMap();
    }


    @Override
    public void displayMembers(List<UserInfo> members) {
        teamMember.clear();
        teamMember = members;
        adapter.setList(teamMember);
        adapter.notifyDataSetChanged();
    }

    public void loadMap() {
        StaticMap staticMap = new StaticMap().type(StaticMap.Type.SATELLITE).size(1000, 320).zoom(18).marker(StaticMap.Marker.Style.RED, new StaticMap.GeoPoint(thisTeam.latitude_home, thisTeam.longitude_home));
        String url = null;
        try {
            url = staticMap.toURL().toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Picasso.with(this)
                .load(url)
                .error(R.drawable.dickbutt)
                // .memoryPolicy(MemoryPolicy.NO_CACHE,MemoryPolicy.NO_STORE)
                .into(homeBaseImage);

    }
}
