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
    // Binding to interact with activity_assessment_detail.xml
    private ActivityAssessmentDetailBinding binding;
    // Date Format to be used throughout
    private final String DATE_FORMAT = "MMM dd, yyyy";
    // Formatter for LocalDate objects
    final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
    // Global view model reference
    private AssessmentViewModel VIEW_MODEL;
    // Reference to use for comparison
    private RadioButton RADIO_SELECTED;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final LayoutInflater INFLATER = LayoutInflater.from(this);
        binding = ActivityAssessmentDetailBinding.inflate(INFLATER);
        // AssessmentViewModel
        VIEW_MODEL = new ViewModelProvider(this).get(AssessmentViewModel.class);
        // Intent from CourseDetailActivity
        final Intent FROM_COURSE_DETAIL = getIntent();
        // The ID of the Assessment selected from CourseDetailActivity
        final long ASSESS_SELECTED_ID = FROM_COURSE_DETAIL
                .getLongExtra("assessId", 0);
        // Passing the ID into the View Model and getting the Assessment for this Activity
        final LiveData<Assessment> LIVE_ASSESS_SELECTED =
                VIEW_MODEL.getAssessmentById(ASSESS_SELECTED_ID);
        // Getting references to the layout fields and buttons
        final EditText ASSESS_TITLE_FIELD = binding.detailAssessmentTitle;
        final EditText ASSESS_DUE_FIELD = binding.detailAssessmentDue;
        final RadioGroup ASSESS_TYPE_GROUP = binding.assessTypeGroup;
        final Button SAVE = binding.detailAssessmentSave;
        final Button DELETE = binding.detailAssessmentDelete;
        // Setting the observer to fill in the form fields
        // Also setting the global reference for the Assessment type
        LIVE_ASSESS_SELECTED.observe(this, assessment -> {
            // See the local onCheckedChanged() below
            ASSESS_TYPE_GROUP.setOnCheckedChangeListener(this);
            // Setting field data
            ASSESS_TITLE_FIELD.setText(assessment.getTitle());

            final LocalDate LOCAL_ASSESS_DUE = assessment.getDueDate();
            // Converting LocalDate to formatted String and then setting the text
            ASSESS_DUE_FIELD.setText(LOCAL_ASSESS_DUE.format(FORMATTER));

            final String ASSESS_TYPE_SELECTED = assessment.getType();
            // Setting the RadioButton selected in the Group based on the Assessment.type String
            if (ASSESS_TYPE_SELECTED.equals("Objective")) RADIO_SELECTED = binding.objectiveRadio;
            else RADIO_SELECTED = binding.performanceRadio;

            ASSESS_TYPE_GROUP.check(RADIO_SELECTED.getId());
        });
        // Array of form Buttons. Used to iterate over to set onClickListeners
        Button[] buttons = {SAVE, DELETE};
        // Iterating to set onClickListeners
        for (Button button : buttons) {
            // Setting onclickListener
            button.setOnClickListener( v -> {
                // Getting a reference to the current object for this screen
                final Assessment ASSESS_SELECTED = LIVE_ASSESS_SELECTED.getValue();
                // Detecting if the Save button was pressed and that the Assessment
                // call to the database has finished.
                if (button.equals(SAVE) && ASSESS_SELECTED != null) {
                    // Set to true if fields are null when Save is pressed
                    boolean nullFieldDetected = false;
                    // Iterating over fields to getText() and validate it's not empty
                    EditText[] formFields = {ASSESS_TITLE_FIELD, ASSESS_DUE_FIELD};
                    for (EditText field : formFields) {
                        // If the field is empty
                        if (field.getText().toString().matches("")) {
                            // Saving will be blocked
                            nullFieldDetected = true;
                            // Disabling future Save attempts until field is not blank
                            SAVE.setEnabled(false);
                            // You messed up now
                            final String MESSAGE = "Field cannot be blank!";
                            field.setError(MESSAGE);
                        }
                    }
                    // As long as the null check above passed, saving can proceed.
                    if (!nullFieldDetected) {
                        // Getting form data
                        final String NEW_TITLE = ASSESS_TITLE_FIELD.getText().toString();
                        final String DUE_STR = ASSESS_DUE_FIELD.getText().toString();
                        // Steps to get the value of the Radio selected
                        final int RADIO_SELECTED_ID = ASSESS_TYPE_GROUP.getCheckedRadioButtonId();
                        final RadioButton RADIO_SELECTED = findViewById(RADIO_SELECTED_ID);
                        final String NEW_TYPE = RADIO_SELECTED.getText().toString();
                        // Setting object field values
                        ASSESS_SELECTED.setTitle(NEW_TITLE);
                        ASSESS_SELECTED.setDueDate(LocalDate.parse(DUE_STR, FORMATTER));
                        ASSESS_SELECTED.setType(NEW_TYPE);
                        // Updating the database
                        VIEW_MODEL.updateAssessment(ASSESS_SELECTED);
                        // Going back to the previous screen
                        finish();
                    }
                }
                // Only need to check for null as the only other option is DELETE
                else if (ASSESS_SELECTED != null){
                    // Setting up alert to caution this action
                    final String ALERT_TITLE = "Are you sure you want to delete this Assessment";
                    final String ALERT_MSG = "Deleting this Assessment is a final act; " +
                            "proceed with caution.";
                    final String BTN_DEL_TEXT = "Delete";
                    final String BTN_CNCL_TXT = "Close";
                    final AlertDialog.Builder BUILDER = new AlertDialog.Builder(this);
                    BUILDER.setTitle(ALERT_TITLE);
                    BUILDER.setMessage(ALERT_MSG);
                    // When Delete button is pressed
                    BUILDER.setPositiveButton(BTN_DEL_TEXT, ((dialog, which) -> {
                        // Remove observers to avoid a null SQL return
                        LIVE_ASSESS_SELECTED.removeObservers(this);
                        // Delete the Assessment from the Database
                        VIEW_MODEL.deleteAssessment(ASSESS_SELECTED);
                        // Going back to the previous screen
                        finish();
                    }));
                    // Cancel the dialog if the Close button is pressed
                    BUILDER.setNegativeButton(BTN_CNCL_TXT, ((dialog, which) -> dialog.cancel()));
                    // Show the alert
                    BUILDER.show();
                }
            });
        }
        // Iterating over EditTexts to set onTextChangedListeners for both
        // and onTouchListener for Assessment Due Date field
        EditText[] formFields = {ASSESS_TITLE_FIELD, ASSESS_DUE_FIELD};
        for (EditText field : formFields) {
            // Only need to set onTouchListener for Due Date field
            if (field.equals(ASSESS_DUE_FIELD)) {
                field.setOnTouchListener((view, event) -> {
                    // This is to prevent creating a date picker for every direction that comes in
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        // Setting up and showing the date picker
                        DatePickerFragment datePicker = new DatePickerFragment();
                        datePicker.show(getSupportFragmentManager(),
                                "FRAGMENT_TAG_MAX_ONE_INSTANCE");
                    }
                    return false;
                });
            }
            // Setting TextChangedListener to each EditText to detect if the values changed
            // are different from what is currently stored in the object or if the field is blank
            field.addTextChangedListener(new TextWatcher() {
                // isTextChanged = true and isFieldBlank = false to allow Saving
                boolean isTextChanged = false;
                boolean isFieldBlank = true;

                // Used to detect if the field upon change is blank
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // If the number of characters changed (count) and the length of the
                    // text that was in the field before the change (s.length) are the same
                    // and the number of characters of the value post change is 0 then
                    // isFieldBlank = true
                    isFieldBlank = count == s.length() && after == 0;
                }
                // Used to detect if the changed value (s) is different from the object's
                // current value
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // The changed text value
                    final String NEW_TEXT = s.toString();
                    // The current object
                    final Assessment ASSESSMENT = LIVE_ASSESS_SELECTED.getValue();
                    // Ensuring the database operation has completed
                    if (ASSESSMENT != null) {
                        // Getting the Assessment's current values
                        final String ASSESS_TITLE = ASSESSMENT.getTitle();
                        final String ASSESS_DUE = ASSESSMENT.getDueDate().format(FORMATTER);
                        // If the new text value is different from both object field values
                        // isTextChanged = true
                        isTextChanged = !NEW_TEXT.equals(ASSESS_TITLE)
                                && !NEW_TEXT.equals(ASSESS_DUE);
                    }
                }
                // Determining if Saving is enabled
                @Override
                public void afterTextChanged(Editable s) {
                    // If the values are different and the fields aren't blank
                    // IS_SAVE_ALLOWED = true
                    final boolean IS_SAVE_ALLOWED = isTextChanged && !isFieldBlank;
                    // If the fields are blank, set an error.
                    if (isFieldBlank) {
                        final String ERR_FIELD_BLANK = "Field cannot be blank!";
                        field.setError(ERR_FIELD_BLANK);
                    }
                    // Remove the error if they aren't blank
                    else field.setError(null);
                    SAVE.setEnabled(IS_SAVE_ALLOWED);
                }
            });
        }
        // See onCheckedChanged() below
        ASSESS_TYPE_GROUP.setOnCheckedChangeListener(this);
        // Saving is disabled initially
        SAVE.setEnabled(false);
        // Setting the view
        setContentView(binding.getRoot());
    }
    // Once a Date is chosen by the user via the Date Picker
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        // Setting Calendar and Calendar values
        final Calendar CALENDAR = Calendar.getInstance();
        CALENDAR.set(Calendar.YEAR, year);
        CALENDAR.set(Calendar.MONTH, month);
        CALENDAR.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        // Need a SimpleDateFormat for the Date object from the Date Picker
        final SimpleDateFormat SIMPLE_FORMAT = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        // Setting the EditText value
        binding.detailAssessmentDue.setText(SIMPLE_FORMAT.format(CALENDAR.getTime()));
    }
    // Determines if saving should be enabled based on whether the objects current value
    // (RADIO_SELECTED, set in onCreate), is different from what the user just checked
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        final boolean IS_CHECKED_CHANGED = !(checkedId == RADIO_SELECTED.getId());
        binding.detailAssessmentSave.setEnabled(IS_CHECKED_CHANGED);
    }
}
