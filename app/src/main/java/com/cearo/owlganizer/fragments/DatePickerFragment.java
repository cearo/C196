package com.cearo.owlganizer.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Objects;

/*
    This class represents a DatePicker which will show up as an overlay over the current Activity
    allowing the user to pick a Date for an input field. Instead of implementing
    DatePickerDialog.onDateSetListener in this class, I am implementing it in the calling Activity.
 */

public class DatePickerFragment extends DialogFragment {

    // Called when the DialogFragment is created.
    @NonNull
    @Override
    public DatePickerDialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Calendar used to hold date values, initialized to the date it was created.
        final Calendar CALENDAR = Calendar.getInstance();
        // Getting the current year value
        int year = CALENDAR.get(Calendar.YEAR);
        // Getting the current month value
        int month = CALENDAR.get(Calendar.MONTH);
        // Getting the current Day of Month value
        int day = CALENDAR.get(Calendar.DAY_OF_MONTH);
        // Creating the DatePickerDialog bound to the calling Activity.
        return new DatePickerDialog(Objects.requireNonNull(getActivity()),
                (DatePickerDialog.OnDateSetListener) getActivity(),
                year, month, day);
    }
}
