package com.android.brogrammers.sportsm8.CalendarTab;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.BaseObservable;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.brogrammers.sportsm8.DataBaseConnection.RetroFitDatabase.APIService;
import com.android.brogrammers.sportsm8.DataBaseConnection.RetroFitDatabase.APIUtils;
import com.android.brogrammers.sportsm8.DataBaseConnection.RetroFitDatabase.DatabaseClasses.Group;
import com.android.brogrammers.sportsm8.DataBaseConnection.RetroFitDatabase.DatabaseClasses.Sport;
import com.android.brogrammers.sportsm8.DataBaseConnection.RetroFitDatabase.DatabaseClasses.UserInfo;
import com.android.brogrammers.sportsm8.R;
import com.android.brogrammers.sportsm8.SocialTab.SelectorContainer;
import com.android.brogrammers.sportsm8.ViewHelperClass;
import com.android.brogrammers.sportsm8.databinding.ActivityCreateNewMeetingTwoBinding;
import com.android.databinding.library.baseAdapters.BR;
import com.schibstedspain.leku.LocationPickerActivity;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;
import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.observers.DisposableSingleObserver;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Korbi on 22.10.2016.
 */

public class CreateNewMeeting extends AppCompatActivity implements View.OnClickListener {

    int minMemberCount = 4, minHours = 2;
    List<Sport> sportIDs;
    String[] sportArten;

    int sportart_ID = 8008;
    List<UserInfo> Selection = new ArrayList<>();
    List<Group> SelectionGroup = new ArrayList<>();
    private Boolean start;
    private MutableDateTime startTime, endTime;
    private DateTime datetime, selectedDate;
    private DateTimeFormatter formatter;
    private boolean enoughPeopleInvited = false;
    private double latitude = 0, longitude = 0;
    private APIService apiService = APIUtils.getAPIService();

    @BindView(R.id.edittext_choose_activity)
    EditText editTextChooseActivity;
    @BindView(R.id.textview_min_meeting_time)
    TextView minTimeTextView;
    @BindView(R.id.textview_min_party_size)
    TextView minPartySizeTextView;
    @BindView(R.id.button_begin_time)
    Button startTimeButton;
    @BindView(R.id.button_end_time)
    Button endTimeButton;
    @BindView(R.id.button_begin_date)
    Button startDateButton;
    @BindView(R.id.button_end_date)
    Button endDateButton;
    @BindView(R.id.textview_add_friends)
    TextView addFriendsButton;
    @BindView(R.id.check_dynamic)
    Switch checkSwitch;
    @BindView(R.id.more_settings_LL)
    LinearLayout moreSettings;
    @BindView(R.id.listview_meeting_activities)
    public ListView listView_activities;
    @BindView(R.id.imageview_expand_arrow)
    ImageView expandArrow;
    @BindView(R.id.location_RL)
    RelativeLayout location;
    @BindView(R.id.button_clear_choose_activity)
    ImageButton clearEditText;

    private String extraInfoString;
    private int step = 0;


    public int dynamic = 1;
    private Intent intent = new Intent();
    Boolean bBegin = false, bEnd = false, bDate = false;
    private ActivityCreateNewMeetingTwoBinding binding;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_new_meeting_two);
        binding.setNewMeeting(this);
        ButterKnife.bind(this);
        start = true;
        extraInfoString = "";

        formatter = DateTimeFormat.forPattern("MM-dd-YYYY HH:mm:ss");
        datetime = new DateTime();
        startTime = new MutableDateTime();
        endTime = new MutableDateTime();
        createList();
        //SearchView
        editTextChooseActivity.setVisibility(View.VISIBLE);
        editTextChooseActivity.clearFocus();

        startDateButton.setText(datetime.toString("EE., dd. MMM. yyyy"));
        endDateButton.setText(datetime.toString("EE., dd. MMM. yyyy"));
        startTimeButton.setText(datetime.toString("HH:mm"));
        endTimeButton.setText(datetime.toString("HH:mm"));
        minTimeTextView.setText(minHours + "");
        minPartySizeTextView.setText(minMemberCount + "");
        location.setOnClickListener(this);
        //startCycle
        timeButtons(startTimeButton);
        editTextChooseActivity.setHintTextColor(ContextCompat.getColor(getBaseContext(), R.color.WhiteTransparent));
        editTextChooseActivity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard();
                    ViewHelperClass.expand(clearEditText, 255);
                } else {
                    ViewHelperClass.expand(clearEditText, 255);
                }
            }
        });

        intent = getIntent();
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("dd/MM/yyyy");
        selectedDate = DateTime.parse(intent.getStringExtra("date"), dateTimeFormatter);
        selectedDate.plusDays(1);

    }

    @OnCheckedChanged(R.id.check_dynamic)
    public void onCheckedChanged(boolean checked) {
        if (checked) {
            int minutes = (startTime.getMinuteOfHour() - startTime.getMinuteOfHour() % 15);
            startTimeButton.setText(startTime.toString("HH:" + String.format("%02d", minutes)));
            minutes = (endTime.getMinuteOfHour() - endTime.getMinuteOfHour() % 15);
            endTimeButton.setText(endTime.toString("HH:" + String.format("%02d", minutes)));
        } else {
            startTimeButton.setText(startTime.toString("HH:mm"));
            endTimeButton.setText(endTime.toString("HH:mm"));
        }
    }

    @OnClick(R.id.cancel_button)
    public void cancel() {
        editTextChooseActivity.clearFocus();
        finish();
    }


    @OnClick(R.id.add_friends_RL)
    void addFriends() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("SelectionMode", true);
        bundle.putSerializable("Selection", new ArrayList<>(Selection));
        Intent intent = new Intent(this, SelectorContainer.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, 2);
    }


    @OnClick(R.id.button_clear_choose_activity)
    public void clear() {
        editTextChooseActivity.setText("");
    }

    @OnClick(R.id.min_meeting_time_RL)
    void setMinTime() {
        createNumberPickerDialog("Wie viele Stunden?", minHours, 24, false);
    }

    @OnClick(R.id.min_party_size_RL)
    void setMinPartySize() {
        createNumberPickerDialog("Ab wie vielen Leuten?", minMemberCount, Selection.size(), true);
    }

    @OnClick({R.id.button_begin_time, R.id.button_end_time})
    public void timeButtons(View view) {
        switch (view.getId()) {
            case R.id.button_begin_time:
                TimePickerDialog tdp = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                        startTime.set(DateTimeFieldType.hourOfDay(), hourOfDay);
                        startTime.set(DateTimeFieldType.minuteOfHour(), minute);
                        startTimeButton.setText(startTime.toString("HH:mm"));
                        bBegin = true;
                        if (step == 0) {
                            step++;
                            timeButtons(endTimeButton);
                        }
                    }
                }, startTime.getHourOfDay(), startTime.getMinuteOfHour(), true);
//                if (checkSwitch.isChecked()) tdp.setTimeInterval(1, 15);
                tdp.show(getFragmentManager(), "TimePickerDialog");
                break;
            case R.id.button_end_time:
                TimePickerDialog tdp2 = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                        endTime.set(DateTimeFieldType.hourOfDay(), hourOfDay);
                        endTime.set(DateTimeFieldType.minuteOfHour(), minute);
                        endTimeButton.setText(endTime.toString("HH:mm"));
                        bEnd = true;
                        if (step == 1) {
                            step++;
                            dateButton(startDateButton);
                        }
                    }
                }, startTime.getHourOfDay() + 2, startTime.getMinuteOfHour(), true);
//                if (checkSwitch.isChecked()) tdp2.setTimeInterval(1, 15);
                tdp2.show(getFragmentManager(), "TimePickerDialog");
                break;
        }


    }


    @OnClick(R.id.more_settings_RL)
    public void expandSettings() {
        expandArrow.animate().rotation(expandArrow.getRotation() == 180 ? 0 : 180);
        if (moreSettings.getAlpha() == 0) {
            moreSettings.setVisibility(View.VISIBLE);
            moreSettings.animate().alpha(1);
        } else {
            moreSettings.animate().alpha(0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (moreSettings.getAlpha() == 0) {
                        moreSettings.setVisibility(View.GONE);
                    }
                }
            });
        }
    }


    @OnClick({R.id.button_begin_date, R.id.button_end_date})
    public void dateButton(View view) {
        switch (view.getId()) {
            case R.id.button_begin_date:
                new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        startTime.setDate(year, month + 1, dayOfMonth);
                        endTime.setDate(year, month + 1, dayOfMonth);
                        startDateButton.setText(endTime.toString("EE., dd. MMM. yyyy"));
                        endDateButton.setText(endTime.toString("EE., dd. MMM. yyyy"));
                        bDate = true;
                    }
                }, selectedDate.getYear(), selectedDate.getMonthOfYear() - 1, selectedDate.getDayOfMonth()).show();
                break;
            case R.id.button_end_date:
                new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        startTime.setDate(year, month + 1, dayOfMonth);
                        endTime.setDate(year, month + 1, dayOfMonth);
                        endDateButton.setText(endTime.toString("EE., dd. MMM. yyyy"));
                    }
                }, selectedDate.getYear(), selectedDate.getMonthOfYear() - 1, selectedDate.getDayOfMonth()).show();
                break;
        }

    }

    @OnClick(R.id.save_meeting)
    public void createMeeting(View view) {
        if (enoughPeopleInvited) {
            if (minMemberCount != 0 && minHours != 0) {
                if (startTime.isBefore(endTime)) {
//                    if (checkSwitch.isChecked()) {
//                        dynamic = 1;
//                        startTime.setMinuteOfHour((startTime.getMinuteOfHour() - startTime.getMinuteOfHour() % 15));
//                        endTime.setMinuteOfHour((endTime.getMinuteOfHour() - endTime.getMinuteOfHour() % 15));
//                    }
                    SharedPreferences sharedPrefs = getBaseContext().getSharedPreferences("loginInformation", Context.MODE_PRIVATE);
                    String email = sharedPrefs.getString("email", "");
                    Map<String, String> members = new HashMap<>();
                    for (int i = 0; i < Selection.size(); i++) {
                        members.put("members" + i, Selection.get(i).email);
                    }
                    apiService.createMeeting(formatter.print(startTime), formatter.print(endTime), minMemberCount, email, extraInfoString, sportart_ID, dynamic, members, longitude, latitude)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action() {
                                @Override
                                public void run() throws Exception {
                                    Toasty.success(getBaseContext(), "Neues Meeting erstellt", Toast.LENGTH_SHORT).show();
                                    setResult(RESULT_OK, intent);
                                    finish();
                                }
                            });
                } else {
                    Toasty.error(this, "Falsche Zeit eingestellt", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toasty.error(this, "Nicht alle Felder ausgefüllt", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toasty.error(this, "Nicht genug Leute eingeladen", Toast.LENGTH_SHORT).show();
        }


    }


    private void createNumberPickerDialog(String message, int current, int max, final Boolean partySize) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final NumberPicker numberPicker = new NumberPicker(this);
        numberPicker.setMaxValue(max);
        numberPicker.setMinValue(0);
        numberPicker.setValue(current);
        numberPicker.setWrapSelectorWheel(false);
        builder.setMessage(message).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (partySize) {
                    minMemberCount = numberPicker.getValue();
                    minPartySizeTextView.setText(minMemberCount + "");
                    minPartySizeTextView.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.green));
                } else {
                    minHours = numberPicker.getValue();
                    minTimeTextView.setText(minHours + "");
                    minTimeTextView.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.green));
                }

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        }).setView(numberPicker).show();
    }


    public void cancelButton(View v) {
        finish();
    }


    public void mergeGroupsAndFriends() {
        for (int i = 0; i < SelectionGroup.size(); i++) {
            apiService.getGroupMembers("getGroupMembers", SelectionGroup.get(i).GroupID)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<List<UserInfo>>() {
                        @Override
                        public void onSuccess(@NonNull List<UserInfo> response) {

                            List<UserInfo> temp = response;
                            for (int j = 0; j < temp.size(); j++) {
                                Boolean tempBool = false;
                                for (int h = 0; h < Selection.size(); h++) {
                                    if (temp.get(j).email.equals(Selection.get(h).email)) {
                                        tempBool = true;
                                    }
                                }
                                if (!tempBool) {
                                    Selection.add(temp.get(j));
                                }
                            }
                            addFriendsButton.setText(Selection.size() + "  Teilnehmer hinzugefügt");
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {

                        }
                    });
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                Selection = (ArrayList<UserInfo>) bundle.getSerializable("partyList");
                SelectionGroup = (ArrayList<Group>) bundle.getSerializable("groupList");
            }
            mergeGroupsAndFriends();
            checkMemberCount();
        } else if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                longitude = data.getDoubleExtra(LocationPickerActivity.LONGITUDE, 0);
                latitude = data.getDoubleExtra(LocationPickerActivity.LATITUDE, 0);
            }
        }


    }


    private void checkMemberCount() {
        if (Selection.size() < minMemberCount) {

            addFriendsButton.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.red));
            enoughPeopleInvited = false;
        } else {
            addFriendsButton.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.green));
            enoughPeopleInvited = true;
        }
    }


    private void createList() {
        apiService.getSports("getData").enqueue(new Callback<List<Sport>>() {
            @Override
            public void onResponse(Call<List<Sport>> call, Response<List<Sport>> response) {
                sportIDs = response.body();
                sportArten = new String[sportIDs.size()];
                for (int i = 0; i < sportIDs.size(); i++) {
                    sportArten[i] = sportIDs.get(i).sportname;
                }
            }

            @Override
            public void onFailure(Call<List<Sport>> call, Throwable t) {

            }
        });

    }


    @OnEditorAction(R.id.edittext_choose_activity)
    public boolean enterPressed(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            hideKeyboard();
            listView_activities.setVisibility(View.GONE);
            editTextChooseActivity.clearFocus();
        }
        return true;
    }

    @OnTextChanged(R.id.edittext_choose_activity)
    public void onTextChanged(CharSequence text) {
        String newText = text.toString();
        listView_activities.setVisibility(View.VISIBLE);
        extraInfoString = newText;
        List<String> searchresults = new ArrayList<>();

        for (int i = 0; i < sportArten.length; i++) {
            if (sportArten[i].toLowerCase().contains(newText)) {
                searchresults.add(sportArten[i]);
            }
        }
        String searchresultsS[] = new String[searchresults.size()];
        searchresultsS = searchresults.toArray(searchresultsS);
        final String[] sResult = searchresultsS;
        extraInfoString = newText;
        if (sResult != null) {
            ArrayAdapter<String> myAdapater = new ArrayAdapter<>(this, R.layout.item_custom_spinner, R.id.search_textview_meeting, sResult);
            myAdapater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            listView_activities.setAdapter(myAdapater);

            listView_activities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    editTextChooseActivity.setText(sResult[position], TextView.BufferType.EDITABLE);
                    editTextChooseActivity.clearFocus();
                    hideKeyboard();
                    editTextChooseActivity.setCursorVisible(false);
                    listView_activities.setVisibility(View.GONE);
                    extraInfoString = sResult[position];
                    minMemberCount = sportIDs.get(position).minPartySize;
                    sportart_ID = sportIDs.get(position).sportID;
                    minPartySizeTextView.setText(minMemberCount + "");
                    checkMemberCount();
                }
            });
        }
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editTextChooseActivity.getWindowToken(), 0);
    }

    public void setDynamic(int dynamicValue) {
        dynamic = dynamicValue;
        findViewById(R.id.dynamic_time_button).setBackgroundColor(dynamic == 2 ? ContextCompat.getColor(this, R.color.colorPrimary) : ContextCompat.getColor(this, R.color.grey));
        findViewById(R.id.fluent_time_button).setBackgroundColor(dynamic == 1 ? ContextCompat.getColor(this, R.color.colorPrimary) : ContextCompat.getColor(this, R.color.grey));
        findViewById(R.id.fluent_time_button).setBackgroundColor(dynamic == 1 ? ContextCompat.getColor(this, R.color.colorPrimary) : ContextCompat.getColor(this, R.color.grey));
        findViewById(R.id.fixed_time_button).setBackgroundColor(dynamic == 0 ? ContextCompat.getColor(this, R.color.colorPrimary) : ContextCompat.getColor(this, R.color.grey));
    }

    public int getDynamic() {
        return dynamic;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new LocationPickerActivity.Builder()
                .withStreetHidden()
                .withCityHidden()
                .withZipCodeHidden()
                .withSatelliteViewHidden()
                .build(getApplicationContext());

        startActivityForResult(intent, 1);


//        int PLACE_PICKER_REQUEST = 1;
//        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
//        try {
//            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
//        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
//            e.printStackTrace();
//        }
    }
}