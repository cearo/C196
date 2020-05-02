package com.cearo.owlganizer.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import androidx.cardview.widget.CardView;
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
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CourseDetailActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener, AdapterView.OnItemSelectedListener,
        ItemClickListener {

    ActivityCourseDetailBinding binding;

    private CourseDetailViewModel VIEW_MODEL;

    private int inputSelectedId = 0;

    final String DATE_FORMAT = "MMM dd, yyyy";

    final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

    final List<Assessment> COURSE_ASSESSMENTS = new ArrayList<>();

    final List<Note> COURSE_NOTES = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        binding = ActivityCourseDetailBinding.inflate(layoutInflater);

        final Spinner STATUS_OPTIONS_SPIN = binding.courseStatusOptions;

        VIEW_MODEL = new ViewModelProvider(this).get(CourseDetailViewModel.class);

        final Spinner MENTOR_OPTIONS_SPIN = binding.courseMentorOptions;
        final ArrayAdapter<Mentor> MENTOR_OPTIONS_ADATPER = new ArrayAdapter<>(
          this,
                R.layout.support_simple_spinner_dropdown_item,
                Constants.ALL_MENTORS
        );
        MENTOR_OPTIONS_SPIN.setAdapter(MENTOR_OPTIONS_ADATPER);

        EditText[] formFields = { binding.detailCourseStart,
                binding.detailCourseEnd,
                binding.detailCourseTitle };
        // Iterating over each EditText to set Listeners.
        for (EditText field : formFields) {
            // Filtering to just the date fields
            // TODO: Hardcoded again.
            if (field.equals(binding.detailCourseStart) || field.equals(binding.detailCourseEnd)) {
                // Setting a listener on each date field
                field.setOnTouchListener((view, event) -> {
                    // Capturing the ID of the field that triggered the listener.
                    inputSelectedId = field.getId();
                    // This ensures only one DatePicker by binding the creation to one event
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        // Creating the new DatePickerFragment
                        DatePickerFragment datePicker = new DatePickerFragment();
                        // Show the fragment
                        // TODO: Make Tag a final String member variable.
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

                boolean isTextChanged = false;
                boolean isFieldBlank = false;


                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    isFieldBlank = count == s.length() && after == 0;
                }

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

                        isTextChanged = !NEW_TEXT.equals(Course_TITLE)
                                && !NEW_TEXT.equals(Course_START)
                                && !NEW_TEXT.equals(Course_END);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    boolean isSaveAllowed = isTextChanged && !isFieldBlank;
                    // TODO: Figure out how to make field validation reusable. Custom interface?
                    if (isFieldBlank) {
                        final String ERR_FIELD_BLANK = "Field cannot be blank!";
                        field.setError(ERR_FIELD_BLANK);
                    }
                    else field.setError(null);
                    binding.detailCourseSave.setEnabled(isSaveAllowed);
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


        final ArrayAdapter<CharSequence> STATUS_OPTIONS_ADAPTER = ArrayAdapter.createFromResource(
                this,
                R.array.course_status_options,
                R.layout.support_simple_spinner_dropdown_item
        );
        STATUS_OPTIONS_ADAPTER.setDropDownViewResource(
                R.layout.support_simple_spinner_dropdown_item
        );
        STATUS_OPTIONS_SPIN.setAdapter(STATUS_OPTIONS_ADAPTER);
        STATUS_OPTIONS_SPIN.setOnItemSelectedListener(this);


        final Intent FROM_TERM_DETAIL = getIntent();
        final long COURSE_SELECTED_ID = FROM_TERM_DETAIL
                .getLongExtra("courseId", 0);

        final LiveData<Course> LIVE_CURRENT_COURSE = VIEW_MODEL
                .getCurrentCourse(COURSE_SELECTED_ID);

        LIVE_CURRENT_COURSE.observe(this, course -> {
            binding.detailCourseTitle.setText(course.getTitle());
            binding.detailCourseStart.setText(course.getStartDate().format(FORMATTER));
            binding.detailCourseEnd.setText(course.getEndDate().format(FORMATTER));
            STATUS_OPTIONS_SPIN.setSelection(STATUS_OPTIONS_ADAPTER
                    .getPosition(course.getStatus()));
        });

        LiveData<Mentor> LIVE_MENTOR = VIEW_MODEL.getCourseMentor();

        LIVE_MENTOR.observe(this, mentor -> {

            int ArrayCount = MENTOR_OPTIONS_ADATPER.getCount();
            for (int i = 0; i < ArrayCount; i++) {
                Mentor theMentor = MENTOR_OPTIONS_ADATPER.getItem(i);
                if (theMentor!= null && theMentor.getName().equals(mentor.getName())) {
                    MENTOR_OPTIONS_SPIN.setSelection(i, false);
                }
            }
        });

        final LiveData<List<Assessment>> LIVE_COURSE_ASSESS =
                VIEW_MODEL.getCourseAssessments();

        final RecyclerView ASSESS_RECYCLER = binding.courseAssessments;
        final List<Assessment> COURSE_ASSESS_LIST = LIVE_COURSE_ASSESS.getValue();
        final AssessmentAdapter ASSESS_ADAPTER = new AssessmentAdapter(COURSE_ASSESS_LIST);
        ASSESS_RECYCLER.setAdapter(ASSESS_ADAPTER);
        ASSESS_RECYCLER.setLayoutManager(new LinearLayoutManager(this));

        LIVE_COURSE_ASSESS.observe(this, ASSESS_LIST -> {
            COURSE_ASSESSMENTS.clear();
            COURSE_ASSESSMENTS.addAll(ASSESS_LIST);
            ASSESS_ADAPTER.setItemClickListener(this);
            ASSESS_ADAPTER.setCourseAssessments(ASSESS_LIST);
        });

        binding.addNewAssessment.setOnClickListener(view -> {
            final Intent TO_NEW_ASSESS =
                    new Intent(this, NewAssessmentActivity.class);
            TO_NEW_ASSESS.putExtra("courseId", COURSE_SELECTED_ID);
            startActivity(TO_NEW_ASSESS);
        });

        final LiveData<List<Note>> LIVE_COURSE_NOTES = VIEW_MODEL.getCourseNotes();

        final RecyclerView NOTE_RECYCLER = binding.courseNotes;
        final List<Note> COURSE_NOTE_LIST = LIVE_COURSE_NOTES.getValue();
        final NoteAdapter NOTE_ADAPTER = new NoteAdapter(COURSE_NOTE_LIST);
        NOTE_RECYCLER.setAdapter(NOTE_ADAPTER);
        NOTE_RECYCLER.setLayoutManager(new LinearLayoutManager(this));

        LIVE_COURSE_NOTES.observe(this, NOTE_LIST ->{
            COURSE_NOTES.clear();
            COURSE_NOTES.addAll(NOTE_LIST);
            NOTE_ADAPTER.setItemClickListener(this);
            NOTE_ADAPTER.setCourseNotes(NOTE_LIST);
        });

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
        EditText dateField = binding.getRoot().findViewById(inputSelectedId);
        // Setting the EditText field value to the formatted Calendar date.
        dateField.setText(formatter.format(CALENDAR.getTime()));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        final Mentor MENTOR_SELECTED = (Mentor) binding.courseMentorOptions.getSelectedItem();
        binding.courseMentorPhone.setText(MENTOR_SELECTED.getPhoneNumber());
        binding.courseMentorEmail.setText(MENTOR_SELECTED.getEmail());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View view, int position) {
        final int VIEW_ID = view.getId();
        final CardView CARD_SELECTED = (CardView) findViewById(VIEW_ID);
        switch(VIEW_ID) {
            case R.id.assessment_item:
                final Assessment ASSESS_SELECTED = COURSE_ASSESSMENTS.get(position);
                final long ASSESS_SELECTED_ID = ASSESS_SELECTED.getAssessmentId();

                final Intent TO_ASSESS_DETAIL =
                        new Intent(this, AssessmentDetailActivity.class);
                TO_ASSESS_DETAIL.putExtra("assessId", ASSESS_SELECTED_ID);
                startActivity(TO_ASSESS_DETAIL);
                break;
            case R.id.note_item:
                final Note NOTE_SELECTED = COURSE_NOTES.get(position);
                final long NOTE_SELECTED_ID = NOTE_SELECTED.getNoteId();
                Log.i("Course Note", NOTE_SELECTED.getTitle());
                // TODO: Finish Note OnClick

                break;
            default: break;
        }
    }
}
