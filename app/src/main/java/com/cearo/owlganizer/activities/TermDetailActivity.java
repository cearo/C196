package com.cearo.owlganizer.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.cearo.owlganizer.R;
import com.cearo.owlganizer.databinding.ActivityTermDetailBinding;
import com.cearo.owlganizer.fragments.DatePickerFragment;
import com.cearo.owlganizer.models.Term;
import com.cearo.owlganizer.models.viewmodels.TermDetailViewModel;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

/*
    This class represents the screen detailing a Term the user selects from the list of all Terms
    in MainActivity. From here the user can view details for the Term, change the Term details and
    update the DB object, or delete the DB object.
 */

public class TermDetailActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener {
    // Binding referencing activity_term_detail.xml
    ActivityTermDetailBinding binding;

    // The ID of the form field selected. Used to fill the form field with data.
    private int inputSelectedId = 0;
    // A reference to the View Model so I don't have to make a new ViewModelProvider every time.
    private TermDetailViewModel viewModel;
    // Date Format used by the form.
    final String DATE_FORMAT = "MMM dd, yyyy";
    // Formatter applying the format.
    final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        binding = ActivityTermDetailBinding.inflate(layoutInflater);
        // Getting an instance of the ViewModel for this screen
        viewModel = new ViewModelProvider(this).get(TermDetailViewModel.class);
        /*
            TODO: Rewrite this comment
            I initialize the term to be extracted from the database as null in order to first
            validate that the termSelectedId is not the default value of 0: which would indicate
            that the object obtained from the term item click was null for some reason.

            TODO: Think about how to implement UI notification of an issue.
            If termSelectedId or currentTerm are null (or 0 which is effectively null) then the UI
            should be notified in some way. Maybe I create an intent to kick back to MainActivity?

         */
        LiveData<Term> currentTerm = null;
        /*
            These variables will hold the String data for the Term we are detailing.
            They will be set after the Term is fetched from the database. I am naming them using
            a final convention to imply their intention to not be changed. I cannot make them final
            as I'm not overriding the AppCompatActivity constructor. They are to represent the
            Term as it was pulled from the database. This will allow me to see if changes made to
            the Term are ever reverted back to their initial state so I can disable the Save button
            allowing me to avoid unnecessary db calls.
            TODO: Look into possibly overriding the AppCompatActivity constructor.
         */
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
                    Term currentTerm = viewModel.getCurrentTerm();
                    // Null safety
                    if (currentTerm != null) {
                        // Extracting values into variables
                        final String TERM_TITLE = currentTerm.getTitle();
                        // Formatting LocalDate strings to match the form
                        final String TERM_START = currentTerm.getStartDate().format(FORMATTER);
                        final String TERM_END = currentTerm.getEndDate().format(FORMATTER);

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
                    binding.detailTermSaveClose.setEnabled(isSaveAllowed);
                }
            });
        }
        // Storing my buttons in an array for the same reason I did with my EditTexts above.
        Button[] buttons = {binding.detailTermSaveClose, binding.detailTermDelete};
        // Iterating over each Button to set onClick listeners.
        for (Button button : buttons) {
            // Setting onClick listener for each button.
            button.setOnClickListener(v -> {
                // This represents a decision to go back to ActivityMain
                boolean goBackToMain = false;
                // Setting up variables for readability.
                final Term TERM = viewModel.getCurrentTerm();
                final Button SAVE_BUTTON = binding.detailTermSaveClose;
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
                    viewModel.updateTerm(TERM);
                    // Returning to MainActivity
                    returnToMainActivity();
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
                                // Getting the LiveData object. Id of 0 will return current.
                                LiveData<Term> liveTerm = viewModel.getTermById(0);
                                // Removing observers to avoid them triggering on deletion.
                                liveTerm.removeObservers(this);
                                // Deleting the Term from the DB
                                viewModel.deleteTerm(TERM);
                                //Returning to MainActivity
                                returnToMainActivity();
                            });
                    BUILDER.setNegativeButton(BTN_CNCL_TXT,
                            (dialog, which) -> dialog.cancel());
                    BUILDER.show();
                }
            });
        }
        // Getting the Intent that started this Activity
        Intent intent = getIntent();
        // Extracting the Extra data attached to the Intent
        // TODO: Update termId string to be a final String member variable.
        long termSelectedId = intent.getLongExtra("termId", 0);

        if (termSelectedId != 0) {
            currentTerm = viewModel.getTermById(termSelectedId);
        }
        // Null safety on currentTerm, considering it is initialized to null.
        if (currentTerm != null) {
            // Setting the LiveData observer to populate the form fields based on the term found.
            currentTerm.observe(this, term -> {
                // Setting form field data
                binding.detailTermTitle.setText(term.getTitle());
                // Setting the formatted LocalDate strings
                binding.detailTermStart.setText(term.getStartDate().format(FORMATTER));
                binding.detailTermEnd.setText(term.getEndDate().format(FORMATTER));
            });
        }

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

    // Used to return to the MainActivity
    private void returnToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


}
