package com.example.alex.helloworld.CreateNewMeeting;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.alex.helloworld.Friends.Friends;
import com.example.alex.helloworld.Information;
import com.example.alex.helloworld.R;
import com.example.alex.helloworld.databaseConnection.AsyncResponse;
import com.example.alex.helloworld.databaseConnection.DBconnection;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Korbi on 22.10.2016.
 */

public class CreateNewMeeting extends Activity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener,TextWatcher {

    int numP = 4, minHours = 2;
    Button SelectedButton;

    int sportart_ID = -1;
    ArrayList<Information> Selection = new ArrayList<>();
    private EditText additionalInfos;
    private Boolean endOrStart;
    private MutableDateTime startTime, endTime;
    private DateTime datetime;
    private DateTimeFormatter formatter;
    private Button startTime_b,endTime_b,date_b;
    private String extraInfoString;
    private TextView text_minHour;
    private TextView text_minParti;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_new_meeting);
        endOrStart = false;
        extraInfoString ="";

        //Popup größe
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        getWindow().setLayout((int) (width * .8), (int) (width * .8));

        //sportartID
        Bundle b = getIntent().getExtras();
        if (b != null) {
            sportart_ID = b.getInt("sportID");
        }
        formatter = DateTimeFormat.forPattern("MM-dd-YYYY HH:mm:ss");
        datetime = new DateTime();
        startTime = new MutableDateTime();
        startTime.set(DateTimeFieldType.secondOfMinute(), 0);
        startTime.set(DateTimeFieldType.year(), 0);
        endTime = new MutableDateTime();
        endTime.set(DateTimeFieldType.secondOfMinute(), 0);
        endTime.set(DateTimeFieldType.year(), 0);
        additionalInfos = (EditText) findViewById(R.id.editText_additional);
        additionalInfos.setSingleLine();
        additionalInfos.addTextChangedListener(this);
        text_minHour = (TextView) findViewById(R.id.textview_num_hour);
        text_minHour.setText(minHours + "");
        text_minParti = (TextView) findViewById(R.id.textview_num);
        text_minParti.setText(numP + "");
        startTime_b = (Button) findViewById(R.id.button_beginn);
        endTime_b = (Button) findViewById(R.id.button_ende);
        date_b = (Button) findViewById(R.id.button_datum);
        startTime_b.setText(startTime.toString("HH:mm"));
        endTime_b.setText(startTime.toString("HH:mm"));
        date_b.setText(datetime.toString("MM.dd.YY"));




    }

    public void timeButtons(View view) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, this, datetime.getHourOfDay(), datetime.getMinuteOfHour(), true);
        timePickerDialog.show();
        SelectedButton = (Button) view;
        switch (view.getId()) {
            case R.id.button_ende:
                endOrStart = false;
                break;
            case R.id.button_beginn:
                endOrStart = true;
                break;
        }

    }

    public void dateButton(View view) {
        int x = datetime.getMonthOfYear();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, datetime.getYear(), datetime.getMonthOfYear() - 1, datetime.getDayOfMonth());
        datePickerDialog.show();
        SelectedButton = (Button) view;

    }

    public void memberCountButton(View view) {
        Button minus = (Button) findViewById(R.id.button_minus);
        switch (view.getId()) {
            case R.id.button_plus:
                numP++;
                text_minParti.setText("" + numP);
                minus.setEnabled(true);
                break;
            case R.id.button_minus:
                if (numP > 0) {
                    numP--;
                    if (numP == 0) {
                        minus.setEnabled(false);
                    }
                }
                text_minParti.setText("" + numP);
                break;
        }
    }

    public void cancelButton(View v) {
        finish();
    }

    public void okButton(View v) {

        if (startTime.get(DateTimeFieldType.millisOfSecond()) == 0 && endTime.get(DateTimeFieldType.millisOfSecond()) == 0 && startTime.get(DateTimeFieldType.year()) != 0 && numP != 0) {
            if (startTime.isBefore(endTime)) {
                SharedPreferences sharedPrefs = getBaseContext().getSharedPreferences("loginInformation", Context.MODE_PRIVATE);
                String email = sharedPrefs.getString("email", "");
                ArrayList<String> paramsArrayList = new ArrayList<>(
                        Arrays.asList("/IndexMeetings.php", "function", "newMeeting", "startTime",formatter.print(startTime), "endTime", formatter.print(endTime),"minPar",numP+"","member", email,"activity",extraInfoString,"sportID",""+sportart_ID)
                );
                for (int i = 0; i < Selection.size(); i++) {
                    paramsArrayList.add("member" + i);
                    paramsArrayList.add(Selection.get(i).email);
                }
                String[] params = new String[paramsArrayList.size()];
                params = paramsArrayList.toArray(params);

                new DBconnection(new AsyncResponse() {
                    @Override
                    public void processFinish(String output) throws ParseException, JSONException {
                        Toast.makeText(getBaseContext(), "Neues Meeting erstellt", Toast.LENGTH_SHORT).show();
                    }
                }).execute(params);
                finish();

            } else {
                Toast.makeText(this, R.string.setTimeWrong, Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, R.string.text_empty_fields, Toast.LENGTH_SHORT).show();
        }

    }

    public void addPartiMembersButton(View view) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("SelectionMode", true);
        Intent intent = new Intent(this, Friends.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, 1);
    }

    public void minTimeButton(View view) {
        Button minus = (Button) findViewById(R.id.button_minus_hour);
        switch (view.getId()) {
            case R.id.button_plus_hour:
                minHours++;
                text_minHour.setText("" + minHours);
                minus.setEnabled(true);
                break;
            case R.id.button_minus_hour:
                if (minHours > 0) {
                    minHours--;
                    if (minHours == 0) {
                        minus.setEnabled(false);
                    }
                }
                text_minHour.setText("" + minHours);
                break;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        startTime.setDate(year, month + 1, dayOfMonth);
        endTime.setDate(year, month + 1, dayOfMonth);
        SelectedButton.setText(endTime.toString("dd.MM.YYYY"));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (endOrStart) {
            startTime.set(DateTimeFieldType.hourOfDay(), hourOfDay);
            startTime.set(DateTimeFieldType.minuteOfHour(), minute);
            startTime.set(DateTimeFieldType.millisOfSecond(), 0);
            SelectedButton.setText(startTime.toString("HH:mm"));

        } else {
            endTime.set(DateTimeFieldType.hourOfDay(), hourOfDay);
            endTime.set(DateTimeFieldType.minuteOfHour(), minute);
            endTime.set(DateTimeFieldType.millisOfSecond(), 0);
            SelectedButton.setText(endTime.toString("HH:mm"));
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {

                String[] params = {"/IndexMeetings.php", "function", "newMeeting", "startTime", formatter.print(startTime), "endTime", formatter.print(endTime)};
                Bundle bundle = data.getExtras();
                Selection = (ArrayList<Information>) bundle.getSerializable("partyList");
                TextView textView = (TextView) findViewById(R.id.number_added);
                textView.setText(Selection.size() + " Teilnehmer");
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        extraInfoString = additionalInfos.getText().toString();
        Toast.makeText(CreateNewMeeting.this, extraInfoString, Toast.LENGTH_SHORT).show();
    }

}