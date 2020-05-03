package com.cearo.owlganizer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.cearo.owlganizer.databinding.ActivityNewNoteBinding;
import com.cearo.owlganizer.models.Note;
import com.cearo.owlganizer.models.viewmodels.NoteViewModel;

public class NewNoteActivity extends AppCompatActivity {
    // Binding reference for activity_new_note.xml
    ActivityNewNoteBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initializing the binding
        final LayoutInflater INFLATER = LayoutInflater.from(this);
        binding = ActivityNewNoteBinding.inflate(INFLATER);
        // This Activity is starting by CourseActivityDetail with the parent course id
        final Intent FROM_COURSE_DETAIL = getIntent();
        final long PARENT_COURSE_ID =
                FROM_COURSE_DETAIL.getLongExtra("courseId", 0);
        // Initializing the view model
        final NoteViewModel VIEW_MODEL = new ViewModelProvider(this)
                .get(NoteViewModel.class);
        // Obtaining field references
        final EditText NOTE_TITLE_FIELD = binding.newNoteTitle;
        final EditText NOTE_BODY_FIELD = binding.newNoteBody;
        final Button SAVE = binding.newNoteSave;
        // EditText fields
        final EditText[] FORM_FIELDS = {NOTE_TITLE_FIELD, NOTE_BODY_FIELD};
        // Iterating to add onTextChangedListener
        for (EditText FIELD : FORM_FIELDS) {

            FIELD.addTextChangedListener(new TextWatcher() {
                // If fields are blank, saving is blocked
                boolean isFieldBlank;
                // Used to determine if fields are blank
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
                    // Don't need onTextChanged here
                }
                // Used to determine if saving is allowed
                @Override
                public void afterTextChanged(Editable s) {
                    // if fields are blank, set an error
                    if (isFieldBlank) {
                        final String ERR_FIELD_BLANK = "Field cannot be blank!";
                        FIELD.setError(ERR_FIELD_BLANK);
                    }
                    // Remove the error otherwise
                    else FIELD.setError(null);
                    SAVE.setEnabled(!isFieldBlank);
                }

            });
        }
        // OnClickListener for Save button
        SAVE.setOnClickListener(view -> {
            // if fields are blank, saving will be blocked
            boolean nullFieldDetected = false;
            // Validating each EditText isn't blank
            for (EditText FIELD : FORM_FIELDS) {
                // If the field is blank
                if (FIELD.getText().toString().matches("")) {
                    // Saving is blocked
                    nullFieldDetected = true;
                    SAVE.setEnabled(false);
                    // Set an error on the field
                    final String MESSAGE = "Field cannot be blank!";
                    FIELD.setError(MESSAGE);
                }
            }
            // If none of the fields are blank
            if (!nullFieldDetected) {
                // Getting the form data
                final String TITLE = NOTE_TITLE_FIELD.getText().toString();
                final String MESSAGE_BODY = NOTE_BODY_FIELD.getText().toString();
                // Making a new object
                final Note NEW_NOTE = new Note(TITLE, MESSAGE_BODY, PARENT_COURSE_ID);
                // Inserting the new Note
                VIEW_MODEL.insertNote(NEW_NOTE);
                // Going back to the previous screen
                finish();
            }
        });
        // Saving is disabled upon initialization
        binding.newNoteSave.setEnabled(false);
        setContentView(binding.getRoot());
    }
}
