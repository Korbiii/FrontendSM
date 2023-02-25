package com.android.brogrammers.sportsm8.calendarTab.meetingDetailMVP;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.brogrammers.sportsm8.BR;
import com.android.brogrammers.sportsm8.calendarTab.meetingDetailMVP.adapter.MemberListAdapter;
import com.android.brogrammers.sportsm8.calendarTab.meetingDetailMVP.adapter.MemberListAdapterRV;
import com.android.brogrammers.sportsm8.dataBaseConnection.databaseClasses.Meeting;
import com.android.brogrammers.sportsm8.dataBaseConnection.databaseClasses.UserInfo;
import com.android.brogrammers.sportsm8.dataBaseConnection.repositories.impl.DatabaseMeetingsRepository;
import com.android.brogrammers.sportsm8.dataBaseConnection.repositories.impl.DatabaseUserRepository;
import com.android.brogrammers.sportsm8.R;
import com.android.brogrammers.sportsm8.databinding.ActivityMeetingDetailViewBinding;
import com.android.brogrammers.sportsm8.databinding.ContentMeetingDetailViewBinding;
import com.android.brogrammers.sportsm8.socialTab.friends.OnlyFriendsView;
import com.android.brogrammers.sportsm8.userClasses.LoginScreen;
import com.android.brogrammers.sportsm8.ViewHelperClass;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class MeetingDetailActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, MeetingDetailView {

    private static final String TAG = MeetingDetailActivity.class.getSimpleName();
    private List<UserInfo> members;
    Meeting thisMeeting;
    MemberListAdapter arrayAdapter;
    MemberListAdapterRV arrayAdapterRV;
    DatabaseMeetingsRepository meetingsRepository;
    DateTime startDateTime, endDateTime;
    private Intent intent;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();


    MeetingDetailViewPresenter presenter;
    private ActivityMeetingDetailViewBinding binding;
    private ContentMeetingDetailViewBinding include;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new MeetingDetailViewPresenter(this, new DatabaseUserRepository(), AndroidSchedulers.mainThread());
        binding = ActivityMeetingDetailViewBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        include = binding.include;
        thisMeeting = getIntent().getParcelableExtra("MeetingOnDay");
        binding.setVariable(BR.meeting, thisMeeting);

        binding.addPeopleToMeeting.setOnClickListener(this::addPeopleToMeeting);
        binding.backArrowMeetingDetail.setOnClickListener(this::back);
        binding.acceptMeeting.setOnClickListener(this::acceptMeeting);
        binding.declineMeeting.setOnClickListener(this::declineMeeting);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Variables

        startDateTime = thisMeeting.getStartDateTime();
        endDateTime = thisMeeting.getEndDateTime();
        Resources res = getResources();
        TypedArray bannerArray = res.obtainTypedArray(R.array.sportDrawables);
        //Set Views

        include.tvTimeLabel.setText(getString(R.string.meeting_start_end,startDateTime.toString("HH:mm"),endDateTime.toString("HH:mm")));
        include.tvDateLabel.setText(startDateTime.toString("dd.MM.YYYY"));
        if (thisMeeting.sportID < bannerArray.length()) {
            binding.meetingDetailViewImageview.setImageResource(bannerArray.getResourceId(thisMeeting.sportID, R.drawable.custommeeting));
        }
        bannerArray.recycle();
        binding.activityNameDetailview.setText(thisMeeting.meetingActivity);
        members = new ArrayList<>();
        arrayAdapter = new MemberListAdapter(this, members);
        include.listviewMeetingDetail.setAdapter(arrayAdapter);
        arrayAdapterRV = new MemberListAdapterRV(this,members);
        include.recyclerviewMeetingDetail.setAdapter(arrayAdapterRV);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        include.recyclerviewMeetingDetail.setLayoutManager(linearLayoutManager);
        //    getMemberList();
        presenter.loadMembers(thisMeeting);


        include.listviewMeetingDetail.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });

        binding.meetingDetailCollABL.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (verticalOffset < -100) verticalOffset = -100;
            float offset = 1 + (verticalOffset / 100f);
            scaleView(new View[]{binding.acceptMeeting, binding.declineMeeting,binding.rangebar,binding.newEndTimeDetailV,binding.newStartTimeDetailV,binding.dashDetailView}, offset);
        });
        meetingsRepository = new DatabaseMeetingsRepository();
        if (thisMeeting.dynamic == 0) {
            binding.rangebar.setVisibility(View.GONE);
        } else {
            rangeBarSetup();
        }
        if (thisMeeting.getConfirmed() == 1 || thisMeeting.duration != 0) {
            ViewHelperClass.setInvisible(new View[]{binding.acceptMeeting, binding.declineMeeting,binding.rangebar,binding.newEndTimeDetailV,binding.newStartTimeDetailV,binding.dashDetailView});
        }
        intent = new Intent();
        //    swipeRefreshLayout.setOnRefreshListener(this);

    }


    private void rangeBarSetup() {
        binding.rangebar.setMax(96);
        binding.newStartTimeDetailV.setText(startDateTime.toString("HH:mm"));
        binding.newEndTimeDetailV.setText(endDateTime.toString("HH:mm"));
        binding.rangebar.getThumb(0).setValue(startDateTime.getMinuteOfDay() / 15);
        binding.rangebar.getThumb(1).setValue(endDateTime.getMinuteOfDay() / 15);
        binding.rangebar.setStepsThumbsApart(4);
        binding.rangebar.setOnThumbValueChangeListener((multiSlider, thumb, thumbIndex, value) -> {
            if (thumbIndex == 0) {
                startDateTime = startDateTime.withTimeAtStartOfDay().plusMinutes(value * 15);
                binding.newStartTimeDetailV.setText(startDateTime.toString("HH:mm"));
                thisMeeting.setStartDateTime(startDateTime);
            } else {
                endDateTime = endDateTime.withTimeAtStartOfDay().plusMinutes(value * 15);
                binding.newEndTimeDetailV.setText(endDateTime.toString("HH:mm"));
                thisMeeting.setEndStartDateTime(endDateTime);
            }
        });
    }

    @Override
    public void setUpprogressBar(int accepted, int total) {
        include.progressBar.removeAllViews();
        for (int i = 0; i < total; i++) {
            ImageView imageView = new ImageView(getBaseContext());
            imageView.setImageResource(R.drawable.ic_person_black_36dp);
            imageView.setId(i);
            if (i >= accepted) {
                imageView.setScaleX(0.5f);
                imageView.setScaleY(0.5f);
                imageView.setAlpha(0.5f);
            }
            include.progressBar.addView(imageView);
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



    ActivityResultLauncher<Intent> startAddingPeopleToMeeting = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Bundle bundle = result.getData().getExtras();
                    List<UserInfo> selection = (List<UserInfo>) bundle.getSerializable("partyList");
                    presenter.addMembers(thisMeeting, selection);
                }
            });


    public void addPeopleToMeeting(View view) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("SelectionMode", true);
        Intent intent = new Intent(this, OnlyFriendsView.class);
        intent.putExtras(bundle);
        startAddingPeopleToMeeting.launch(intent);
//        startActivityForResult(intent, 1);
    }

    public void back(View view) {
        finish();
    }


    public void acceptMeeting(View view) {
        compositeDisposable.add(meetingsRepository.confirmMeeting(thisMeeting)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> Toasty.info(getBaseContext(), "Teilnahme zugesagt", Toast.LENGTH_SHORT).show()));


        setResult(RESULT_OK, intent);
        ViewHelperClass.setInvisible(new View[]{binding.acceptMeeting, binding.declineMeeting,binding.rangebar,binding.newEndTimeDetailV,binding.newStartTimeDetailV,binding.dashDetailView});
        int count = 0;
        include.progressBar.findViewById(count).animate().scaleX(1).scaleY(1).alpha(1);
        for (int i = 0; i < members.size(); i++) {
            String email = LoginScreen.getEmailAdress(this);
            if (members.get(i).email.equals(email)) {
                members.get(i).confirmed = 1;
                arrayAdapter.notifyDataSetChanged();
            }
        }
    }

    public void declineMeeting(View view) {
        compositeDisposable.add(meetingsRepository.declineMeeting(thisMeeting)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> Toasty.info(getBaseContext(), "Teilnahme abgesagt", Toast.LENGTH_SHORT).show()));
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
        for (View value : view) {
            value.animate().scaleX(offset).setDuration(100);
            value.animate().scaleY(offset).setDuration(100);
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