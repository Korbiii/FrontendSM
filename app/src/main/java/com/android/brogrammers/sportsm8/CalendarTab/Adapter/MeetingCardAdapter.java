package com.android.brogrammers.sportsm8.CalendarTab.Adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.android.brogrammers.sportsm8.BR;
import com.android.brogrammers.sportsm8.CalendarTab.CalendarFragmentMVP.DayFragment;
import com.android.brogrammers.sportsm8.DataBaseConnection.ApiServices.MeetingApiService;
import com.android.brogrammers.sportsm8.R;
import com.android.brogrammers.sportsm8.ZZOldClassers.DatabaseHelperMeetings;
import com.android.brogrammers.sportsm8.DataBaseConnection.APIUtils;
import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.Match;
import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.Meeting;
import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.TimeCalcObject;
import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.UserInfo;
import com.android.brogrammers.sportsm8.databinding.ItemMatchBinding;
import com.android.brogrammers.sportsm8.databinding.ItemMeetingsBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableSingleObserver;

public class MeetingCardAdapter extends RecyclerView.Adapter<MeetingCardAdapter.MeetingsViewHolder> {

    private static final int MATCH = 1;
    private static final int MEETING = 2;
    private Context context;
    private List<Meeting> meetingsOnDay;
    private List<Match> matchesOnDay = new ArrayList<>();
    private MeetingsViewHolder meetingsViewHolder;
    private DayFragment dayFragment;

    public MeetingCardAdapter(Context context, List<Meeting> meetingsOnDay, DayFragment dayFragment) {
        this.context = context;
        this.meetingsOnDay = meetingsOnDay;
        this.dayFragment = dayFragment;
    }

    //doesnt need an int position because the card looks the same for all; still this is iterated through before and just like onBindViewHolder!
    @Override
    public MeetingsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == MEETING) {
            ItemMeetingsBinding viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_meetings, parent, false);
            viewDataBinding.setPresenter(dayFragment);
            return new MeetingsViewHolder(viewDataBinding);
        } else {
            ItemMatchBinding viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_match, parent, false);
            return new MeetingsViewHolder(viewDataBinding);
        }
        //this is what the recyclerViewAdapter returns to the recyclerView. Equivalent to FragmentPagerAdapter's getItem which returns the Fragment
    }

    //onBindViewholder seems to be iterated through, probably getItemCount() times!!
    @Override
    public void onBindViewHolder(MeetingsViewHolder meetingsViewHolder, int position) {
        this.meetingsViewHolder = meetingsViewHolder;
        ViewDataBinding viewDataBinding = this.meetingsViewHolder.getViewDataBinding();

        switch (meetingsViewHolder.getItemViewType()) {
            case MEETING:
                int positionInMeeting = position - matchesOnDay.size();
                viewDataBinding.setVariable(BR.meeting, meetingsOnDay.get(positionInMeeting));
                if (meetingsOnDay.get(positionInMeeting).dynamic == 1)
                    getOptimalTime(positionInMeeting);
                break;
            case MATCH:
                viewDataBinding.setVariable(BR.match, matchesOnDay.get(position));
                break;

        }
        viewDataBinding.executePendingBindings();


    }


    private void getOptimalTime(final int position) {
        MeetingApiService apiService = APIUtils.getMeetingAPIService();
        apiService.getMeetingMemberTimes(meetingsOnDay.get(position).MeetingID)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<UserInfo>>() {
                    @Override
                    public void onSuccess(@NonNull List<UserInfo> userInfos) {
                        if (userInfos != null) {
                            UserInfo timeObject = calculateTime(userInfos, meetingsOnDay.get(position).minParticipants);
                            if (timeObject.myendTime != null) {
                                meetingsOnDay.get(position).setStartTime(timeObject.mystartTime);
                                meetingsOnDay.get(position).setEndTime(timeObject.myendTime);
                                notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }
                });
    }

    private UserInfo calculateTime(List<UserInfo> timeList, int min) {
        UserInfo userInfo = new UserInfo();
        List<TimeCalcObject> timeCalcList = new ArrayList<>();
        for (int i = 0; i < timeList.size(); i++) {
            timeCalcList.add(new TimeCalcObject(timeList.get(i).mystartTime, 1));
            timeCalcList.add(new TimeCalcObject(timeList.get(i).myendTime, -1));
        }
        Collections.sort(timeCalcList, new Comparator<TimeCalcObject>() {
            @Override
            public int compare(TimeCalcObject o1, TimeCalcObject o2) {
                return o1.time.compareTo(o2.time);
            }
        });
        boolean startSet = false;
        boolean timeFound = false;
        for (int i = 1; i < timeCalcList.size() && !timeFound; i++) {
            timeCalcList.get(i).number = timeCalcList.get(i - 1).number + timeCalcList.get(i).minusOrPlus;
            if (timeCalcList.get(i).number >= min && !startSet) {
                userInfo.setMystartTime(timeCalcList.get(i).time);
                startSet = true;
            }
            if (startSet && timeCalcList.get(i).number < min) {
                userInfo.setMyendTime(timeCalcList.get(i).time);
                timeFound = true;
            }
        }
        return userInfo;
    }


    public void setMeetingsOnDay(List<Meeting> meetingsOnDay) {
        this.meetingsOnDay = meetingsOnDay;
    }

    public void removeItem(Meeting infoData) {
        int position = meetingsOnDay.indexOf(infoData);
        meetingsOnDay.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }

    @Override
    public int getItemCount() {
        return meetingsOnDay.size() + matchesOnDay.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (matchesOnDay.size() <= position) {
            return MEETING;
        } else {
            return MATCH;
        }
    }

    class MeetingsViewHolder extends RecyclerView.ViewHolder {
        private ViewDataBinding mViewDataBinding;

        public MeetingsViewHolder(ViewDataBinding viewDataBinding) {
            super(viewDataBinding.getRoot());
            mViewDataBinding = viewDataBinding;
//            ButterKnife.bind(this, itemView);
        }

        public ViewDataBinding getViewDataBinding() {
            return mViewDataBinding;
        }


    }

}
