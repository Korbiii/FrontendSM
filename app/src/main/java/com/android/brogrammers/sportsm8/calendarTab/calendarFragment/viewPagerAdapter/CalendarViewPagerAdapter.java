package com.android.brogrammers.sportsm8.calendarTab.calendarFragment.viewPagerAdapter;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.android.brogrammers.sportsm8.calendarTab.calendarFragment.dayFragment.DayFragmentView;
import com.android.brogrammers.sportsm8.R;
import com.android.brogrammers.sportsm8.dataBaseConnection.databaseClasses.Meeting;
import com.android.brogrammers.sportsm8.databinding.TabItemBinding;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class CalendarViewPagerAdapter extends FragmentStateAdapter {

    private final List<DayFragmentView> fragmentList = new ArrayList<>();
    private final Context context;

    public CalendarViewPagerAdapter(Fragment fragment, Context ApplicationContext, List<DayFragmentView> meetings) {
        super(fragment);
        fragmentList.clear();
        this.context = ApplicationContext;
        fragmentList.addAll(meetings);
    }


    public View getTabView(DateTime todayPosition,int position) {
        TabItemBinding tabItemBinding = TabItemBinding.inflate(LayoutInflater.from(context));
        View rootView = tabItemBinding.getRoot();

        tabItemBinding.textViewDate.setText(context.getString(R.string.date_calendar_tabs,todayPosition.getDayOfMonth(),todayPosition.getMonthOfYear()));
        tabItemBinding.textViewDay.setText(todayPosition.toString("E"));
        DayFragmentView dayFragmentView = (DayFragmentView) this.createFragment(position);
        List<Meeting> onDay = dayFragmentView.getMeetingsOnDay();
        if (onDay != null) {
            if (onDay.size() > 0) {
                tabItemBinding.textViewDate.setTypeface(Typeface.DEFAULT_BOLD);
                tabItemBinding.textViewDate.setPaintFlags(tabItemBinding.textViewDate.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            }
            for (int i = 0; i < onDay.size(); i++) {
                if (onDay.get(i).getConfirmed() == 0) {
                    tabItemBinding.imgView.setVisibility(View.VISIBLE);
                }
                if(onDay.get(i).duration!=0){
                    tabItemBinding.imgView.setVisibility(View.GONE);
                }
            }

        }
        return rootView;
    }

    public void updateFragmentList(List<DayFragmentView> updatedList){
        fragmentList.clear();
        fragmentList.addAll(updatedList);
    }

    public void setStartDate(DateTime today) {
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }


}

