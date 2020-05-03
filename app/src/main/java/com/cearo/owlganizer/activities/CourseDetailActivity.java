package com.cearo.owlganizer.activities;

import android.app.AlertDialog;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cearo.owlganizer.R;
import com.cearo.owlganizer.adapters.recyclerviews.AssessmentAdapter;
import com.cearo.owlganizer.adapters.recyclerviews.NoteAdapter;
import com.cearo.owlganizer.databinding.ActivityCourseDetailBinding;
import com.cearo.owlganizer.fragments.DatePickerFragment;
import com.cearo.owlganizer.models.Assessment;
import com.cearo.owlganizer.models.Course;
import com.cearo.owlganizer.models.Mentor;
import com.cearo.owlganizer.models.Note;
import com.cearo.owlganizer.models.viewmodels.CourseDetailViewModel;
import com.cearo.owlganizer.utils.Constants;
import com.cearo.owlganizer.utils.listeners.ItemClickListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CourseDetailActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener, AdapterView.OnItemSelectedListener,
        ItemClickListener {
    // Binding reference for activity_course_detail.xml
    ActivityCourseDetailBinding binding;
    // Global view model reference
    private CourseDetailViewModel VIEW_MODEL;
    // ID of date EditText field set from within onTouchListener
    private int dateFieldSelectedId = 0;
    // Date format String
    final String DATE_FORMAT = "MMM dd, yyyy";
    // Formatter for LocalDate objects
    final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
    //
    final List<Assessment> COURSE_ASSESSMENTS = new ArrayList<>();

    final List<Note> COURSE_NOTES = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initializing activity_course_detail.xml reference
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        binding = ActivityCourseDetailBinding.inflate(layoutInflater);
        // Reference to course status options Spinner
        final Spinner STATUS_OPTIONS_SPIN = binding.courseStatusOptions;
        // Initializing view model
        VIEW_MODEL = new ViewModelProvider(this).get(CourseDetailViewModel.class);
        // Configuring the mentor options Spinner
        final Spinner MENTOR_OPTIONS_SPIN = binding.courseMentorOptions;
        final ArrayAdapter<Mentor> MENTOR_OPTIONS_ADATPER = new ArrayAdapter<>(
          this,
                R.layout.support_simple_spinner_dropdown_item,
                Constants.ALL_MENTORS
        );
        MENTOR_OPTIONS_SPIN.setAdapter(MENTOR_OPTIONS_ADATPER);
        // All EditText fields
        EditText[] formFields = { binding.detailCourseStart,
                binding.detailCourseEnd,
                binding.detailCourseTitle };
        // Iterating over each EditText to set Listeners.
        for (EditText field : formFields) {
            // Filtering to just the date fields
            if (field.equals(binding.detailCourseStart) || field.equals(binding.detailCourseEnd)) {
                // Setting a listener on each date field
                field.setOnTouchListener((view, event) -> {
                    // Capturing the ID of the field that triggered the listener.
                    dateFieldSelectedId = field.getId();
                    // This ensures only one DatePicker by binding the creation to one event
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
                    // Making the CharSequence a String for comparison
                    final String NEW_TEXT = s.toString();
                    // Using the View Model reference to get the current Course
                    LiveData<Course> currentLiveCourse = VIEW_MODEL.getCurrentCourse();
                    Course currentCourse = currentLiveCourse.getValue();

                    // Null safety
                    if (currentCourse != null) {
                        // Extracting values into variables
                        final String Course_TITLE = currentCourse.getTitle();
                        // Formatting LocalDate strings to match the form
                        final String Course_START = currentCourse.getStartDate().format(FORMATTER);
                        final String Course_END = currentCourse.getEndDate().format(FORMATTER);
                        // If the new text value is different from both object field values
                        // isTextChanged = true
                        isTextChanged = !NEW_TEXT.equals(Course_TITLE)
                                && !NEW_TEXT.equals(Course_START)
                                && !NEW_TEXT.equals(Course_END);
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
                    binding.detailCourseSave.setEnabled(IS_SAVE_ALLOWED);
                }
            });
        }
        // Storing my buttons in an array for the same reason I did with my EditTexts above.
        Button[] buttons = {binding.detailCourseSave, binding.detailCourseDelete};
        // Iterating over each Button to set onClick listeners.
        for (Button button : buttons) {
            // Setting onClick listener for each button.
            button.setOnClickListener(v -> {
                // This represents a decision to go back to ActivityMain
                boolean goBackToMain = false;
                // Setting up variables for readability.
                final LiveData<Course> currentLiveCourse = VIEW_MODEL.getCurrentCourse();
                final Course currentCourse = currentLiveCourse.getValue();
                final Button SAVE_BUTTON = binding.detailCourseSave;
                final Button DELETE_BUTTON = binding.detailCourseDelete;
                /*
                    Determining what action to take based on which button is pressed:

                    SAVE_BUTTON: Updates the Course object's fields and then updates the DB.
                    DELETE_BUTTON: Prompts for user verification then deletes the Course from the DB.
                 */

                if (button.equals(SAVE_BUTTON) && currentCourse != null) {
                    // I don't need to worry about null field validation as I do that already
                    // in the onTextChanged listener.
                    final String NEW_TITLE = binding.detailCourseTitle.getText().toString();
                    final String NEW_START_STR = binding.detailCourseStart.getText().toString();
                    final String NEW_END_STR = binding.detailCourseEnd.getText().toString();
                    // Transforming date Strings into LocalDate objects.
                    final LocalDate NEW_START_L_DATE = LocalDate.parse(NEW_START_STR, FORMATTER);
                    final LocalDate NEW_END_L_DATE = LocalDate.parse(NEW_END_STR, FORMATTER);
                    // Setting object fields to new values.
                    currentCourse.setTitle(NEW_TITLE);
                    currentCourse.setStartDate(NEW_START_L_DATE);
                    currentCourse.setEndDate(NEW_END_L_DATE);
                    // Updating DB
                    VIEW_MODEL.updateCourse(currentCourse);
                    // Returning to MainActivity
                    finish();
                } else if (button.equals(DELETE_BUTTON)) {
                    final String ALERT_TITLE = "Are you sure you want to delete this Course?";
                    final String ALERT_MSG = "Deleting this Course is a final act; " +
                            "proceed with caution.";
                    final String BTN_DEL_TXT = getString(R.string.button_delete);
                    final String BTN_CNCL_TXT = getString(R.string.button_close);
                    final AlertDialog.Builder BUILDER = new AlertDialog.Builder(this);
                    BUILDER.setTitle(ALERT_TITLE);
                    BUILDER.setMessage(ALERT_MSG);
                    BUILDER.setPositiveButton(BTN_DEL_TXT,
                            (dialog, which) -> {
                                // Getting the LiveData object. Id of 0 will return current.
                                LiveData<Course> liveCourse = VIEW_MODEL.getCurrentCourse();
                                LiveData<Mentor> liveMentor = VIEW_MODEL.getCourseMentor();
                                LiveData<List<Assessment>> liveAssessList = VIEW_MODEL
                                        .getCourseAssessments();
                                // Removing observers to avoid them triggering on deletion.
                                liveCourse.removeObservers(this);
                                liveMentor.removeObservers(this);
                                liveAssessList.removeObservers(this);
                                // Deleting the Course from the DB
                                VIEW_MODEL.deleteCourse(currentCourse);
                                //Returning to MainActivity
                                finish();
                            });
                    BUILDER.setNegativeButton(BTN_CNCL_TXT,
                            (dialog, which) -> dialog.cancel());
                    BUILDER.show();
                }
            });
        }
        // Setting up course status options Spinner
        final ArrayAdapter<CharSequence> STATUS_OPTIONS_ADAPTER = ArrayAdapter.createFromResource(
                this,
                R.array.course_status_options,
                R.layout.support_simple_spinner_dropdown_item
        );
        STATUS_OPTIONS_ADAPTER.setDropDownViewResource(
                R.layout.support_simple_spinner_dropdown_item
        );
        STATUS_OPTIONS_SPIN.setAdapter(STATUS_OPTIONS_ADAPTER);
        // See onItemSleected() below
        STATUS_OPTIONS_SPIN.setOnItemSelectedListener(this);
        MENTOR_OPTIONS_SPIN.setOnItemSelectedListener(this);
        // This Activity was started by TermDetailActivity
        final Intent FROM_TERM_DETAIL = getIntent();
        // The ID of the course selected
        final long COURSE_SELECTED_ID = FROM_TERM_DETAIL
                .getLongExtra("courseId", 0);
        // Passing the ID for the course into the view model and
        // retrieving the database information
        final LiveData<Course> LIVE_CURRENT_COURSE = VIEW_MODEL
                .getCurrentCourse(COURSE_SELECTED_ID);
        // Observer waiting for the database information to come back or
        // for object information to change
        LIVE_CURRENT_COURSE.observe(this, course -> {
            // Setting the field values once the course information loads or changes
            binding.detailCourseTitle.setText(course.getTitle());
            binding.detailCourseStart.setText(course.getStartDate().format(FORMATTER));
            binding.detailCourseEnd.setText(course.getEndDate().format(FORMATTER));
            STATUS_OPTIONS_SPIN.setSelection(STATUS_OPTIONS_ADAPTER
                    .getPosition(course.getStatus()));
        });
        // Retrieving database information for the Mentor for this Course
        LiveData<Mentor> LIVE_MENTOR = VIEW_MODEL.getCourseMentor();
        // Observer waiting for the database information to come back or object info
        // to change.
        LIVE_MENTOR.observe(this, mentor -> {
            /*
                * This is a custom comparison implementation to set the Mentor object
                * selected in the Spinner based on the Mentor for this course. When
                * I attempted to set the object using the Adapters set method, because
                * it wasn't the same reference, it wasn't finding the match.
             */
            // How many Mentors are there?
            int ArrayCount = MENTOR_OPTIONS_ADATPER.getCount();
            // For every Mentor
            // I need the index reference, which is why I didn't use a for each
            for (int i = 0; i < ArrayCount; i++) {
                // Getting a Mentor object from the Adapter
                Mentor theMentor = MENTOR_OPTIONS_ADATPER.getItem(i);
                // Comparing the names of the Mentor objects to determine equivalence
                if (theMentor!= null && theMentor.getName().equals(mentor.getName())) {
                    MENTOR_OPTIONS_SPIN.setSelection(i, false);
                }
            }
        });
        // Getting Course Assessment List from the database result
        final LiveData<List<Assessment>> LIVE_COURSE_ASSESS =
                VIEW_MODEL.getCourseAssessments();
        // Setting up the Assessment List RecyclerView
        final RecyclerView ASSESS_RECYCLER = binding.courseAssessments;
        // Getting the database result
        final List<Assessment> COURSE_ASSESS_LIST = LIVE_COURSE_ASSESS.getValue();
        // Passing that result into the RecyclerView Adapter
        final AssessmentAdapter ASSESS_ADAPTER = new AssessmentAdapter(COURSE_ASSESS_LIST);
        // Setting the Adapter to the RecyclerView
        ASSESS_RECYCLER.setAdapter(ASSESS_ADAPTER);
        // A LinearLayoutManager to display the items in a linear fashion
        ASSESS_RECYCLER.setLayoutManager(new LinearLayoutManager(this));
        // Awaiting the database result or a change to the list
        LIVE_COURSE_ASSESS.observe(this, ASSESS_LIST -> {
            // Clearing the local Array and setting the values to the result
            COURSE_ASSESSMENTS.clear();
            COURSE_ASSESSMENTS.addAll(ASSESS_LIST);
            // Now that we have items, enable the ItemClickListener
            ASSESS_ADAPTER.setItemClickListener(this);
            // Set the Adapter's backing List to the result
            // This will also notify the RecyclerView of the change
            ASSESS_ADAPTER.setCourseAssessments(ASSESS_LIST);
        });
        // Click Listener to add a new Assessment to the list for this Course
        binding.addNewAssessment.setOnClickListener(view -> {
            // Going to NewAssessmentActivity with the parent course ID
            final Intent TO_NEW_ASSESS =
                    new Intent(this, NewAssessmentActivity.class);
            TO_NEW_ASSESS.putExtra("courseId", COURSE_SELECTED_ID);
            startActivity(TO_NEW_ASSESS);
        });
        // Getting the Course Notes database result
        final LiveData<List<Note>> LIVE_COURSE_NOTES = VIEW_MODEL.getCourseNotes();
        // Setting up the Course Notes RecyclerView
        final RecyclerView NOTE_RECYCLER = binding.courseNotes;
        // Getting the database result
        final List<Note> COURSE_NOTE_LIST = LIVE_COURSE_NOTES.getValue();
        // Setting the result to the adapter, could be null.
        final NoteAdapter NOTE_ADAPTER = new NoteAdapter(COURSE_NOTE_LIST);
        NOTE_RECYCLER.setAdapter(NOTE_ADAPTER);
        // Using LinearLayoutManager to display items in a linear fashion
        NOTE_RECYCLER.setLayoutManager(new LinearLayoutManager(this));
        // Awaiting database result or object change
        LIVE_COURSE_NOTES.observe(this, NOTE_LIST ->{
            // Clearing the Array and setting the value to the result
            COURSE_NOTES.clear();
            COURSE_NOTES.addAll(NOTE_LIST);
            // Enabling ItemClickListener
            NOTE_ADAPTER.setItemClickListener(this);
            // Notifying RecyclerView of the change
            NOTE_ADAPTER.setCourseNotes(NOTE_LIST);
        });
        // Click Listener to go to NewNoteActivity with the parent course ID
        binding.addNewNote.setOnClickListener(view -> {
            final Intent TO_NEW_NOTE = new Intent(this, NewNoteActivity.class);
            final Course CURRENT_COURSE = LIVE_CURRENT_COURSE.getValue();
            final long PARENT_COURSE_ID = CURRENT_COURSE != null
                    ? CURRENT_COURSE.getCourseId() : 0;
            TO_NEW_NOTE.putExtra("courseId", PARENT_COURSE_ID);
            startActivity(TO_NEW_NOTE);
        });

        setContentView(binding.getRoot());
    }
    // Set the text value of the EditText selected by the user with the
    // Date String from the Date Picker.
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
    // When an item is chosen from the courseStatusOptions or courseMentorOptions Spinners
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Used to determine which Spinner triggered the event
        final int PARENT_ID = parent.getId();
        // If the value is different from the current object field's value
        // isSaveAllowed = true
        boolean isSaveAllowed = false;
        // Determining which Spinner triggered the event
        switch(PARENT_ID) {
            // An item from the courseStatusOptions Spinner was chosen
            case R.id.course_status_options:
                // Getting and converting the backing String array resource
                final List<String> STATUS_OPTIONS = Arrays.asList(getResources()
                        .getStringArray(R.array.course_status_options));
                // Getting the value that was chosen by the user
                final String STATUS_SELECTED = STATUS_OPTIONS.get(position);
                // Getting the current Course for this Activity
                final Course CURRENT_COURSE = VIEW_MODEL.getCurrentCourse().getValue();
                // If the current Course isn't null and the status chosen by the user is
                // different from the current Course's value, isSaveAllowed = true
                isSaveAllowed = CURRENT_COURSE != null
                        && !STATUS_SELECTED.equals(CURRENT_COURSE.getStatus());
                break;
            // An item from the courseMentorOptions Spinner was chosen
            case R.id.course_mentor_options:
                // Getting the Mentor chosen by the user
                final Mentor MENTOR_SELECTED = (Mentor) binding.courseMentorOptions
                        .getSelectedItem();
                // Getting the current Mentor for this Course
                final Mentor CURRENT_MENTOR = VIEW_MODEL.getCourseMentor().getValue();
                // If the current Mentor isn't null and is different from the
                // the one just chosen by the user, isSaveAllowed = true
                isSaveAllowed = CURRENT_MENTOR != null
                        && !MENTOR_SELECTED.getName().equals(CURRENT_MENTOR.getName());
                // Changing read only fields to display Mentor's info
                binding.courseMentorPhone.setText(MENTOR_SELECTED.getPhoneNumber());
                binding.courseMentorEmail.setText(MENTOR_SELECTED.getEmail());
                break;
        }
        // Enabling saving based on the validation
        binding.detailCourseSave.setEnabled(isSaveAllowed);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // I currently have no real use for when nothing is selected but I have to implement it.
    }
    // click function for when a RecyclerView item is chosen
    @Override
    public void onClick(View view, int position) {
        final int VIEW_ID = view.getId();
        // Determining which RecyclerView item was chosen
        switch(VIEW_ID) {
            // An Assessment was chosen
            case R.id.assessment_item:
                // Getting the selected Assessment reference to capture the ID
                final Assessment ASSESS_SELECTED = COURSE_ASSESSMENTS.get(position);
                final long ASSESS_SELECTED_ID = ASSESS_SELECTED.getAssessmentId();
                // Going to AssessmentDetailActivity with the ID of the selected Assessment
                final Intent TO_ASSESS_DETAIL =
                        new Intent(this, AssessmentDetailActivity.class);
                TO_ASSESS_DETAIL.putExtra("assessId", ASSESS_SELECTED_ID);
                startActivity(TO_ASSESS_DETAIL);
                break;
            // A Note was chosen
            case R.id.note_item:
                // Getting the selected Note reference to capture the ID
                final Note NOTE_SELECTED = COURSE_NOTES.get(position);
                final long NOTE_SELECTED_ID = NOTE_SELECTED.getNoteId();
                // Going to NoteDetaiLActivity with the ID of the selected Note
                final Intent TO_NOTE_DETAIL = new Intent(this,
                        NoteDetailActivity.class);
                TO_NOTE_DETAIL.putExtra("noteId", NOTE_SELECTED_ID);
                startActivity(TO_NOTE_DETAIL);
                break;
        }
    }
}
