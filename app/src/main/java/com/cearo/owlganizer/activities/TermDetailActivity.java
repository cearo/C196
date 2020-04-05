package com.cearo.owlganizer.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.cearo.owlganizer.databinding.ActivityTermDetailBinding;
import com.cearo.owlganizer.fragments.DatePickerFragment;
import com.cearo.owlganizer.models.Term;
import com.cearo.owlganizer.models.viewmodels.TermDetailViewModel;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

public class TermDetailActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener {

    ActivityTermDetailBinding binding;

    // TODO: Investigate whether this can be local to onCreate.
    private int inputSelectedId = 0;
    private TermDetailViewModel viewModel;
    final String DATE_FORMAT = "MMM dd, yyyy";
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
            Since the binding.newTermStart and binding.newTermEnd fields use exactly the same
            listener, I am storing references to them in an array so I can iterate over each one
            and assign the listener. The only reason to do it this way is to keep it DRY.
         */
        // Array of fields
        // TODO: Look into a better way to do this. Hardcoded = no good.
        EditText[] formFields = { binding.detailTermStart,
                                binding.detailTermEnd,
                                binding.detailTermTitle };
        // Iterating over each EditText
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
            field.addTextChangedListener(new TextWatcher() {
                // TODO: Implement null field checks
                // Representing if the text changed
                boolean isTextChanged = false;


                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    Log.i("beforeTextChanged", s.toString());
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
                        // If the changed text is the same as the current Term's values
                        if (NEW_TEXT.equals(TERM_TITLE)
                        || NEW_TEXT.equals(TERM_START)
                        || NEW_TEXT.equals(TERM_END)) {
                            // Disable the save button
                            binding.detailTermSaveClose.setEnabled(false);
                        }
                        // Otherwise, the values are different so
                        else {
                            // Enable the save button
                            binding.detailTermSaveClose.setEnabled(true);
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    Log.i("afterTextChanged", s.toString());
                    // TODO: Implement field validation
                    // TODO: Figure out how to make field validation reusable. Custom interface?
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
                // Setting up LocalDate formatting
                // TODO: make this variable a private member variable. Also see onDateSet.
                final String DATE_FORMAT = "MMM dd, yyyy";
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
                // Setting the formatted LocalDate strings
                binding.detailTermStart.setText(term.getStartDate().format(formatter));
                binding.detailTermEnd.setText(term.getEndDate().format(formatter));
            });
        }

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
            // TODO: make this variable a private member variable. Also see onDateSet.
            final String dateFormat = "MMM dd, yyyy";
            // Date formatter using the format String, currently only handling US Locales.
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat, Locale.US);
            // Getting the EditText that invoked the DatePickerDialog.
            EditText dateField = binding.getRoot().findViewById(inputSelectedId);
            // Setting the EditText field value to the formatted Calendar date.
            dateField.setText(formatter.format(CALENDAR.getTime()));
    }
}
