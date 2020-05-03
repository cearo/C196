package com.cearo.owlganizer.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.cearo.owlganizer.R;
import com.cearo.owlganizer.databinding.ActivityNewCourseBinding;
import com.cearo.owlganizer.fragments.DatePickerFragment;
import com.cearo.owlganizer.models.Course;
import com.cearo.owlganizer.models.Mentor;
import com.cearo.owlganizer.models.viewmodels.NewCourseViewModel;
import com.cearo.owlganizer.utils.Constants;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class NewCourseActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener, AdapterView.OnItemSelectedListener {
    // Binding reference to activity_new_course.xml
    private ActivityNewCourseBinding binding;
    // Global Date format String
    private final String DATE_FORMAT = "MMM dd, yyyy";
    // ID of the date EditText selected. Set in onTouchListener
    private int dateFieldSelectedId = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This Activity was started by TermDetailActivity with the parent term id passed in
        final Intent FROM_TERM_DETAIL = getIntent();
        final long TERM_ID = FROM_TERM_DETAIL.getLongExtra("termId", 0);
        // Initializing the View Model
        final NewCourseViewModel VIEW_MODEL = new ViewModelProvider(this)
                .get(NewCourseViewModel.class);
        // Getting the list of all Mentors from the database
        final LiveData<List<Mentor>> ALL_MENTORS = VIEW_MODEL.getAllMentors();
        // Initializing the binding
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        binding = ActivityNewCourseBinding.inflate(layoutInflater);
        // Saving is disabled by default
        binding.newCourseSave.setEnabled(false);
        // The form's EditText fields
        EditText[] formFields = {binding.newCourseTitle, binding.newCourseStart,
                binding.newCourseEnd};

        // Iterating over each EditText
        for (EditText field : formFields) {
            // Setting a listener on each field
            if (field.equals(binding.newCourseStart) || field.equals(binding.newCourseEnd)) {

                field.setOnTouchListener((view, event) -> {
                    // Capturing the ID of the field that triggered the listener.
                    dateFieldSelectedId = field.getId();
                    // This ensures there is only one DatePicker by binding the creation to one event
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        // Creating the new DatePickerFragment
                        DatePickerFragment datePicker = new DatePickerFragment();
                        // Show the fragment
                        datePicker.show(getSupportFragmentManager(),
                                "FRAGMENT_TAG_MAX_ONE_INSTANCE");
                    }
                    return false;
                });
            }
            /*
                Adding Text Changed Listeners to each input field to analyze if the data the user
                is inputting is valid and different from what is currently stored in the object.
                If the new values are different, saving will be enabled and otherwise disabled.
             */
            field.addTextChangedListener(new TextWatcher() {
                // If fields are blank, saving is blocked
                boolean isFieldBlank = false;

                // Used to detect if the field is blank upon change
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
                    // Currently don't need to do anything when the text actually changes.
                }
                // Determining if saving is enabled
                @Override
                public void afterTextChanged(Editable s) {
                    // If fields are blank, set an error
                    if (isFieldBlank) {
                        final String ERR_FIELD_BLANK = "Field cannot be blank!";
                        field.setError(ERR_FIELD_BLANK);
                    }
                    // Remove the error otherwise
                    else field.setError(null);
                    binding.newCourseSave.setEnabled(!isFieldBlank);
                }
            });
        }

        // Setting the onClickListener for the Save Button action
        binding.newCourseSave.setOnClickListener(view -> {
            // Representing if an input field is blank.
            boolean nullFieldDetected = false;
            // Iterating over all fields
            for (EditText field : formFields) {
                // Detecting blank field
                if (field.getText().toString().matches("")) {
                    // Field is blank
                    nullFieldDetected = true;
                    // Disabling further save attempts until resolved.
                    binding.newCourseSave.setEnabled(false);
                    // Error message
                    final String MESSAGE = "Field cannot be blank!";
                    field.setError(MESSAGE);
                }
            }
            // As long as no null fields are detected, continue with the operation.
            if (!nullFieldDetected) {

                // **** Gathering form data *****
                String title = binding.newCourseTitle.getText().toString();
                String startDate = binding.newCourseStart.getText().toString();
                String endDate = binding.newCourseEnd.getText().toString();
                String status = binding.courseStatusOptions.getSelectedItem().toString();
                Mentor courseMentor = (Mentor) binding.courseMentorOptions.getSelectedItem();
                long mentorId = courseMentor.getMentorId();
                // **** End gathering form data ****

                // **** Transforming date form field text into LocalDate objects ****

                // dateFormat represents the format of the data coming in for a formatter
                final String dateFormat = "MMM dd, yyyy";
                // A formatter using the dateFormat String
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
                // Creating the LocalDate objects
                LocalDate localStartDate = LocalDate.parse(startDate, formatter);
                LocalDate localEndDate = LocalDate.parse(endDate, formatter);

                // **** End Text to LocalDate transformation ****


                final Course NEW_COURSE = new Course(title, localStartDate, localEndDate,
                        status, TERM_ID, mentorId);
                VIEW_MODEL.insertCourse(NEW_COURSE);

                finish();
            }
        });
        // Setting up course status options Spinner
        final Spinner STATUS_OPTIONS_SPIN = binding.courseStatusOptions;
        final ArrayAdapter<CharSequence> STATUS_OPTIONS_ADAPTER = ArrayAdapter.createFromResource(
                this,
                R.array.course_status_options,
                R.layout.support_simple_spinner_dropdown_item
        );
        STATUS_OPTIONS_ADAPTER.setDropDownViewResource(
                R.layout.support_simple_spinner_dropdown_item
        );
        STATUS_OPTIONS_SPIN.setAdapter(STATUS_OPTIONS_ADAPTER);
        // Setting up course mentor options Spinner
        final Spinner MENTOR_OPTIONS_SPIN = binding.courseMentorOptions;
        ArrayAdapter<Mentor> mentorOptionsAdapter = new ArrayAdapter<>(
            this,
            R.layout.support_simple_spinner_dropdown_item,
            Constants.ALL_MENTORS
        );
        MENTOR_OPTIONS_SPIN.setAdapter(mentorOptionsAdapter);
        // See onItemSelected() below
        STATUS_OPTIONS_SPIN.setOnItemSelectedListener(this);
        MENTOR_OPTIONS_SPIN.setOnItemSelectedListener(this);
        setContentView(binding.getRoot());
    }
    // Sets the EditText text value once the user picks a date from the date picker
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        // Calendar to hold the date values
        final Calendar CALENDAR = Calendar.getInstance();
        // Setting Calendar values
        CALENDAR.set(Calendar.YEAR, year);
        CALENDAR.set(Calendar.MONTH, month);
        CALENDAR.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        // Date format String for how the date will be displayed to the user once chosen.
        final String dateFormat = "MMM dd, yyyy";
        // Date formatter using the format String, currently only handling US Locales.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat, Locale.US);
        // Getting the EditText that invoked the DatePickerDialog.
        EditText dateField = binding.getRoot().findViewById(dateFieldSelectedId);
        // Setting the EditText field value to the formatted Calendar date.
        dateField.setText(formatter.format(CALENDAR.getTime()));
    }
    // When a user picks a mentor, update the Mentor info fields
    // When the user picks anything from a Spinner, enable saving
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        final Mentor MENTOR_SELECTED = Constants.ALL_MENTORS.get(position);
        binding.courseMentorPhoneDisplay.setText(MENTOR_SELECTED.getPhoneNumber());
        binding.courseMentorEmailDisplay.setText(MENTOR_SELECTED.getEmail());
        binding.newCourseSave.setEnabled(true);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
