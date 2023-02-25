package com.android.brogrammers.sportsm8.calendarTab;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.core.content.ContextCompat;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.android.brogrammers.sportsm8.dataBaseConnection.apiServices.APIService;
import com.android.brogrammers.sportsm8.dataBaseConnection.APIUtils;
import com.android.brogrammers.sportsm8.dataBaseConnection.apiServices.GroupsApiService;
import com.android.brogrammers.sportsm8.dataBaseConnection.apiServices.MeetingApiService;
import com.android.brogrammers.sportsm8.dataBaseConnection.databaseClasses.Group;
import com.android.brogrammers.sportsm8.dataBaseConnection.databaseClasses.Sport;
import com.android.brogrammers.sportsm8.dataBaseConnection.databaseClasses.UserInfo;
import com.android.brogrammers.sportsm8.R;
import com.android.brogrammers.sportsm8.socialTab.SelectorContainer;
import com.android.brogrammers.sportsm8.ViewHelperClass;
import com.android.brogrammers.sportsm8.databinding.ActivityCreateNewMeetingTwoBinding;
import com.android.brogrammers.sportsm8.databinding.ContentCreateNewMeeting2Binding;
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
import java.util.Locale;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;


public class CreateNewMeeting extends AppCompatActivity {

    int minMemberCount = 4, minHours = 2;
    List<Sport> sportIDs;
    String[] sportArten;

    int sportart_ID = 8008;
    List<UserInfo> Selection = new ArrayList<>();
    List<Group> SelectionGroup = new ArrayList<>();
    private MutableDateTime startTime, endTime;
    private DateTime selectedDate;
    private DateTimeFormatter formatter;
    private boolean enoughPeopleInvited = false;
    private double latitude = 0, longitude = 0;
    private final GroupsApiService groupsAPIService = APIUtils.getGroupsAPIService();
    private final MeetingApiService meetingApiService = APIUtils.getMeetingAPIService();
    private final APIService apiService = APIUtils.getAPIService();
    private String extraInfoString;
    private int step = 0;


    public int dynamic = 1;
    private Intent intent = new Intent();
    Boolean bBegin = false, bEnd = false, bDate = false;
    private ActivityCreateNewMeetingTwoBinding binding;
    private ContentCreateNewMeeting2Binding include;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateNewMeetingTwoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        include = binding.include;
        binding.setNewMeeting(this);

        include.addFriendsRL.setOnClickListener(v -> addFriends());
        include.buttonMinMeetingTime.setOnClickListener(v->setMinTime());
        include.buttonMinPartySize.setOnClickListener(v-> setMinPartySize());
        include.moreSettingsRL.setOnClickListener(v-> expandSettings());
        include.rlLocation.setOnClickListener(v->chooseLocation());

        include.btnTimeBegin.setOnClickListener(this::timeButtons);
        include.btnTimeEnd.setOnClickListener(this::timeButtons);
        include.btnDateBegin.setOnClickListener(this::dateButton);
        include.btnDateEnd.setOnClickListener(this::dateButton);
        include.checkDynamic.setOnCheckedChangeListener(this::onCheckedChanged);

        binding.cancelButton.setOnClickListener(v -> cancel());
        binding.btnClearChooseActivity.setOnClickListener(v -> clear());
        binding.saveMeeting.setOnClickListener(v->createMeeting());
        binding.etChooseActivity.setOnEditorActionListener(this::enterPressed);

        binding.etChooseActivity.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                onTextChange(charSequence);
            }
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {}
        });


        extraInfoString = "";
        formatter = DateTimeFormat.forPattern("dd-MM-YYYY HH:mm:ss");
        startTime = new MutableDateTime();
        endTime = new MutableDateTime();
        endTime.addHours(minHours);
        createList();
        //SearchView
        binding.etChooseActivity.setVisibility(View.VISIBLE);
        binding.etChooseActivity.clearFocus();
        include.btnDateBegin.setText(startTime.toString("EE., dd. MMM. yyyy"));
        include.btnDateEnd.setText(endTime.toString("EE., dd. MMM. yyyy"));
        include.btnTimeBegin.setText(startTime.toString("HH:mm"));
        include.btnTimeEnd.setText(endTime.toString("HH:mm"));
        include.tvMinMeetingTime.setText(String.valueOf(minHours));
        include.tvMinPartySize.setText(String.valueOf(minMemberCount));
        //startCycle
        timeButtons(include.btnTimeBegin);
        binding.etChooseActivity.setHintTextColor(ContextCompat.getColor(getBaseContext(), R.color.WhiteTransparent));
        binding.etChooseActivity.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                hideKeyboard();
            }
            ViewHelperClass.expand(binding.btnClearChooseActivity, 255);
        });

        intent = getIntent();
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("dd/MM/yyyy");
        selectedDate = DateTime.parse(intent.getStringExtra("date"), dateTimeFormatter);
        selectedDate.plusDays(1);
    }

    public void onCheckedChanged(View view,boolean checked) {
        if (checked) {
            int minutes = (startTime.getMinuteOfHour() - startTime.getMinuteOfHour() % 15);
            include.btnTimeBegin.setText(startTime.toString("HH:" + String.format(Locale.GERMANY,"%02d", minutes)));
            minutes = (endTime.getMinuteOfHour() - endTime.getMinuteOfHour() % 15);
            include.btnTimeEnd.setText(endTime.toString("HH:" + String.format(Locale.GERMANY,"%02d", minutes)));
        } else {
            include.btnTimeBegin.setText(startTime.toString("HH:mm"));
            include.btnTimeEnd.setText(endTime.toString("HH:mm"));
        }
    }

    public void cancel() {
        binding.etChooseActivity.clearFocus();
        finish();
    }

    ActivityResultLauncher<Intent> startAddingFriendsActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Bundle bundle = result.getData().getExtras();
                        Selection = (ArrayList<UserInfo>) bundle.getSerializable("partyList");
                        SelectionGroup = (ArrayList<Group>) bundle.getSerializable("groupList");
                    }
                }
                mergeGroupsAndFriends();
                checkMemberCount();
            });

    void addFriends() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("SelectionMode", true);
        bundle.putSerializable("Selection", new ArrayList<>(Selection));
        Intent intent = new Intent(this, SelectorContainer.class);
        intent.putExtras(bundle);
        startAddingFriendsActivity.launch(intent);
    }


    public void clear() {
        binding.etChooseActivity.setText("");
    }

    void setMinTime() {
        createNumberPickerDialog("Wie viele Stunden?", minHours, 24, false);
    }

    void setMinPartySize() {
        createNumberPickerDialog("Ab wie vielen Leuten?", minMemberCount, 30, true);
    }

    public void timeButtons(View view) {
        int id = view.getId();
        if (id == R.id.btn_time_begin) {
            TimePickerDialog tdp = TimePickerDialog.newInstance((view12, hourOfDay, minute, second) -> {
                startTime.set(DateTimeFieldType.hourOfDay(), hourOfDay);
                startTime.set(DateTimeFieldType.minuteOfHour(), minute);
                include.btnTimeBegin.setText(startTime.toString("HH:mm"));
                bBegin = true;
                if (step == 0) {
                    step++;
                    timeButtons(include.btnTimeEnd);
                }
            }, startTime.getHourOfDay(), startTime.getMinuteOfHour(), true);
//                if (checkSwitch.isChecked()) tdp.setTimeInterval(1, 15);
            tdp.show(getFragmentManager(), "TimePickerDialog");
        } else if (id == R.id.btn_time_end) {
            TimePickerDialog tdp2 = TimePickerDialog.newInstance((view1, hourOfDay, minute, second) -> {
                endTime.set(DateTimeFieldType.hourOfDay(), hourOfDay);
                endTime.set(DateTimeFieldType.minuteOfHour(), minute);
                include.btnTimeEnd.setText(endTime.toString("HH:mm"));
                bEnd = true;
                if (step == 1) {
                    step++;
                    dateButton(include.btnDateBegin);
                }
            }, startTime.getHourOfDay() + 2, startTime.getMinuteOfHour(), true);
//                if (checkSwitch.isChecked()) tdp2.setTimeInterval(1, 15);
            tdp2.show(getFragmentManager(), "TimePickerDialog");
        }


    }

    public void expandSettings() {
        include.ivExpandArrow.animate().rotation(include.ivExpandArrow.getRotation() == 180 ? 0 : 180);
        if (include.llMoreSettings.getAlpha() == 0) {
            include.llMoreSettings.setVisibility(View.VISIBLE);
            include.llMoreSettings.animate().alpha(1);
        } else {
            include.llMoreSettings.animate().alpha(0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (include.llMoreSettings.getAlpha() == 0) {
                        include.llMoreSettings.setVisibility(View.GONE);
                    }
                }
            });
        }
    }



    public void dateButton(View view) {
        int id = view.getId();
        if (id == R.id.btn_date_begin) {
            new DatePickerDialog(this, (datePicker, year, month, dayOfMonth) -> {
                startTime.setDate(year, month + 1, dayOfMonth);
                endTime.setDate(year, month + 1, dayOfMonth);
                include.btnDateBegin.setText(endTime.toString("EE., dd. MMM. yyyy"));
                include.btnDateEnd.setText(endTime.toString("EE., dd. MMM. yyyy"));
                bDate = true;
            }, selectedDate.getYear(), selectedDate.getMonthOfYear() - 1, selectedDate.getDayOfMonth()).show();
        } else if (id == R.id.btn_date_end) {
            new DatePickerDialog(this, (datePicker, year, month, dayOfMonth) -> {
                startTime.setDate(year, month + 1, dayOfMonth);
                endTime.setDate(year, month + 1, dayOfMonth);
                include.btnDateEnd.setText(endTime.toString("EE., dd. MMM. yyyy"));
            }, selectedDate.getYear(), selectedDate.getMonthOfYear() - 1, selectedDate.getDayOfMonth()).show();
        }

    }

    public void createMeeting() {
        if (enoughPeopleInvited) {
            if (minMemberCount != 0 && minHours != 0) {
                if (startTime.isBefore(endTime)) {
                    SharedPreferences sharedPrefs = getBaseContext().getSharedPreferences("loginInformation", Context.MODE_PRIVATE);
                    String email = sharedPrefs.getString("email", "");
                    Map<String, String> members = new HashMap<>();
                    for (int i = 0; i < Selection.size(); i++) {
                        members.put("members" + i, Selection.get(i).email);
                    }
                    meetingApiService.createMeeting(formatter.print(startTime), formatter.print(endTime), minMemberCount, email, extraInfoString, sportart_ID, dynamic, members, longitude, latitude)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new DisposableCompletableObserver() {
                                @Override
                                public void onComplete() {
                                    Toasty.success(getBaseContext(), "Neues Meeting erstellt", Toast.LENGTH_SHORT).show();
                                    setResult(RESULT_OK, intent);
                                    finish();
                                    dispose();
                                }

                                @Override
                                public void onError(Throwable e) {
                                    dispose();
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
        builder.setMessage(message).setCancelable(false).setPositiveButton("Ok", (dialogInterface, i) -> {
            if (partySize) {
                minMemberCount = numberPicker.getValue();
                include.tvMinPartySize.setText(String.valueOf(minMemberCount));
            } else {
                minHours = numberPicker.getValue();
                include.tvMinMeetingTime.setText(String.valueOf(minHours));
            }

        }).setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel()).setView(numberPicker).show();
    }


    public void cancelButton(View v) {
        finish();
    }


    public void mergeGroupsAndFriends() {
        for (int i = 0; i < SelectionGroup.size(); i++) {
            compositeDisposable.add(groupsAPIService.getGroupMembers(SelectionGroup.get(i).GroupID)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<List<UserInfo>>() {
                        @Override
                        public void onSuccess(@NonNull List<UserInfo> response) {
                            for (int j = 0; j < response.size(); j++) {
                                boolean tempBool = false;
                                for (int h = 0; h < Selection.size(); h++) {
                                    if (response.get(j).email.equals(Selection.get(h).email)) {
                                        tempBool = true;
                                    }
                                }
                                if (!tempBool) {
                                    Selection.add(response.get(j));
                                }
                            }
                            dispose();
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            dispose();
                        }
                    }));
        }
    }


    private void checkMemberCount() {
        if (Selection.size() < minMemberCount) {
            include.tvAddFriends.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.red));
            enoughPeopleInvited = false;
        } else {
            include.tvAddFriends.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.green));
            enoughPeopleInvited = true;
        }
        include.tvAddFriends.setText(getString(R.string.participants_added, Selection.size()));
    }


    private void createList() {
        compositeDisposable.add(apiService.getSports()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<Sport>>() {
                    @Override
                    public void onSuccess(@NonNull List<Sport> sports) {
                        sportIDs= sports;
                        sportArten = new String[sportIDs.size()];
                        for (int i = 0; i < sportIDs.size(); i++) {
                            sportArten[i] = sportIDs.get(i).sportname;
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e){
                        Log.d("TAG",e.toString());
                        Log.d("TAG","Error");
                    }
                }));
    }


    public boolean enterPressed(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            hideKeyboard();
            include.lvMeetingActivities.setVisibility(View.GONE);
            binding.etChooseActivity.clearFocus();
        }
        return true;
    }

    public void onTextChange(CharSequence text) {
        String newText = text.toString();
        include.lvMeetingActivities.setVisibility(View.VISIBLE);
        extraInfoString = newText;
        List<String> searchresults = new ArrayList<>();

        for (String s : sportArten) {
            if (s.toLowerCase().contains(newText)) {
                searchresults.add(s);
            }
        }
        String[] searchresultsArray = new String[searchresults.size()];
        searchresultsArray = searchresults.toArray(searchresultsArray);
        final String[] sResult = searchresultsArray;
        extraInfoString = newText;
        ArrayAdapter<String> myAdapater = new ArrayAdapter<>(this, R.layout.item_custom_spinner, R.id.search_textview_meeting, sResult);
        myAdapater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        include.lvMeetingActivities.setAdapter(myAdapater);

        include.lvMeetingActivities.setOnItemClickListener((parent, view, position, id) -> {
            binding.etChooseActivity.setText(sResult[position], TextView.BufferType.EDITABLE);
            binding.etChooseActivity.clearFocus();
            hideKeyboard();
            binding.etChooseActivity.setCursorVisible(false);
            include.lvMeetingActivities.setVisibility(View.GONE);
            extraInfoString = sResult[position];
            minMemberCount = sportIDs.get(position).minPartySize;
            sportart_ID = sportIDs.get(position).sportID;
            include.tvMinPartySize.setText(String.valueOf(minMemberCount));
            checkMemberCount();
        });
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(  binding.etChooseActivity.getWindowToken(), 0);
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
    public void onDestroy() {
        try {
            compositeDisposable.dispose();
            super.onDestroy();
        } catch (NullPointerException ignored) {

        }
    }

    ActivityResultLauncher<Intent> startLocationPickerActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                longitude = result.getData().getDoubleExtra(LocationPickerActivity.LONGITUDE, 0);
                latitude = result.getData().getDoubleExtra(LocationPickerActivity.LATITUDE, 0);
            });

    public void chooseLocation(){
        Intent intent = new LocationPickerActivity.Builder()
                .withStreetHidden()
                .withCityHidden()
                .withZipCodeHidden()
                .withSatelliteViewHidden()
                .build(getApplicationContext());
        startLocationPickerActivity.launch(intent);
    }
}