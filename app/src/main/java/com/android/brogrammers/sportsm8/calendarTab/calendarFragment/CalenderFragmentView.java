package com.android.brogrammers.sportsm8.calendarTab.calendarFragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.brogrammers.sportsm8.calendarTab.calendarFragment.viewPagerAdapter.CalendarViewPagerAdapter;
import com.android.brogrammers.sportsm8.calendarTab.calendarFragment.dayFragment.DayFragmentView;
import com.android.brogrammers.sportsm8.dataBaseConnection.repositories.impl.DatabaseMeetingsRepository;
import com.android.brogrammers.sportsm8.R;
import com.android.brogrammers.sportsm8.ViewHelperClass;
import com.android.brogrammers.sportsm8.dataBaseConnection.databaseClasses.Meeting;
import com.android.brogrammers.sportsm8.databinding.FragmentCalendarBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
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
 * {@link CalenderFragmentView.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CalenderFragmentView#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalenderFragmentView extends Fragment implements SwipeRefreshLayout.OnRefreshListener, CalendarFragmentViewInterface {
    CalendarViewPagerAdapter viewPagerAdapter;
    List<Meeting> meetings;
    Activity parentActivity;
    private OnFragmentInteractionListener mListener;

    CalenderFragmentPresenter presenter;
    private FragmentCalendarBinding binding;

    public CalenderFragmentView() {
        // Required empty public constructor
    }

    public static CalenderFragmentView newInstance(String param1, String param2) {
        return new CalenderFragmentView();
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCalendarBinding.inflate(inflater,container,false);
        View rootView = binding.getRoot();

        binding.swipeLayoutToRefreshCalendar.setOnRefreshListener(this);
        binding.swipeLayoutToRefreshCalendar.setRefreshing(true);


        binding.materialCalendarViewFragment.setOnDateChangedListener((OnDateSelectedListener) (widget, date, selected) -> {

            presenter.loadMeetings(new DateTime(date.getYear(),date.getMonth()+1,date.getDay(),0,0));
            ViewHelperClass.expand(binding.materialCalendarViewFragment, 250);
            scrollTo(0);
        });
        return rootView;
    }

    @Override
    public void displayMeetings(List<DayFragmentView> meetings, List<CalendarDay> highlights, DateTime startDate) {
        if(viewPagerAdapter!=null) {
            viewPagerAdapter.updateFragmentList(meetings);
            viewPagerAdapter.setStartDate(startDate);
            viewPagerAdapter.notifyDataSetChanged();
        }else{
            viewPagerAdapter = new CalendarViewPagerAdapter(this,parentActivity.getApplicationContext(),meetings);
            binding.viewPager.setAdapter(viewPagerAdapter);
            new TabLayoutMediator(binding.tabLayoutCalendar, binding.viewPager,
                    (tab, position) -> tab.setText("OBJECT " + (position + 1))
            ).attach();
//            binding.viewPager.addOnPageChangeListener(this);
            binding.tabLayoutCalendar.setSmoothScrollingEnabled(true);
        }
        customTabs(startDate);
        createHighlightList(highlights);
        binding.swipeLayoutToRefreshCalendar.setRefreshing(false);
    }

    @Override
    public void displayNoMeetings() {
         Toasty.info(getContext(), "No Meetings! Create some!", Toast.LENGTH_SHORT).show();
        binding.swipeLayoutToRefreshCalendar.setRefreshing(false);
    }

    private void customTabs(DateTime startDate) {
        for (int i = 0; i < binding.tabLayoutCalendar.getTabCount(); i++) {
            TabLayout.Tab tab = binding.tabLayoutCalendar.getTabAt(i);
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
        dt.addDays(binding.tabLayoutCalendar.getSelectedTabPosition());
        return dt;
    }

    public void scrollTo(int i) {
        if (binding.tabLayoutCalendar.getTabAt(i) != null) {
            binding.tabLayoutCalendar.getTabAt(i).select();
        }
    }

//    @Override
//    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//    }
//
//    @Override
//    public void onPageSelected(int position) {
//        int count = binding.tabLayoutCalendar.getTabCount();
//        if (count < 60) {
//            if (count - position == 1) {
//                Toasty.info(parentActivity, "Neue Tage geladen", Toast.LENGTH_SHORT).show();
//                viewPagerAdapter.addFragmentsToList(presenter.getNextDays(7,position));
//                customTabs(viewPagerAdapter.getCurrentStartDate());
//                scrollTo(position);
//            }
//        }
//    }
//
//    @Override
//    public void onPageScrollStateChanged(int state) {
//
//    }

    public void toggleView(View view) {
        int id = view.getId();
        if (id == R.id.setLocation) {
            ViewHelperClass.expand(binding.textviewLocationFilter, 250);
        } else if (id == R.id.change_start_date) {
            ViewHelperClass.expand(binding.materialCalendarViewFragment, 250);
        }
    }

    public void createHighlightList(List<CalendarDay> highlights) {
        EventDecorator eventDecorator = new EventDecorator(ContextCompat.getColor(getContext(), R.color.red), highlights);
        binding.materialCalendarViewFragment.addDecorator(eventDecorator);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        binding.materialCalendarViewFragment.setTileHeightDp(35);
        binding.materialCalendarViewFragment.setTileWidth(displayMetrics.widthPixels / 8);
    }

    public void setLocation(double longitude, double latitude, boolean locationMode) {
        presenter.setLocation(longitude,latitude,locationMode,binding.tabLayoutCalendar.getTabCount());
    }

    public void setFilterText(CharSequence text) {
        binding.textviewLocationFilter.setText(getString(R.string.meetings_location_display,text));
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
