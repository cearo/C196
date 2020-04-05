package com.cearo.owlganizer.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.cearo.owlganizer.databinding.ActivityNewTermBinding;
import com.cearo.owlganizer.fragments.DatePickerFragment;
import com.cearo.owlganizer.models.Term;
import com.cearo.owlganizer.models.viewmodels.TermViewModel;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

/*
    This class represents the activity of adding a new Term to the database. This class
    implements DatePickerDialog.OnDateSetListener as it utilizes a DatePickerDialog as the method
    of entry for the Term Start Date and Term End Date fields and the listener populates the
    EditText field with the date chosen.
 */

public class NewTermActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener {

    // View-binding for activity_new_term.xml
    private ActivityNewTermBinding binding;
    /*
       This field is used to track which input the user selected to invoke the DatePicker. This
       value is set only from within the onTouchListeners set on the binding.newTermStart and
       binding.newTermEnd. This field is only accessed from within the onDateSet method to find the
       view by the ID stored in this field and set the text to the date chosen.
     */
    private int inputSelectedId = 0;

    // Called when the Activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Layout Inflater bound to the current activity
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        // Inflating the binding for activity_new_term.xml
        binding = ActivityNewTermBinding.inflate(layoutInflater);

        // **** Setting Interaction Listeners ****

        /*
            Since the binding.newTermStart and binding.newTermEnd fields use exactly the same
            listener, I am storing references to them in an array so I can iterate over each one
            and assign the listener. The only reason to do it this way is to keep it DRY.
         */
        // Array of fields
        EditText[] dateFields = {binding.newTermStart, binding.newTermEnd};
        // Iterating over each EditText
        for (EditText field : dateFields) {
            // Setting a listener on each field
            field.setOnTouchListener((view, event) -> {
                // Capturing the ID of the field that triggered the listener.
                inputSelectedId = field.getId();
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

        // Setting the onClickListener for the Save Button action
        binding.newTermSave.setOnClickListener(view -> {
            // **** Gathering form data *****
            String title = binding.newTermTitle.getText().toString();
            String startDate = binding.newTermStart.getText().toString();
            String endDate = binding.newTermEnd.getText().toString();
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

            // Creating the new Term object to be inserted.
            Term newTerm = new Term(title, localStartDate, localEndDate);

            // ViewModel for inserting the new Term
            TermViewModel viewModel = new ViewModelProvider(this).get(TermViewModel.class);
            // Inserting the new Term
            viewModel.insertTerm(newTerm);
            // Transitioning back to the MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        // **** End Setting Interaction Listeners ****
        setContentView(binding.getRoot());
    }

    // Called when the user picks a date from the DatePickerDialog
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
}
