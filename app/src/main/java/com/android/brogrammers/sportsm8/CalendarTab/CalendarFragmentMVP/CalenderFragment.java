package com.android.brogrammers.sportsm8.CalendarTab.CalendarFragmentMVP;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.brogrammers.sportsm8.CalendarTab.Adapter.CalendarViewPagerAdapter;
import com.android.brogrammers.sportsm8.DataBaseConnection.ApiServices.MeetingApiService;
import com.android.brogrammers.sportsm8.DataBaseConnection.Repositories.impl.DatabaseMeetingsRepository;
import com.android.brogrammers.sportsm8.R;
import com.android.brogrammers.sportsm8.ViewHelperClass;
import com.android.brogrammers.sportsm8.DataBaseConnection.APIUtils;
import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.Meeting;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CalenderFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CalenderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalenderFragment extends Fragment implements ViewPager.OnPageChangeListener, SwipeRefreshLayout.OnRefreshListener,CalendarFragmentView {
    ViewPager viewPager;
    CalendarViewPagerAdapter viewPagerAdapter;
    List<Meeting> meetings;
    TabLayout tabLayout;
    Activity parentActivity;
    SwipeRefreshLayout swipeRefreshLayout;
    MaterialCalendarView calendarView;
    TextView filterTV;
    private OnFragmentInteractionListener mListener;

    CalenderFragmentPresenter presenter;

    public CalenderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CalenderFragmentOld.
     */
    public static CalenderFragment newInstance(String param1, String param2) {
        return new CalenderFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new CalenderFragmentPresenter(this,new DatabaseMeetingsRepository(),AndroidSchedulers.mainThread());
        parentActivity = getActivity();
        meetings = new ArrayList<>();
        presenter.loadMeetings(new DateTime());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabLayout);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.calender_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setRefreshing(true);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
        calendarView = (MaterialCalendarView) rootView.findViewById(R.id.calendar_calendar);
        filterTV = (TextView) rootView.findViewById(R.id.filter_tv);

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                viewPagerAdapter.setNeedsUpdate(true);
                presenter.loadMeetings(new DateTime(date.getYear(),date.getMonth()+1,date.getDay(),0,0));
                ViewHelperClass.expand(calendarView, 250);
                scrollTo(0);
            }
        });
        return rootView;
    }

    @Override
    public void displayMeetings(List<DayFragment> meetings, List<CalendarDay> highlights,DateTime startDate) {
        if(viewPagerAdapter!=null) {
            viewPagerAdapter.updateFragmentList(meetings);
            viewPagerAdapter.setStartDate(startDate);
            viewPagerAdapter.notifyDataSetChanged();
        }else{
            viewPagerAdapter = new CalendarViewPagerAdapter(this.getChildFragmentManager(),parentActivity.getApplicationContext(),meetings,startDate);
            viewPager.setAdapter(viewPagerAdapter);
            viewPager.addOnPageChangeListener(this);
            tabLayout.setupWithViewPager(viewPager);
            tabLayout.setSmoothScrollingEnabled(true);
        }
        customTabs(startDate);
        createHighlightList(highlights);
        viewPagerAdapter.setNeedsUpdate(false);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void displayNoMeetings() {
         Toasty.info(getContext(), "No Meetings! Create some!", Toast.LENGTH_SHORT).show();
         swipeRefreshLayout.setRefreshing(false);
    }

    private void customTabs(DateTime startDate) {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(null);
            tab.setCustomView(viewPagerAdapter.getTabView(startDate.plusDays(i),i));
        }
    }

    @Override
    public void onRefresh() {
        presenter.refreshMeetings();
    }

    public MutableDateTime getSelectedDate() {
        MutableDateTime dt = new MutableDateTime();
        dt.addDays(tabLayout.getSelectedTabPosition());
        return dt;
    }

    public void scrollTo(int i) {
        if (tabLayout.getTabAt(i) != null) {
            tabLayout.getTabAt(i).select();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        int count = tabLayout.getTabCount();
        if (count < 60) {
            if (count - position == 1) {
                Toasty.info(parentActivity, "Neue Tage geladen", Toast.LENGTH_SHORT).show();
                viewPagerAdapter.addFragmentsToList(presenter.getNextDays(7,position));
                customTabs(viewPagerAdapter.getCurrentStartDate());
                scrollTo(position);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void toggleView(View view) {
        switch (view.getId()) {
            case R.id.setLocation:
                ViewHelperClass.expand(filterTV, 250);
                break;
            case R.id.change_start_date:
                ViewHelperClass.expand(calendarView, 250);
                break;
        }
    }

    public void createHighlightList(List<CalendarDay> highlights) {
        EventDecorator eventDecorator = new EventDecorator(ContextCompat.getColor(getContext(), R.color.red), highlights);
        calendarView.addDecorator(eventDecorator);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        calendarView.setTileHeightDp(35);
        calendarView.setTileWidth(displayMetrics.widthPixels / 8);
    }

    public void setLocation(double longitude, double latitude, boolean locationMode) {
        viewPagerAdapter.setNeedsUpdate(true);
        presenter.setLocation(longitude,latitude,locationMode,tabLayout.getTabCount());
    }

    public void setFilterText(CharSequence text) {
        filterTV.setText("Meetings in:  " + text);
    }




    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private class EventDecorator implements DayViewDecorator {

        private final int color;
        private final List<CalendarDay> dates = new ArrayList<>();

        EventDecorator(int color, List<CalendarDay> dates) {
            this.color = color;
            this.dates.addAll(dates);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            //  view.setBackgroundDrawable(ContextCompat.getDrawable(getContext(),R.drawable.ic_access_time_black_48dp));
            view.addSpan(new DotSpan(5, color));
        }
    }

}
