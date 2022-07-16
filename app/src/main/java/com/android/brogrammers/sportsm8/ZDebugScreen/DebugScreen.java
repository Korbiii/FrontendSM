package com.android.brogrammers.sportsm8.ZDebugScreen;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.android.brogrammers.sportsm8.DataBaseConnection.APIUtils;
import com.android.brogrammers.sportsm8.DataBaseConnection.ApiServices.MeetingApiService;
import com.android.brogrammers.sportsm8.R;
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;

import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;

public class DebugScreen extends AppCompatActivity {

    @BindView(R.id.email_switcher)
    Button emailSwitch;
    int i;
    int newmeetings = 0;
    private MeetingApiService apiService = APIUtils.getMeetingAPIService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        SharedPreferences sharedPrefs = getSharedPreferences("loginInformation", Context.MODE_PRIVATE);
        emailSwitch.setText(sharedPrefs.getString("email", ""));
    }


    @OnClick(R.id.email_switcher)
    public void onClick(View view) {
        String[] emails = getResources().getStringArray(R.array.emails);
        SharedPreferences sharedPrefs = getSharedPreferences("loginInformation", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString("email", emails[i]);
        editor.apply();
        Button btn = (Button) findViewById(R.id.email_switcher);
        btn.setText(emails[i]);
        i++;
        i = i % emails.length;
    }

    @OnClick(R.id.random_meetings)
    public void createRandomMeetings() {
        SharedPreferences sharedPrefs = getBaseContext().getSharedPreferences("loginInformation", Context.MODE_PRIVATE);
        String email = sharedPrefs.getString("email", "");
        DateTimeFormatter formatter = DateTimeFormat.forPattern("MM-dd-YYYY HH:mm:ss");
        MutableDateTime randomStartTime = new MutableDateTime();
        MutableDateTime randomEndTime;
        Random random = new Random();
        int randInt = random.nextInt(7);
        randomStartTime.addDays(randInt);
        randomStartTime.addHours(randInt);
        randomEndTime = randomStartTime;
        randomEndTime.addHours(2);
        Map<String, String> members = new HashMap<>();
        members.put("member1","Alex@alex.de");
        apiService.createMeeting(formatter.print(randomStartTime),formatter.print(randomEndTime),2,email,"Test Meeting",randInt,0,members,0,0)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        while (newmeetings < 10) {
                            createRandomMeetings();
                            newmeetings++;
                        }
                    }
                });
    }

    @OnClick(R.id.searchUpdates)
    public void searchForUpdates() {
        AppUpdater appUpdater = new AppUpdater(this)
                .setUpdateFrom(UpdateFrom.JSON)
                .setUpdateJSON("http://sportsm8.bplaced.net/Update/update.json")
                .setDisplay(Display.DIALOG)
                .showAppUpdated(true);
        appUpdater.start();

    }
}
