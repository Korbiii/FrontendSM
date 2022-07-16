package com.android.brogrammers.sportsm8.CalendarTab.CalendarFragmentMVP;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.brogrammers.sportsm8.CalendarTab.Adapter.MeetingCardAdapter;
import com.android.brogrammers.sportsm8.CalendarTab.MeetingDetailMVP.MeetingDetailActivity;
import com.android.brogrammers.sportsm8.R;
import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.Meeting;
import com.android.brogrammers.sportsm8.DataBaseConnection.Repositories.impl.DatabaseMeetingsRepository;
import com.google.gson.JsonObject;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.joda.time.DateTime;
import org.joda.time.Minutes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.observers.DisposableSingleObserver;

public class DayFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<Meeting> meetingsOnDay;
    private MeetingCardAdapter rvAdapter;
    private FloatingActionButton floatingActionButton;
    private DatabaseMeetingsRepository meetingsRepository = new DatabaseMeetingsRepository();
    private int position;

    public static DayFragment newInstance(List<Meeting> meetingsOnDay) {
        DayFragment dayFragment = new DayFragment();
        Bundle args = new Bundle();
        Collections.sort(meetingsOnDay, new CustomComperator());
        args.putSerializable("meetingsOnDay", new ArrayList<>(meetingsOnDay));
        dayFragment.setArguments(args);
        return dayFragment;
    }

    public void updateInstance(List<Meeting> meetings, DateTime today, double latitude, double longitude, boolean locationMode) {
        meetingsOnDay.clear();

        for (int i = 0; i < meetings.size(); i++) {
            DateTime dt1 = meetings.get(i).getStartDateTime();
            if (dt1.withTimeAtStartOfDay().equals(today.withTimeAtStartOfDay())) {
                if (locationMode) {
                    Location start = new Location("locationA");
                    start.setLatitude(latitude);
                    start.setLongitude(longitude);
                    Location end = new Location("locationB");
                    end.setLatitude(meetings.get(i).latitude);
                    end.setLongitude(meetings.get(i).longitude);
                    double distance = start.distanceTo(end);
                    if (distance < 5000) {
                        meetingsOnDay.add(meetings.get(i));
                    }
                } else {
                    meetingsOnDay.add(meetings.get(i));
                }
            }
        }
        rvAdapter = new MeetingCardAdapter(getContext(), meetingsOnDay, this);
        recyclerView.setAdapter(rvAdapter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_day, container, false);
        floatingActionButton = (FloatingActionButton) getActivity().findViewById(R.id.fab_calendar);
        // needs to use the containers function since its not an activity

        //#############
        recyclerView = (RecyclerView) view.findViewById(R.id.meetings_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        meetingsOnDay = (ArrayList<Meeting>) this.getArguments().getSerializable("meetingsOnDay");
        rvAdapter = new MeetingCardAdapter(getContext(), meetingsOnDay, this);
        recyclerView.setAdapter(rvAdapter);

        AppBarLayout appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == 0) {
                    floatingActionButton.animate().translationX(0).setDuration(100);
                } else {
                    floatingActionButton.animate().translationX(500);
                }
            }
        });
        return view;
    }

    public List<Meeting> getMeetingsOnDay() {
        return (ArrayList<Meeting>) this.getArguments().getSerializable("meetingsOnDay");
    }

    public void startDetailView(Meeting meeting) {
        Intent intent = new Intent(getContext(), MeetingDetailActivity.class);
        intent.putExtra("MeetingOnDay",meeting);
        startActivityForResult(intent, 2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            ((CalenderFragment) getParentFragment()).onRefresh();
            //TODO: Refresh better
        }
    }

    public void declineMeeting(final Meeting meeting) {
        meetingsRepository.declineMeeting(meeting)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                         Toasty.info(getContext(), "Teilnahme abgesagt", Toast.LENGTH_SHORT).show();
                    }
                });
        rvAdapter.removeItem(meeting);

    }

    public boolean showDeclineButton(View view, Meeting meeting) {
        if (meeting.duration != 0 || meeting.getConfirmed() == 1)
            view.findViewById(R.id.decline_meeting_button_2).setVisibility(View.VISIBLE);
        return true;
    }

    public void setOtherTime(final Meeting meeting) {
        TimePickerDialog tdp = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                final int begin = hourOfDay;
                final int beginMinute = minute;
                TimePickerDialog tdp2 = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePickerDialog view, int endHourOfDay, int endMinute, int second) {
                        DateTime startTime = meeting.getStartDateTime().withTimeAtStartOfDay();
                        DateTime endTime = meeting.getStartDateTime().withTimeAtStartOfDay();
                        endTime = endTime.plusMinutes(endHourOfDay * 60 + endMinute);
                        startTime = startTime.plusMinutes(begin * 60 + beginMinute);
                        boolean checked = false;
                        if (meeting.dynamic == 2) checked = true;
                        else {
                            DateTime actualStart = meeting.getStartDateTime();
                            DateTime actualEnd = meeting.getEndDateTime();
                            checked = Minutes.minutesBetween(actualStart, endTime).getMinutes() >= 30 && Minutes.minutesBetween(startTime, actualEnd).getMinutes() >= 30;
                        }

                        if (checked) {
                            meetingsRepository.setOtherTime(meeting,startTime,endTime)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Action() {
                                        @Override
                                        public void run() throws Exception {
                                             Toasty.info(getContext(), "Andere Zeit gesetzt!", Toast.LENGTH_SHORT).show();
                                        }
                                    });


                            position = meetingsOnDay.indexOf(meeting);
                            meetingsOnDay.get(position).setConfirmed(1);
                            rvAdapter.setMeetingsOnDay(meetingsOnDay);
                            rvAdapter.notifyDataSetChanged();
                            //TODO: Check if time is ok
                        } else {
                            Toasty.info(getContext(), "Die Ausgewählte Zeit muss sich mit der Ursprünglichen überschneiden", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, 0, 0, true);
                //tdp2.setTimeInterval(1, 15);
                tdp2.show(((Activity) getContext()).getFragmentManager(), "tag");
            }
        }, 0, 0, true);
        // tdp.setTimeInterval(1, 15);
        tdp.show(((Activity) getContext()).getFragmentManager(), "tag");

    }

    public void acceptMeeting(final Meeting meeting) {
        meetingsRepository.confirmMeeting(meeting)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                         Toasty.info(getContext(), "Meeting confirmed", Toast.LENGTH_SHORT).show();
                    }
                });

        position = meetingsOnDay.indexOf(meeting);
        meetingsOnDay.get(position).setConfirmed(1);
        rvAdapter.setMeetingsOnDay(meetingsOnDay);
        rvAdapter.notifyDataSetChanged();
        meetingsRepository.isMeetingReady(meeting)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<JsonObject>() {
                    @Override
                    public void onSuccess(@io.reactivex.annotations.NonNull JsonObject jsonElement) {
                        int x = 0;
                        x = jsonElement.get("confirmed_number").getAsInt();
                        if (x >= meeting.minParticipants) {
                            meetingsOnDay.get(position).status = 1;
                            rvAdapter.setMeetingsOnDay(meetingsOnDay);
                            rvAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        e.getMessage();
                    }
                });
    }

    public static class CustomComperator implements Comparator<Meeting> {

        @Override
        public int compare(Meeting o1, Meeting o2) {
            return o1.getStartDateTime().compareTo(o2.getStartDateTime());
        }
    }
}
