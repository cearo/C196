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

    private ActivityNewAssessmentBinding binding;

    private AssessmentViewModel VIEW_MODEL;

    final String DATE_FORMAT = "MMM dd, yyyy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent FROM_COURSE_DETAIL = getIntent();
        final long PARENT_COURSE_ID = FROM_COURSE_DETAIL
                .getLongExtra("courseId", 0);

        VIEW_MODEL = new ViewModelProvider(this).get(AssessmentViewModel.class);

        final LayoutInflater INFLATER = LayoutInflater.from(this);
        binding = ActivityNewAssessmentBinding.inflate(INFLATER);

        EditText[] formFields = {binding.newAssessmentTitle, binding.newAssessmentDue};

        for (EditText field : formFields) {

            field.addTextChangedListener(new TextWatcher() {

                boolean isFieldBlank = false;

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    isFieldBlank = count == s.length() && after == 0;
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (isFieldBlank) {
                        final String ERR_FIELD_BLANK = "Field cannot be blank!";
                        field.setError(ERR_FIELD_BLANK);
                    }
                    else field.setError(null);
                }
            });
        }

        binding.newAssessmentDue.setOnTouchListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                DatePickerFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(),
                        "FRAGMENT_TAG_MAX_ONE_INSTANCE");
            }
            return false;
        });

        binding.newAssessmentSave.setOnClickListener(view -> {
            boolean nullFieldDetected = false;

            for (EditText field : formFields) {
                if (field.getText().toString().matches("")) {
                    nullFieldDetected = true;
                    binding.newAssessmentSave.setEnabled(false);
                    final String MESSAGE = "Field cannot be blank!";
                    field.setError(MESSAGE);
                }
            }

            if (!nullFieldDetected) {
                String title = binding.newAssessmentTitle.getText().toString();
                String dueDateString = binding.newAssessmentDue.getText().toString();

                int radioSelectedId = binding.assessTypeGroup.getCheckedRadioButtonId();
                RadioButton radioSelected = (RadioButton) findViewById(radioSelectedId);
                String type = radioSelected.getText().toString();

                final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
                LocalDate localDueDate = LocalDate.parse(dueDateString, FORMATTER);

                final Assessment NEW_ASSESS = new Assessment(title, localDueDate, type,
                        PARENT_COURSE_ID);
                VIEW_MODEL.insertAssessment(NEW_ASSESS);
                finish();
            }
        });

        setContentView(binding.getRoot());
    }

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
