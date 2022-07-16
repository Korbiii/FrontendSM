package com.android.brogrammers.sportsm8.CalendarTab.MeetingDetailMVP;

import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.brogrammers.sportsm8.BR;
import com.android.brogrammers.sportsm8.CalendarTab.MeetingDetailMVP.Adapter.MemberListAdapter;
import com.android.brogrammers.sportsm8.CalendarTab.MeetingDetailMVP.Adapter.MemberListAdapterRV;
import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.Meeting;
import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.UserInfo;
import com.android.brogrammers.sportsm8.DataBaseConnection.Repositories.impl.DatabaseMeetingsRepository;
import com.android.brogrammers.sportsm8.DataBaseConnection.Repositories.impl.DatabaseUserRepository;
import com.android.brogrammers.sportsm8.R;
import com.android.brogrammers.sportsm8.SocialTab.Friends.OnlyFriendsView;
import com.android.brogrammers.sportsm8.UserClasses.LoginScreen;
import com.android.brogrammers.sportsm8.ViewHelperClass;
import com.android.brogrammers.sportsm8.databinding.ActivityMeetingDetailViewBinding;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import io.apptik.widget.MultiSlider;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;

public class MeetingDetailActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, MeetingDetailView {

    private static final String TAG = MeetingDetailActivity.class.getSimpleName();
    private List<UserInfo> members;
    Meeting thisMeeting;
    MemberListAdapter arrayAdapter;
    MemberListAdapterRV arrayAdapterRV;
    DatabaseMeetingsRepository meetingsRepository;
    DateTime startDateTime, endDateTime;
    private Intent intent;

    @BindView(R.id.listview_meeting_detail)
    ListView listView;
    @BindView(R.id.recyclerview_meeting_detail)
    RecyclerView recyclerView;
    //@BindView(R.id.meeting_detail_swipeRefresh)
    //SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.time_meeting_detail)
    TextView textView_time;
    @BindView(R.id.tv_home_name)
    TextView textView_date;
    @BindView(R.id.activity_name_detailview)
    TextView textView_sportID;
    @BindView(R.id.meeting_detail_view_imageview)
    ImageView bannerImage;
    @BindView(R.id.meeting_detail_collABL)
    AppBarLayout appBarLayout;
    @BindView(R.id.accept_meeting)
    ImageButton acceptMeeting;
    @BindView(R.id.decline_meeting)
    ImageButton declineMeeting;
    @BindView(R.id.rangebar)
    MultiSlider rangeBar;
    @BindView(R.id.progress_bar)
    LinearLayout progressBar;
    @BindView(R.id.new_endTime_detailV)
    TextView newEndTimeView;
    @BindView(R.id.new_startTime_detailV)
    TextView newStartTimeView;
    @BindView(R.id.dash_detailView)
    TextView dashView;

    MeetingDetailViewPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new MeetingDetailViewPresenter(this, new DatabaseUserRepository(), AndroidSchedulers.mainThread());
        // setContentView(R.layout.activity_meeting_detail_view);
        ActivityMeetingDetailViewBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_meeting_detail_view);
        thisMeeting = getIntent().getParcelableExtra("MeetingOnDay");
        binding.setVariable(BR.meeting, thisMeeting);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        //Variables

        startDateTime = thisMeeting.getStartDateTime();
        endDateTime = thisMeeting.getEndDateTime();
        Resources res = getResources();
        TypedArray bannerArray = res.obtainTypedArray(R.array.sportDrawables);
        //Set Views
        textView_time.setText(startDateTime.toString("HH:mm") + "-" + endDateTime.toString("HH:mm"));
        textView_date.setText(startDateTime.toString("dd.MM.YYYY"));
        if (thisMeeting.sportID < bannerArray.length()) {
            bannerImage.setImageResource(bannerArray.getResourceId(thisMeeting.sportID, R.drawable.custommeeting));
        }
        bannerArray.recycle();
        textView_sportID.setText(thisMeeting.meetingActivity);
        members = new ArrayList<>();
        arrayAdapter = new MemberListAdapter(this, members);
        listView.setAdapter(arrayAdapter);
        arrayAdapterRV = new MemberListAdapterRV(this,members);
        recyclerView.setAdapter(arrayAdapterRV);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        //    getMemberList();
        presenter.loadMembers(thisMeeting);

        findViewById(R.id.listview_meeting_detail).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset < -100) verticalOffset = -100;
                float offset = 1 + (verticalOffset / 100);
                scaleView(new View[]{acceptMeeting, declineMeeting, rangeBar, newEndTimeView, newStartTimeView, dashView}, offset);
            }
        });
        meetingsRepository = new DatabaseMeetingsRepository();
        if (thisMeeting.dynamic == 0) {
            rangeBar.setVisibility(View.GONE);
        } else {
            rangeBarSetup();
        }
        if (thisMeeting.getConfirmed() == 1 || thisMeeting.duration != 0) {
            ViewHelperClass.setInvisible(new View[]{acceptMeeting, declineMeeting, rangeBar, newEndTimeView, newStartTimeView, dashView});
        }
        intent = new Intent();
        //    swipeRefreshLayout.setOnRefreshListener(this);

    }

    private void rangeBarSetup() {
        rangeBar.setMax(96);
        newStartTimeView.setText(startDateTime.toString("HH:mm"));
        newEndTimeView.setText(endDateTime.toString("HH:mm"));
        rangeBar.getThumb(0).setValue(startDateTime.getMinuteOfDay() / 15);
        rangeBar.getThumb(1).setValue(endDateTime.getMinuteOfDay() / 15);
        rangeBar.setStepsThumbsApart(4);
        rangeBar.setOnThumbValueChangeListener(new MultiSlider.OnThumbValueChangeListener() {
            @Override
            public void onValueChanged(MultiSlider multiSlider, MultiSlider.Thumb thumb, int thumbIndex, int value) {
                if (thumbIndex == 0) {
                    startDateTime = startDateTime.withTimeAtStartOfDay().plusMinutes(value * 15);
                    newStartTimeView.setText(startDateTime.toString("HH:mm"));
                    thisMeeting.setStartDateTime(startDateTime);
                } else {
                    endDateTime = endDateTime.withTimeAtStartOfDay().plusMinutes(value * 15);
                    newEndTimeView.setText(endDateTime.toString("HH:mm"));
                    thisMeeting.setEndStartDateTime(endDateTime);
                }
            }
        });
    }

    @Override
    public void setUpprogressBar(int accepted, int total) {
        progressBar.removeAllViews();
        for (int i = 0; i < total; i++) {
            ImageView imageView = new ImageView(getBaseContext());
            imageView.setImageResource(R.drawable.ic_person_black_36dp);
            imageView.setId(i);
            if (i >= accepted) {
                imageView.setScaleX(0.5f);
                imageView.setScaleY(0.5f);
                imageView.setAlpha(0.5f);
            }
            progressBar.addView(imageView);
        }
    }

    @Override
    public void updateMemberList() {
        presenter.loadMembers(thisMeeting);
    }

    @Override
    public void showError() {
        Toasty.error(this, "Error", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.add_people_to_meeting)
    public void addPeopleToMeeting(View view) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("SelectionMode", true);
        Intent intent = new Intent(this, OnlyFriendsView.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, 1);
    }

    @OnClick(R.id.back_arrow_meeting_detail)
    public void back() {
        finish();
    }

    @OnClick(R.id.accept_meeting)
    public void acceptMeeting() {
        meetingsRepository.confirmMeeting(thisMeeting)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                         Toasty.info(getBaseContext(), "Teilnahme zugesagt", Toast.LENGTH_SHORT).show();
                    }
                });


        setResult(RESULT_OK, intent);
        ViewHelperClass.setInvisible(new View[]{acceptMeeting, declineMeeting, rangeBar, newEndTimeView, newStartTimeView, dashView});
        int count = 0;
        progressBar.findViewById(count).animate().scaleX(1).scaleY(1).alpha(1);
        for (int i = 0; i < members.size(); i++) {
            String email = LoginScreen.getEmailAdress(this);
            if (members.get(i).email.equals(email)) {
                members.get(i).confirmed = 1;
                arrayAdapter.notifyDataSetChanged();
            }
        }
    }

    @OnClick(R.id.decline_meeting)
    public void declineMeeting() {
        meetingsRepository.declineMeeting(thisMeeting)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                         Toasty.info(getBaseContext(), "Teilnahme abgesagt", Toast.LENGTH_SHORT).show();
                    }
                });
        setResult(RESULT_OK, intent);
        finish();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                List<UserInfo> selection = (List<UserInfo>) bundle.getSerializable("partyList");
                presenter.addMembers(thisMeeting, selection);
            }
        }
    }

    @Override
    public void onRefresh() {
        presenter.loadMembers(thisMeeting);
    }

    public void scaleView(View[] view, float offset) {
        for (int i = 0; i < view.length; i++) {
            view[i].animate().scaleX(offset).setDuration(100);
            view[i].animate().scaleY(offset).setDuration(100);
        }
    }

    @Override
    public void displayMembers(List<UserInfo> members) {
        Log.d(TAG, "displayMembers: found some Members");
        this.members.clear();
        this.members.addAll(members);
        arrayAdapter.setList(this.members);
        arrayAdapter.notifyDataSetChanged();

        arrayAdapterRV.setList(this.members);
        arrayAdapterRV.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unsubscribe();
    }

    @Override
    public void displayNoMembers() {
        Toasty.error(this, "No Members found", Toast.LENGTH_SHORT).show();
    }


    public void showUsername(String username) {
        TextView usernameTextView = (TextView) findViewById(R.id.username_detail_meetingdetail);
        usernameTextView.setText(username);
    }
}