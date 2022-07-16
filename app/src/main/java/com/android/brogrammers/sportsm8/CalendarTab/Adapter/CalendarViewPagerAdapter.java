package com.android.brogrammers.sportsm8.CalendarTab.Adapter;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.android.brogrammers.sportsm8.CalendarTab.CalendarFragmentMVP.DayFragment;
import com.android.brogrammers.sportsm8.R;
import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.Meeting;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class CalendarViewPagerAdapter extends FragmentStatePagerAdapter {

    private List<DayFragment> fragmentList = new ArrayList<>();
    private Context context;
    private boolean needsUpdate=false;
    private DateTime startDate;

    public CalendarViewPagerAdapter(FragmentManager fragmentManager, Context ApplicationContext, List<DayFragment> meetings,DateTime currentStartDate) {
        super(fragmentManager);
        fragmentList.clear();
        this.context = ApplicationContext;
        startDate = currentStartDate;
        fragmentList.addAll(meetings);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }


    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public int getItemPosition(Object object) {
        // POSITION_NONE makes it possible to reload the PagerAdapter
        if (needsUpdate) {
            return POSITION_NONE;
        } else {
            return POSITION_UNCHANGED;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        DateTime todayPosition = startDate.plusDays(position);
        return todayPosition.getDayOfMonth() + "." + todayPosition.getMonthOfYear();
    }

    public View getTabView(DateTime todayPosition,int position) {
        View v = LayoutInflater.from(context).inflate(R.layout.tab_item, null);
        TextView tv = (TextView) v.findViewById(R.id.textView_date);
        TextView tv2 = (TextView) v.findViewById(R.id.textView_day);
        tv.setText(todayPosition.getDayOfMonth() + "." + todayPosition.getMonthOfYear());
        tv2.setText(todayPosition.toString("E"));
        DayFragment dayFragment = (DayFragment) this.getItem(position);
        List<Meeting> onDay = dayFragment.getMeetingsOnDay();
        if (onDay != null) {
            if (onDay.size() > 0) {
                tv.setTypeface(Typeface.DEFAULT_BOLD);
                tv.setPaintFlags(tv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            }
            for (int i = 0; i < onDay.size(); i++) {
                if (onDay.get(i).getConfirmed() == 0) {
                    v.findViewById(R.id.imgView).setVisibility(View.VISIBLE);
                }
                if(onDay.get(i).duration!=0){
                    v.findViewById(R.id.imgView).setVisibility(View.GONE);
                }
            }

        }
        return v;
    }

    public void updateFragmentList(List<DayFragment> updatedList){
        fragmentList.clear();
        fragmentList.addAll(updatedList);
    }

    public void addFragmentsToList(List<DayFragment> newDayFragments){
        fragmentList.addAll(newDayFragments);
        notifyDataSetChanged();
    }

    public void setNeedsUpdate(boolean needsUpdate) {
        this.needsUpdate = needsUpdate;
    }

    public void setStartDate(DateTime today) {
        this.startDate = today;
    }
    public DateTime getCurrentStartDate(){ return startDate;}
}

