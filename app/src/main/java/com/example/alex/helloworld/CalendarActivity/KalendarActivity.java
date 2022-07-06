package com.example.alex.helloworld.CalendarActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.alex.helloworld.Information;
import com.example.alex.helloworld.R;
import com.example.alex.helloworld.SlidingTabLayout.SlidingTabLayout;
import com.example.alex.helloworld.databaseConnection.Database;
import com.example.alex.helloworld.databaseConnection.UIthread;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONException;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;


/**
 * Created by alex on 12/2/2016.
 */

public class KalendarActivity extends AppCompatActivity implements UIthread {

    SlidingTabLayout slidingTabLayout;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kalendar_activity);

        //using Korbis SlidingTabLayouts doesnt work yet, no idea why... maybe too many tabs??

        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.tabLayout);
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return ContextCompat.getColor(getBaseContext(), R.color.colorAccent);
            }
        });

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), getApplicationContext(), 7));
        slidingTabLayout.setViewPager(viewPager);

        //###################
        //this should happen on start of the app and then only on reopen/update
        //###################
        //String[] params = {"IndexMeetings.php", "function", "getMeeting", "meetingID", meetingID};
        String[] params = {"IndexMeetings.php", "function", "getMeeting", "email", "alexa.reish@gmail.com"};
        Database db = new Database(this, this.getApplicationContext());
        //meetingsOnDay is ArrayList<Information>
        db.execute(params);
        //###################################################
        //Create updating spinner to show its still loading

    }

    @Override
    public void updateUI() {

        //Initialize the fragments again after data has been loaded

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), getApplicationContext(), 7));
        slidingTabLayout.setViewPager(viewPager);

        // needs to be adapted to keep current db info if refreshing not possible (no overwriting of sharedPreferences if no connection)
    }

    @Override
    public void updateUI(String answer) {

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private int numberOfTabs;

        public ViewPagerAdapter(FragmentManager fragmentManager, Context ApplicationContext, int numberOfTabs){
            super(fragmentManager);
            this.numberOfTabs = numberOfTabs;
        }

        @Override
        public Fragment getItem(int position) {
            //get local Information
            SharedPreferences sharedPrefs = getSharedPreferences("meetingInformation", Context.MODE_PRIVATE);
            String meetingJson = sharedPrefs.getString("meetingJSON", "");
            //
            System.out.println("THESE ARE THE MEETINGZ "+meetingJson); // Gives me nothing but success = 0 :O
            //
            ArrayList<Information> meetingsOnDay = new ArrayList<Information>();

            try {
                ArrayList<Information> meetings = Database.jsonToArrayList(meetingJson);
                int today = LocalDate.now().getDayOfYear();
                System.out.println("THIS IS TODAY "+today);
                for(int i = 0; i< meetings.size(); i++){
                    System.out.println("MEETING "+i);
                    String date = meetings.get(i).startTime.substring(0,10); //problem if no meetingsOnDay yet!?
                    int dateOfMeeting = DateTimeFormat.forPattern("yyyy-MM-dd").parseLocalDate(date).getDayOfYear();
                    if(dateOfMeeting==today+position){
                        meetingsOnDay.add(meetings.get(i));
                    }
                }
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
            return MeetingsFragment.newInstance(position, meetingsOnDay); //position+1 ???
        }

        @Override
        public int getCount() {
            return numberOfTabs;
        }
        @Override
        public CharSequence getPageTitle(int position){

            String[] btnText = {"Mo", "Di", "Mi", "Do", "Fr", "Sa", "So"};

            int todayInWeek = LocalDate.now().getDayOfWeek();
            /*for(int i=0; i<7; i++){
                tabLayout.addTab(tabLayout.newTab().setText(btnText[(todayInWeek+i)%7]));
            }*/
            return btnText[(todayInWeek+position)%7];

            //return super.getPageTitle(position);
        }

    }

}
