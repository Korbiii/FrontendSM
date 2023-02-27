package com.android.brogrammers.sportsm8.calendarTab.createNewMeetingMVP;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.core.content.ContextCompat;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.android.brogrammers.sportsm8.dataBaseConnection.databaseClasses.Meeting;
import com.android.brogrammers.sportsm8.R;
import com.android.brogrammers.sportsm8.dataBaseConnection.repositories.impl.DatabaseGroupRepository;
import com.android.brogrammers.sportsm8.dataBaseConnection.repositories.impl.DatabaseMeetingsRepository;
import com.android.brogrammers.sportsm8.dataBaseConnection.repositories.impl.DatabaseSportsRepository;
import com.android.brogrammers.sportsm8.socialTab.SelectorContainer;
import com.android.brogrammers.sportsm8.databinding.ActivityCreateNewMeetingTwoBinding;
import com.android.brogrammers.sportsm8.databinding.ContentCreateNewMeeting2Binding;
import com.schibstedspain.leku.LocationPickerActivity;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;


public class CreateNewMeetingActivity
        extends AppCompatActivity implements CreateNewMeetingView {

    //Starting Loop Variables
    private int step = 0;
    private Boolean bBegin = false, bEnd = false, bDate = false;

    private ActivityCreateNewMeetingTwoBinding binding;
    private ContentCreateNewMeeting2Binding include;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    CreateNewMeetingViewPresenter presenter;
    private Intent intent = new Intent();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new CreateNewMeetingViewPresenter(this,
                new DatabaseMeetingsRepository(),
                new DatabaseGroupRepository(),
                new DatabaseSportsRepository(),
                AndroidSchedulers.mainThread());
        binding = ActivityCreateNewMeetingTwoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        include = binding.include;
        intent = getIntent();

        include.addFriendsRL.setOnClickListener(this::addFriends);
        include.moreSettingsRL.setOnClickListener(this::expandSettings);
        include.btnTimeBegin.setOnClickListener(this::timeButtons);
        include.btnTimeEnd.setOnClickListener(this::timeButtons);
        include.btnDateBegin.setOnClickListener(this::dateButton);
        include.btnDateEnd.setOnClickListener(this::dateButton);
        include.rlLocation.setOnClickListener(this::setLocation);

        include.fixedTimeButton.setOnClickListener(v -> setDynamic(v,0));
        include.fluentTimeButton.setOnClickListener(v -> setDynamic(v,1));
        include.dynamicTimeButton.setOnClickListener(v -> setDynamic(v,2));

        include.minMeetingTimeRL.setOnClickListener(presenter::setMinTime);
        include.minPartySizeRL.setOnClickListener(presenter::setMinPartysize);


        binding.cancelButton.setOnClickListener(this::cancel);
        binding.btnClearChooseActivity.setOnClickListener(this::clear);
        binding.saveMeeting.setOnClickListener(presenter::createMeeting);
        binding.etChooseActivity.setOnEditorActionListener(this::enterPressed);

        binding.etChooseActivity.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                sportSearchTriggered(charSequence.toString());
                     }
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {}
        });

        updateDateTimeTextviews(presenter.meeting);
        setMinTimeHours(presenter.meeting.duration);
        setMinimumNumberOfParticipants(presenter.meeting.minParticipants);
    }

    public void sportSearchTriggered(String searchString){
        include.lvMeetingActivities.setVisibility(View.VISIBLE);
        List<String> searchResults = presenter.getSearchResults(searchString);
        ArrayAdapter<String> sportSearchResultAdapter = new ArrayAdapter<>(this, R.layout.item_custom_spinner, R.id.search_textview_meeting, searchResults);
        sportSearchResultAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        include.lvMeetingActivities.setAdapter(sportSearchResultAdapter);

        include.lvMeetingActivities.setOnItemClickListener((parent, view, position, id) -> {
            binding.etChooseActivity.setText(searchResults.get(position), TextView.BufferType.EDITABLE);
            binding.etChooseActivity.clearFocus();
            hideKeyboard();
            binding.etChooseActivity.setCursorVisible(false);
            include.lvMeetingActivities.setVisibility(View.GONE);
            presenter.chooseSport(searchResults.get(position));
        });

    }


    public boolean enterPressed(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            hideKeyboard();
            include.lvMeetingActivities.setVisibility(View.GONE);
            binding.etChooseActivity.clearFocus();
        }
        return true;
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(  binding.etChooseActivity.getWindowToken(), 0);
    }

    private void cancel(View view) {
        binding.etChooseActivity.clearFocus();
        finish();
    }

    public void clear(View view) {
        binding.etChooseActivity.setText("");
    }

    public void setDynamic(View v, int dynamicValue) {
        presenter.meeting.dynamic = dynamicValue;
        findViewById(R.id.dynamic_time_button).setBackgroundColor(dynamicValue == 2 ? ContextCompat.getColor(this, R.color.colorPrimary) : ContextCompat.getColor(this, R.color.grey));
        findViewById(R.id.fluent_time_button).setBackgroundColor(dynamicValue == 1 ? ContextCompat.getColor(this, R.color.colorPrimary) : ContextCompat.getColor(this, R.color.grey));
        findViewById(R.id.fluent_time_button).setBackgroundColor(dynamicValue == 1 ? ContextCompat.getColor(this, R.color.colorPrimary) : ContextCompat.getColor(this, R.color.grey));
        findViewById(R.id.fixed_time_button).setBackgroundColor(dynamicValue == 0 ? ContextCompat.getColor(this, R.color.colorPrimary) : ContextCompat.getColor(this, R.color.grey));
    }



    ActivityResultLauncher<Intent> startLocationPickerActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                presenter.meeting.longitude = (float) result.getData().getDoubleExtra(LocationPickerActivity.LONGITUDE, 0);
                presenter.meeting.latitude = (float) result.getData().getDoubleExtra(LocationPickerActivity.LATITUDE, 0);
            });

    private void setLocation(View view) {
        Intent intent = new LocationPickerActivity.Builder()
                .withStreetHidden()
                .withCityHidden()
                .withZipCodeHidden()
                .withSatelliteViewHidden()
                .build(getApplicationContext());
        startLocationPickerActivity.launch(intent);
    }

    private void timeButtons(View view) {
        int id = view.getId();
        if (id == R.id.btn_time_begin) {
            TimePickerDialog tdp = TimePickerDialog.newInstance((timePickerView, hourOfDay, minute, second) -> {
                presenter.setStartTime(hourOfDay,minute);
                bBegin = true;
                if (step == 0) {
                    step++;
                    timeButtons(include.btnTimeEnd);
                }
            }, presenter.meeting.startTime.getHourOfDay(), presenter.meeting.startTime.getMinuteOfHour(), true);
//                if (checkSwitch.isChecked()) tdp.setTimeInterval(1, 15);
            tdp.show(getFragmentManager(), "TimePickerDialog");
        } else if (id == R.id.btn_time_end) {
            TimePickerDialog tdp2 = TimePickerDialog.newInstance((timePickerView, hourOfDay, minute, second) -> {
                presenter.setEndTime(hourOfDay,minute);
                bEnd = true;
                if (step == 1) {
                    step++;
                    dateButton(include.btnDateBegin);
                }
            }, presenter.meeting.startTime.getHourOfDay() + 2, presenter.meeting.startTime.getMinuteOfHour(), true);
//                if (checkSwitch.isChecked()) tdp2.setTimeInterval(1, 15);
            tdp2.show(getFragmentManager(), "TimePickerDialog");
        }
    }

    public void dateButton(View view) {
        int id = view.getId();
        if (id == R.id.btn_date_begin) {
            new DatePickerDialog(this, (datePickerView, year, month, dayOfMonth) -> {
                presenter.setDate(year,month,dayOfMonth);
                bDate = true;
            }, presenter.meeting.startTime.getYear(), presenter.meeting.startTime.getMonthOfYear() - 1, presenter.meeting.startTime.getDayOfMonth()).show();
        } else if (id == R.id.btn_date_end) {
            new DatePickerDialog(this, (datePickerView, year, month, dayOfMonth) -> {
                presenter.setDate(year,month,dayOfMonth);
            }, presenter.meeting.startTime.getYear(), presenter.meeting.startTime.getMonthOfYear() - 1, presenter.meeting.startTime.getDayOfMonth()).show();
        }

    }

    ActivityResultLauncher<Intent> startAddingFriendsActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    presenter.resultOfAddingFriends(result);
                }else{
                    //TODO Error
                }
            });

    private void addFriends(View view){
        Bundle bundle = new Bundle();
        bundle.putBoolean("SelectionMode", true);
        bundle.putSerializable("Selection", new ArrayList<>(presenter.Selection));
        Intent intent = new Intent(this, SelectorContainer.class);
        intent.putExtras(bundle);
        startAddingFriendsActivity.launch(intent);
    }

    private void expandSettings(View view){
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


    @Override
    public void createNumberPickerDialog(String message, int current, int max,String target) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final NumberPicker numberPicker = new NumberPicker(this);
        numberPicker.setMaxValue(max);
        numberPicker.setMinValue(0);
        numberPicker.setValue(current);
        numberPicker.setWrapSelectorWheel(false);
        builder.setMessage(message).setCancelable(false).setPositiveButton("Ok", (dialogInterface, i) -> {
                    int numberPickerValue = numberPicker.getValue();
                    if (target.equals("minParticipantsCount")){
                        presenter.meeting.minParticipants = numberPickerValue;
                        setMinimumNumberOfParticipants(numberPickerValue);
                    }
                    if (target.equals("minHours")){
                        presenter.meeting.duration = numberPickerValue;
                        setMinTimeHours(numberPickerValue);
                    }

                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel()).setView(numberPicker).show();
    }

    @Override
    public void setMinTimeHours(int minTimeHours) {
        include.tvMinMeetingTime.setText(String.valueOf(minTimeHours));
    }

    @Override
    public void setMinimumNumberOfParticipants(int minimumNumberOfParticipants) {
        include.tvMinPartySize.setText(String.valueOf(minimumNumberOfParticipants));
    }
    @Override
    public  void updateDateTimeTextviews(Meeting meeting) {
        include.btnTimeBegin.setText(meeting.startTime.toString("HH:mm"));
        include.btnTimeEnd.setText(meeting.endTime.toString("HH:mm"));
        include.btnDateBegin.setText(meeting.startTime.toString("EE., dd. MMM. yyyy"));
        include.btnDateEnd.setText(meeting.endTime.toString("EE., dd. MMM. yyyy"));
    }

    @Override
    public void closeActivity() {
        Toasty.success(getBaseContext(), getString(R.string.new_meeting_created), Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void showErrorToast(String message) {
            Toasty.success(this, message, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void displayIfEnoughParticipantsSelected(boolean enough, int size) {
        include.tvAddFriends.setText(getString(R.string.participants_added, size));
        include.tvAddFriends.setTextColor(enough ? ContextCompat.getColor(getBaseContext(), R.color.green):ContextCompat.getColor(getBaseContext(), R.color.red));
    }
}