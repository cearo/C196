package com.cearo.owlganizer.activities;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.cearo.owlganizer.databinding.ActivityAssessmentDetailBinding;
import com.cearo.owlganizer.fragments.DatePickerFragment;
import com.cearo.owlganizer.models.Assessment;
import com.cearo.owlganizer.models.viewmodels.AssessmentViewModel;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;


public class AssessmentDetailActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener, RadioGroup.OnCheckedChangeListener {

    private ActivityAssessmentDetailBinding binding;

    private final String DATE_FORMAT = "MMM dd, yyyy";
    final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

    private AssessmentViewModel VIEW_MODEL;

    RadioButton RADIO_SELECTED;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final LayoutInflater INFLATER = LayoutInflater.from(this);
        binding = ActivityAssessmentDetailBinding.inflate(INFLATER);

        VIEW_MODEL = new ViewModelProvider(this).get(AssessmentViewModel.class);

        final Intent FROM_COURSE_DETAIL = getIntent();
        final long ASSESS_SELECTED_ID = FROM_COURSE_DETAIL
                .getLongExtra("assessId", 0);

        final LiveData<Assessment> LIVE_ASSESS_SELECTED =
                VIEW_MODEL.getAssessmentById(ASSESS_SELECTED_ID);

        final EditText ASSESS_TITLE_FIELD = binding.detailAssessmentTitle;
        final EditText ASSESS_DUE_FIELD = binding.detailAssessmentDue;
        final RadioGroup ASSESS_TYPE_GROUP = binding.assessTypeGroup;
        final Button SAVE = binding.detailAssessmentSave;
        final Button DELETE = binding.detailAssessmentDelete;

        LIVE_ASSESS_SELECTED.observe(this, assessment -> {
            ASSESS_TYPE_GROUP.setOnCheckedChangeListener(this);
            ASSESS_TITLE_FIELD.setText(assessment.getTitle());

            final LocalDate LOCAL_ASSESS_DUE = assessment.getDueDate();

            ASSESS_DUE_FIELD.setText(LOCAL_ASSESS_DUE.format(FORMATTER));

            final String ASSESS_TYPE_SELECTED = assessment.getType();

            if (ASSESS_TYPE_SELECTED.equals("Objective")) RADIO_SELECTED = binding.objectiveRadio;
            else RADIO_SELECTED = binding.performanceRadio;

            ASSESS_TYPE_GROUP.check(RADIO_SELECTED.getId());
        });

        Button[] buttons = {SAVE, DELETE};

        for (Button button : buttons) {

            button.setOnClickListener( v -> {
                final Assessment ASSESS_SELECTED = LIVE_ASSESS_SELECTED.getValue();

                if (button.equals(SAVE) && ASSESS_SELECTED != null) {

                    boolean nullFieldDetected = false;
                    EditText[] formFields = {ASSESS_TITLE_FIELD, ASSESS_DUE_FIELD};
                    for (EditText field : formFields) {
                        if (field.getText().toString().matches("")) {
                            nullFieldDetected = true;

                            SAVE.setEnabled(false);

                            final String MESSAGE = "Field cannot be blank!";
                            field.setError(MESSAGE);
                        }
                    }

                    if (!nullFieldDetected) {
                        final String NEW_TITLE = ASSESS_TITLE_FIELD.getText().toString();
                        final String DUE_STR = ASSESS_DUE_FIELD.getText().toString();

                        final int RADIO_SELECTED_ID = ASSESS_TYPE_GROUP.getCheckedRadioButtonId();
                        final RadioButton RADIO_SELECTED = findViewById(RADIO_SELECTED_ID);
                        final String NEW_TYPE = RADIO_SELECTED.getText().toString();

                        ASSESS_SELECTED.setTitle(NEW_TITLE);
                        ASSESS_SELECTED.setDueDate(LocalDate.parse(DUE_STR, FORMATTER));
                        ASSESS_SELECTED.setType(NEW_TYPE);
                        VIEW_MODEL.updateAssessment(ASSESS_SELECTED);
                        finish();
                    }
                }
                else {
                    final String ALERT_TITLE = "Are you sure you want to delete this Assessment";
                    final String ALERT_MSG = "Deleting this Assessment is a final act; " +
                            "proceed with caution.";
                    final String BTN_DEL_TEXT = "Delete";
                    final String BTN_CNCL_TXT = "Close";
                    final AlertDialog.Builder BUILDER = new AlertDialog.Builder(this);
                    BUILDER.setTitle(ALERT_TITLE);
                    BUILDER.setMessage(ALERT_MSG);
                    BUILDER.setPositiveButton(BTN_DEL_TEXT, ((dialog, which) -> {
                        LIVE_ASSESS_SELECTED.removeObservers(this);
                        VIEW_MODEL.deleteAssessment(ASSESS_SELECTED);
                        finish();
                    }));
                    BUILDER.setNegativeButton(BTN_CNCL_TXT, ((dialog, which) -> dialog.cancel()));
                    BUILDER.show();
                }
            });
        }
        EditText[] formFields = {ASSESS_TITLE_FIELD, ASSESS_DUE_FIELD};

        for (EditText field : formFields) {

            if (field.equals(ASSESS_DUE_FIELD)) {
                field.setOnTouchListener((view, event) -> {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        DatePickerFragment datePicker = new DatePickerFragment();

                        datePicker.show(getSupportFragmentManager(),
                                "FRAGMENT_TAG_MAX_ONE_INSTANCE");
                    }
                    return false;
                });
            }

            field.addTextChangedListener(new TextWatcher() {

                boolean isTextChanged = false;
                boolean isFieldBlank = false;

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    isFieldBlank = count == s.length() && after == 0;
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    final String NEW_TEXT = s.toString();

                    final Assessment ASSESSMENT = LIVE_ASSESS_SELECTED.getValue();

                    if (ASSESSMENT != null) {

                        final String ASSESS_TITLE = ASSESSMENT.getTitle();
                        final String ASSESS_DUE = ASSESSMENT.getDueDate().format(FORMATTER);

                        isTextChanged = !NEW_TEXT.equals(ASSESS_TITLE)
                                && !NEW_TEXT.equals(ASSESS_DUE);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    final boolean IS_SAVE_ALLOWED = isTextChanged && !isFieldBlank;

                    if (isFieldBlank) {
                        final String ERR_FIELD_BLANK = "Field cannot be blank!";
                        field.setError(ERR_FIELD_BLANK);
                    }
                    else field.setError(null);
                    SAVE.setEnabled(IS_SAVE_ALLOWED);
                }
            });
        }
        SAVE.setEnabled(false);
        setContentView(binding.getRoot());
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        final Calendar CALENDAR = Calendar.getInstance();

        CALENDAR.set(Calendar.YEAR, year);
        CALENDAR.set(Calendar.MONTH, month);
        CALENDAR.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        final SimpleDateFormat SIMPLE_FORMAT = new SimpleDateFormat(DATE_FORMAT, Locale.US);

        binding.detailAssessmentDue.setText(SIMPLE_FORMAT.format(CALENDAR.getTime()));
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        final boolean IS_CHECKED_CHANGED = !(checkedId == RADIO_SELECTED.getId());
        Log.i("onCheckChanged", "Here");
        if (!binding.detailAssessmentSave.isEnabled()) binding.detailAssessmentSave
                .setEnabled(IS_CHECKED_CHANGED);
    }
}
