package com.example.alex.helloworld.CalendarActivity;

/**
 * Created by agemcipe on 31.10.16.
 */


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.example.alex.helloworld.Information;
import com.example.alex.helloworld.R;
import com.example.alex.helloworld.databaseConnection.DBconnection;
import com.example.alex.helloworld.databaseConnection.Database;
import com.example.alex.helloworld.databaseConnection.UIthread;
import com.google.gson.Gson;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import com.example.alex.helloworld.R.*;

import static java.lang.String.valueOf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by agemcipe on 21.10.16.
 */

public class CalendarActivity extends AppCompatActivity implements View.OnClickListener, UIthread {

    private int daySelected;
    private int colorPrimary;
    private int colorAccent;

    Button[] weekDayButtons;
    final static String DEBUG_TAG = "Yolo: ";
    private float x1, x2;
    RecyclerView recyclerView;
    RecyclerViewAdapter adapter;
    DBconnection dBconnection;

    ArrayList<Information> meetings = new ArrayList<>();

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_week);

        weekDayButtons = new Button[7];
        String[] btnText = {"Mo", "Di", "Mi", "Do", "Fr", "Sa", "So"};
        String[] btnTags = {"mondayButton","tuesdayButton", "wednesdayButton","thursdayButton","fridayButton", "saturdayButton","sundayButton"};

        LinearLayout btnLayout = (LinearLayout)findViewById(R.id.buttonLayout);
        for(int i=0; i<7; i++){
            int todayInWeek = LocalDate.now().getDayOfWeek()-1;
            Button day = new Button(this);
            day.setText(btnText[(todayInWeek+i)%7]);
            day.setOnClickListener(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            day.setLayoutParams(params);
            day.setTag(btnTags[(todayInWeek+i)%7]);

            btnLayout.addView(day);
            weekDayButtons[i] = day;
        }
        colorPrimary = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
        colorAccent = ContextCompat.getColor(getApplicationContext(), R.color.colorAccent);

        initButtons(weekDayButtons);

        //Loading of Meetings from database
        try {
            System.out.println("GETTING MEETINGS");
            getMeetings();
        } catch (InterruptedException | ParseException | JSONException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Call to Database class to update the UI through updateUI
     */
    public void getMeetings() throws InterruptedException, ExecutionException, ParseException, JSONException {

        SharedPreferences sharedPrefs = getBaseContext().getSharedPreferences("loginInformation", Context.MODE_PRIVATE);
        String email = sharedPrefs.getString("email", "");
        String[] params = {"IndexMeetings.php", "function", "getMeeting", "email", email};

        Database db = new Database(this, this.getApplicationContext());
        //meetingsOnDay is ArrayList<Information>
        db.execute(params);
    }

    @Override
    public void updateUI(){

        //get local Information
        SharedPreferences sharedPrefs = getSharedPreferences("meetingInformation", Context.MODE_PRIVATE);
        String meetingJson = sharedPrefs.getString("meetingJSON", "");
        try {
            meetings = Database.jsonToArrayList(meetingJson);
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }

        ArrayList<Information> meetingsOnDay = new ArrayList<Information>();
        int today = LocalDate.now().getDayOfYear();
        for(int i = 0; i< meetings.size(); i++){
            //System.out.println("MEETING "+i);
            String date = meetings.get(i).startTime.substring(0,10); //problem if no meetingsOnDay yet!?
            int dateOfMeeting = DateTimeFormat.forPattern("yyyy-MM-dd").parseLocalDate(date).getDayOfYear();
            if(dateOfMeeting==today+daySelected){
                meetingsOnDay.add(meetings.get(i));
                System.out.println("Today: "+meetings.get(i).MeetingID);
            }
        }

        recyclerView = (RecyclerView)findViewById(id.inviteView);
        adapter = new RecyclerViewAdapter(this, meetingsOnDay);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        System.out.println("Database Class initialized, layout updated");
    }

    @Override
    public void updateUI(String answer) {

    }

    private ArrayList<Information> jsonToArrayList(String stringFromJson) throws JSONException, ParseException {

        ArrayList<Information> data = new ArrayList<>();

        JSONObject jsonObject = new JSONObject(stringFromJson);

        int i=0;
        while(jsonObject.has(""+i)) {
            String meetingString = jsonObject.get(""+i).toString();

            Gson gson = new Gson();
            Information current = gson.fromJson(meetingString, Information.class);
            data.add(current);
            i++;
        }
        return data;
    }

    /**
     * function that changes the BackgroundResource of an array of Buttons expect the
     * the Button at index j
     *
     * the array of Buttons
     * @param buttons the array of Buttons
     * @param j       the index {@code int} of the button that does not change the background color
     * @param k       the int value of the color
     */
    private void setBackground(Button[] buttons, int j, int k) {
        for (int i = 0; i < buttons.length; i++) {
            if (i != j) {
                buttons[i].setBackgroundColor(k);
            }
        }
    }

    /**
     * function to initialize Buttons with onClickListener
     *
     * @param buttons the array of Buttons
     */
    private void initButtons(Button[] buttons) {
        //daySelected = ((Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 2) % 7 + 7) % 7;
        daySelected = LocalDate.now().getDayOfWeek()-1;
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setOnClickListener(this);
            buttons[i].setBackgroundColor(colorPrimary);
        }
        buttons[daySelected].setBackgroundColor(colorAccent);
    }

    /**
     * overriden onClick Method for weekDayButtons
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (valueOf(v.getTag())) {
            case "mondayButton":
                daySelected = ((LocalDate.now().plusWeeks(1).withDayOfWeek(DateTimeConstants.MONDAY).getDayOfYear())-(LocalDate.now().getDayOfYear()))%7;
                break;

            case "tuesdayButton":
                daySelected = ((LocalDate.now().plusWeeks(1).withDayOfWeek(DateTimeConstants.TUESDAY).getDayOfYear())-(LocalDate.now().getDayOfYear()))%7;
                break;

            case "wednesdayButton":
                daySelected = ((LocalDate.now().plusWeeks(1).withDayOfWeek(DateTimeConstants.WEDNESDAY).getDayOfYear())-(LocalDate.now().getDayOfYear()))%7;
                break;

            case "thursdayButton":
                daySelected = ((LocalDate.now().plusWeeks(1).withDayOfWeek(DateTimeConstants.THURSDAY).getDayOfYear())-(LocalDate.now().getDayOfYear()))%7;
                break;

            case "fridayButton":
                daySelected = ((LocalDate.now().plusWeeks(1).withDayOfWeek(DateTimeConstants.FRIDAY).getDayOfYear())-(LocalDate.now().getDayOfYear()))%7;
                break;

            case "saturdayButton":
                daySelected = ((LocalDate.now().plusWeeks(1).withDayOfWeek(DateTimeConstants.SATURDAY).getDayOfYear())-(LocalDate.now().getDayOfYear()))%7;
                break;

            case "sundayButton":
                daySelected = ((LocalDate.now().plusWeeks(1).withDayOfWeek(DateTimeConstants.SUNDAY).getDayOfYear())-(LocalDate.now().getDayOfYear()))%7;
                break;

            default:
                break;
        }
        weekDayButtons[daySelected].setBackgroundColor(colorAccent);
        setBackground(weekDayButtons, daySelected, colorPrimary);
        updateUI();
    }

    /**
     * overriden onTouchEvent function for swipe detection
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case (MotionEvent.ACTION_DOWN):
                Log.d(DEBUG_TAG, "Action was DOWN");
                x1 = event.getX();
                return true;
            case (MotionEvent.ACTION_MOVE):
                Log.d(DEBUG_TAG, "Action was MOVE");
                return true;
            case (MotionEvent.ACTION_UP):
                x2 = event.getX();
                if (x1 < x2) {
                    //left to right swipe
                    daySelected--;
                    if (daySelected < 0) daySelected = 0;
                    Log.d(DEBUG_TAG, "Left to Right Swipe detected");

                } else if (x1 > x2) {
                    //right to left swipe
                    daySelected++;
                    if (daySelected > 6) daySelected = 6;
                    Log.d(DEBUG_TAG, "Right to Left Swipe detected");
                }
                weekDayButtons[daySelected].setBackgroundColor(colorAccent);
                setBackground(weekDayButtons, daySelected, colorPrimary);
                updateUI(); // day as parameter!

                Log.d(DEBUG_TAG, "Action was UP");

                return true;
            default:
                return super.onTouchEvent(event);
        }
    }
}
