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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cearo.owlganizer.R;
import com.cearo.owlganizer.adapters.recyclerviews.CourseAdapter;
import com.cearo.owlganizer.databinding.ActivityTermDetailBinding;
import com.cearo.owlganizer.fragments.DatePickerFragment;
import com.cearo.owlganizer.models.Course;
import com.cearo.owlganizer.models.Term;
import com.cearo.owlganizer.models.viewmodels.TermDetailViewModel;
import com.cearo.owlganizer.utils.listeners.ItemClickListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/*
    This class represents the screen detailing a Term the user selects from the list of all Terms
    in MainActivity. From here the user can view details for the Term, change the Term details and
    update the DB object, or delete the DB object.
 */

public class TermDetailActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener, ItemClickListener {
    // Binding referencing activity_term_detail.xml
    ActivityTermDetailBinding binding;

    // The ID of the form field selected. Used to fill the form field with data.
    private int inputSelectedId = 0;

    // Date Format used by the form.
    final String DATE_FORMAT = "MMM dd, yyyy";
    // Formatter applying the format.
    final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

    final List<Course> TERM_COURSES = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        binding = ActivityTermDetailBinding.inflate(layoutInflater);
        // Getting an instance of the ViewModel for this screen
        final TermDetailViewModel VIEW_MODEL =
                new ViewModelProvider(this).get(TermDetailViewModel.class);

        // Getting the Intent that started this Activity
        final Intent FROM_MAIN_ACT = getIntent();
        // Extracting the Extra data attached to the Intent
        // TODO: Update termId string to be a final String member variable.
        final long TERM_SELECTED_ID = FROM_MAIN_ACT.getLongExtra("termId", 0);

//        liveTermsWithCourses = viewModel.getTermsWithCourses();

        final LiveData<Term> CURRENT_TERM = VIEW_MODEL.getTermById(TERM_SELECTED_ID);

        // **** Setting Interaction Listeners ****

        /*
            I am storing references to my form input fields in an array so I can iterate over each
            one and assign the listener. The only reason to do it this way is to keep it DRY.
         */
        // Array of fields
        // TODO: Look into a better way to do this. Hardcoded = no good.
        EditText[] formFields = { binding.detailTermStart,
                                binding.detailTermEnd,
                                binding.detailTermTitle };
        // Iterating over each EditText to set Listeners.
        for (EditText field : formFields) {
            // Filtering to just the date fields
            // TODO: Hardcoded again.
            if (field.equals(binding.detailTermStart) || field.equals(binding.detailTermEnd)) {
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
                    // Using the View Model reference to get the current Term
                    final Term TERM = CURRENT_TERM.getValue();
                    // Null safety
                    if (TERM != null) {
                        // Extracting values into variables
                        final String TERM_TITLE = TERM.getTitle();
                        // Formatting LocalDate strings to match the form
                        final String TERM_START = TERM.getStartDate().format(FORMATTER);
                        final String TERM_END = TERM.getEndDate().format(FORMATTER);

                        isTextChanged = !NEW_TEXT.equals(TERM_TITLE)
                                && !NEW_TEXT.equals(TERM_START)
                                && !NEW_TEXT.equals(TERM_END);
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
                    binding.detailTermSave.setEnabled(isSaveAllowed);
                }
            });
        }
        // Storing my buttons in an array for the same reason I did with my EditTexts above.
        Button[] buttons = {binding.detailTermSave, binding.detailTermDelete};
        // Iterating over each Button to set onClick listeners.
        for (Button button : buttons) {
            // Setting onClick listener for each button.
            button.setOnClickListener(v -> {
                // This represents a decision to go back to ActivityMain
                boolean goBackToMain = false;
                // Setting up variables for readability.
                final Term TERM = CURRENT_TERM.getValue();
                final Button SAVE_BUTTON = binding.detailTermSave;
                final Button DELETE_BUTTON = binding.detailTermDelete;
                /*
                    Determining what action to take based on which button is pressed:

                    SAVE_BUTTON: Updates the Term object's fields and then updates the DB.
                    DELETE_BUTTON: Prompts for user verification then deletes the Term from the DB.
                 */

                if (button.equals(SAVE_BUTTON)) {
                    // I don't need to worry about null field validation as I do that already
                    // in the onTextChanged listener.
                    final String NEW_TITLE = binding.detailTermTitle.getText().toString();
                    final String NEW_START_STR = binding.detailTermStart.getText().toString();
                    final String NEW_END_STR = binding.detailTermEnd.getText().toString();
                    // Transforming date Strings into LocalDate objects.
                    final LocalDate NEW_START_L_DATE = LocalDate.parse(NEW_START_STR, FORMATTER);
                    final LocalDate NEW_END_L_DATE = LocalDate.parse(NEW_END_STR, FORMATTER);
                    // Setting object fields to new values.
                    TERM.setTitle(NEW_TITLE);
                    TERM.setStartDate(NEW_START_L_DATE);
                    TERM.setEndDate(NEW_END_L_DATE);
                    // Updating DB
                    VIEW_MODEL.updateTerm(TERM);
                    // Returning to MainActivity
                    finish();
                } else if (button.equals(DELETE_BUTTON)) {
                    final String ALERT_TITLE = "Are you sure you want to delete this Term?";
                    final String ALERT_MSG = "Deleting this Term is a final act; act with caution.";
                    final String BTN_DEL_TXT = getString(R.string.button_delete);
                    final String BTN_CNCL_TXT = getString(R.string.button_close);
                    final AlertDialog.Builder BUILDER = new AlertDialog.Builder(this);
                    BUILDER.setTitle(ALERT_TITLE);
                    BUILDER.setMessage(ALERT_MSG);
                    BUILDER.setPositiveButton(BTN_DEL_TXT,
                            (dialog, which) -> {
                                // Getting the LiveData object
                                final LiveData<Term> LIVE_TERM = VIEW_MODEL.getCurrentTerm();
                                // Removing observers to avoid them triggering on deletion.
                                LIVE_TERM.removeObservers(this);
                                // Deleting the Term from the DB
                                VIEW_MODEL.deleteTerm(TERM);
                                //Returning to MainActivity
                                finish();
                            });
                    BUILDER.setNegativeButton(BTN_CNCL_TXT,
                            (dialog, which) -> dialog.cancel());
                    BUILDER.show();
                }
            });
        }

        // Null safety
        if (CURRENT_TERM != null) {
            // Setting the LiveData observer to populate the form fields based on the term found.
            CURRENT_TERM.observe(this, term -> {
                // Setting form field data
                binding.detailTermTitle.setText(term.getTitle());
                // Setting the formatted LocalDate strings
                binding.detailTermStart.setText(term.getStartDate().format(FORMATTER));
                binding.detailTermEnd.setText(term.getEndDate().format(FORMATTER));
            });
        }

        binding.termAddNewCourse.setOnClickListener(view -> {
            final Intent TO_NEW_COURSE = new Intent(this, NewCourseActivity.class);
            TO_NEW_COURSE.putExtra("termId", TERM_SELECTED_ID);
            startActivity(TO_NEW_COURSE);
        });

        final RecyclerView RECYCLER_VIEW = binding.termCourses;

        final CourseAdapter ADAPTER = new CourseAdapter(TERM_COURSES);


        RECYCLER_VIEW.setAdapter(ADAPTER);
        RECYCLER_VIEW.setLayoutManager(new LinearLayoutManager(this));

        final LiveData<List<Course>> LIVE_COURSE_LIST = VIEW_MODEL.getTermCourses();
        LIVE_COURSE_LIST.observe(this, COURSE_LIST -> {
            ADAPTER.setItemClickListener(this);
            TERM_COURSES.clear();
            TERM_COURSES.addAll(COURSE_LIST);
            ADAPTER.setTermCourses(TERM_COURSES);
        });
        // Displaying activity_term_detail.xml
        setContentView(binding.getRoot());
    }


    // Called once a date is chosen from the DatePickerDialog.
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            // Calendar to hold the date values
            final Calendar CALENDAR = Calendar.getInstance();
            // Setting Calendar values
            CALENDAR.set(Calendar.YEAR, year);
            CALENDAR.set(Calendar.MONTH, month);
            CALENDAR.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            // Date formatter using the format String, currently only handling US Locales.
            SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT, Locale.US);
            // Getting the EditText that invoked the DatePickerDialog.
            EditText dateField = binding.getRoot().findViewById(inputSelectedId);
            // Setting the EditText field value to the formatted Calendar date.
            dateField.setText(formatter.format(CALENDAR.getTime()));
    }

    @Override
    public void onClick(View view, int position) {
//            TermsWithCourses termCourses = termsWithCourses.getValue();
//            List<Course> termCourseList = termCourses != null ? termCourses.getCourses() : null;
            Course courseSelected = TERM_COURSES != null ? TERM_COURSES.get(position) : null;

            if (courseSelected != null) {

                long courseId = courseSelected.getCourseId();

                final Intent TO_COURSE_DETAIL =
                        new Intent(this, CourseDetailActivity.class);

                TO_COURSE_DETAIL.putExtra("courseId", courseId);
                startActivity(TO_COURSE_DETAIL);

            }
    }
    // Used to return to the MainActivity

    private void returnToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
