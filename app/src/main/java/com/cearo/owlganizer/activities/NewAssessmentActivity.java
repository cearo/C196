package com.cearo.owlganizer.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.cearo.owlganizer.databinding.ActivityNewAssessmentBinding;
import com.cearo.owlganizer.fragments.DatePickerFragment;
import com.cearo.owlganizer.models.Assessment;
import com.cearo.owlganizer.models.viewmodels.AssessmentViewModel;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

public class NewAssessmentActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener {
    // Binding reference for activity_new_assessment.xml
    private ActivityNewAssessmentBinding binding;
    // Global view model reference
    private AssessmentViewModel VIEW_MODEL;
    // Global date format String
    final String DATE_FORMAT = "MMM dd, yyyy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // CourseDetailActivity starts this Activity, passing in the ID of the parent course
        final Intent FROM_COURSE_DETAIL = getIntent();
        final long PARENT_COURSE_ID = FROM_COURSE_DETAIL
                .getLongExtra("courseId", 0);
        // Initializing view model
        VIEW_MODEL = new ViewModelProvider(this).get(AssessmentViewModel.class);
        // Initialing binding
        final LayoutInflater INFLATER = LayoutInflater.from(this);
        binding = ActivityNewAssessmentBinding.inflate(INFLATER);
        // Form EditText fields
        EditText[] formFields = {binding.newAssessmentTitle, binding.newAssessmentDue};
        // Getting Radio Group reference
        final RadioGroup ASSESS_TYPE_GROUP = binding.assessTypeGroup;
        // Getting reference to the Objective RadioButton and then setting it as the default option
        final RadioButton ASSESS_OBJECTIVE_BTN = binding.objectiveRadio;
        ASSESS_TYPE_GROUP.check(ASSESS_OBJECTIVE_BTN.getId());
        // Iterating over EditTexts to set onTextChangedListener for field validation
        for (EditText field : formFields) {

            field.addTextChangedListener(new TextWatcher() {
                // Fields need to not be blank for saving to be enabled
                boolean isFieldBlank = false;
                // Determines if the field is blank
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // If the number of characters changed (count) and the length of the
                    // text that was in the field before the change (s.length) are the same
                    // and the number of characters of the value post change is 0 then
                    // isFieldBlank = true
                    isFieldBlank = count == s.length() && after == 0;
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // I have no real need for onTextChanged in this activity
                }
                // Determining if saving is enabled
                @Override
                public void afterTextChanged(Editable s) {
                    // If the fields are blank, set an error.
                    if (isFieldBlank) {
                        final String ERR_FIELD_BLANK = "Field cannot be blank!";
                        field.setError(ERR_FIELD_BLANK);
                    }
                    // Remove the error if they aren't
                    else field.setError(null);
                }
            });
        }
        // Setting an onTouchListener to create a date picker
        binding.newAssessmentDue.setOnTouchListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                DatePickerFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(),
                        "FRAGMENT_TAG_MAX_ONE_INSTANCE");
            }
            return false;
        });
        // Save button onClickListener
        binding.newAssessmentSave.setOnClickListener(view -> {
            // If fields are empty, saving will be blocked
            boolean nullFieldDetected = false;
            // Iterating over each EditText to see if it's empty
            for (EditText field : formFields) {
                // If the field is empty
                if (field.getText().toString().matches("")) {
                    // Saving is disabled
                    nullFieldDetected = true;
                    binding.newAssessmentSave.setEnabled(false);
                    // Set an error on the field
                    final String MESSAGE = "Field cannot be blank!";
                    field.setError(MESSAGE);
                }
            }
            // If the fields aren't empty, saving can proceed
            if (!nullFieldDetected) {
                // Getting form data
                String title = binding.newAssessmentTitle.getText().toString();
                String dueDateString = binding.newAssessmentDue.getText().toString();

                int radioSelectedId = binding.assessTypeGroup.getCheckedRadioButtonId();
                RadioButton radioSelected = findViewById(radioSelectedId);
                String type = radioSelected.getText().toString();
                // Formatter to parse a LocalDate from the formatted date String
                final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
                LocalDate localDueDate = LocalDate.parse(dueDateString, FORMATTER);
                // Making a new Assessment object
                final Assessment NEW_ASSESS = new Assessment(title, localDueDate, type,
                        PARENT_COURSE_ID);
                // Inserting it into the database
                VIEW_MODEL.insertAssessment(NEW_ASSESS);
                // Going back to the previous screen
                finish();
            }
        });

        setContentView(binding.getRoot());
    }
    // Sets the EditText text value when a user picks a date from the date picker
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        final Calendar CALENDAR = Calendar.getInstance();

        CALENDAR.set(Calendar.YEAR, year);
        CALENDAR.set(Calendar.MONTH, month);
        CALENDAR.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        final SimpleDateFormat FORMATTER = new SimpleDateFormat(DATE_FORMAT, Locale.US);

        binding.newAssessmentDue.setText(FORMATTER.format(CALENDAR.getTime()));
    }
}
