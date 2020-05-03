package com.cearo.owlganizer.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;

import com.cearo.owlganizer.R;
import com.cearo.owlganizer.databinding.ActivityNoteDetailBinding;
import com.cearo.owlganizer.models.Note;
import com.cearo.owlganizer.models.viewmodels.NoteViewModel;

public class NoteDetailActivity extends AppCompatActivity {
    // Binding reference for activity_note_detail.xml
    ActivityNoteDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initializing the binding
        LayoutInflater INFLATER = LayoutInflater.from(this);
        binding = ActivityNoteDetailBinding.inflate(INFLATER);
        // Getting form field references
        final EditText NOTE_TITLE = binding.noteDetailTitle;
        final EditText NOTE_MESSAGE_BODY = binding.noteDetailBody;
        final Button SAVE = binding.noteDetailSave;
        final Button DELETE = binding.noteDetailDelete;
        // This Activity was started by CourseDetailActivity with the ID of the Note chosen
        final Intent FROM_COURSE_DETAIL = getIntent();
        final long NOTE_SELECTED_ID = FROM_COURSE_DETAIL
                .getLongExtra("noteId", 0);
        // Initializing the view model
        final NoteViewModel VIEW_MODEL = new ViewModelProvider(this)
                .get(NoteViewModel.class);
        // Passing the ID of the chosen Note into the view model and obtaining database result
        final LiveData<Note> LIVE_CURRENT_NOTE = VIEW_MODEL.getNoteById(NOTE_SELECTED_ID);
        // Iterating over EditText's to add TextChangedListener
        final EditText[] FORM_FIELDS = {NOTE_TITLE, NOTE_MESSAGE_BODY};
        for (EditText FIELD : FORM_FIELDS) {

            FIELD.addTextChangedListener(new TextWatcher() {
                // isTextChanged = true and isFieldBlank = false to allow Saving
                boolean isFieldBlank = true;
                boolean isTextChanged = false;
                // Used to determine if a field is blank upon change
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // If the number of characters changed (count) and the length of the
                    // text that was in the field before the change (s.length) are the same
                    // and the number of characters of the value post change is 0 then
                    // isFieldBlank = true
                    isFieldBlank = count == s.length() && after == 0;
                }
                // Used to determine if the changed value is different from the object's current
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Changed value
                    final String NEW_TEXT = s.toString();
                    // Getting current object
                    final Note NOTE = LIVE_CURRENT_NOTE.getValue();
                    // If database result hasn't come back, this could be null
                    if (NOTE != null) {
                        // Getting current object values
                        final String NOTE_TITLE = NOTE.getTitle();
                        final String NOTE_BODY = NOTE.getMessageBody();
                        // If the changed value is different from the object's current value
                        // isTextChanged = true
                        isTextChanged = !NEW_TEXT.equals(NOTE_TITLE)
                                && !NEW_TEXT.equals(NOTE_BODY);
                    }
                }
                // Determining if saving is enabled
                @Override
                public void afterTextChanged(Editable s) {
                    // If the values are different and the fields aren't blank
                    // IS_SAVE_ALLOWED = true
                    final boolean IS_SAVE_ALLOWED = isTextChanged && !isFieldBlank;
                    // If a field is blank, set an error
                    if (isFieldBlank) {
                        final String ERR_FIELD_BLANK = "Field cannot be blank!";
                        FIELD.setError(ERR_FIELD_BLANK);
                    }
                    // Otherwise remove the error
                    else FIELD.setError(null);
                    SAVE.setEnabled(IS_SAVE_ALLOWED);
                }
            });
        }
        // Awaiting database result or object change
        LIVE_CURRENT_NOTE.observe(this, NOTE -> {
            // Setting form field values
            NOTE_TITLE.setText(NOTE.getTitle());
            NOTE_MESSAGE_BODY.setText(NOTE.getMessageBody());

        });
        // Form buttons to iterate over to set onClickListeners
        final Button[] BUTTONS = {SAVE, DELETE};
        for (Button BUTTON : BUTTONS) {
            BUTTON.setOnClickListener(view -> {
                // Getting the current Note
                final Note NOTE_SELECTED = LIVE_CURRENT_NOTE.getValue();
                // Determining which Button I'm working with
                final int BUTTON_ID = BUTTON.getId();
                switch(BUTTON_ID) {
                    // Save button
                    case R.id.note_detail_save:
                        // If fields are blank, saving is blocked
                        boolean nullFieldDetected = false;
                        // Validating form fields aren't blank
                        for(EditText FIELD : FORM_FIELDS) {
                            // If the field is blank
                            if (FIELD.getText().toString().matches("")) {
                                // Saving is blocked
                                nullFieldDetected = true;
                                SAVE.setEnabled(false);
                                // Set an error
                                final String MESSAGE = "Field cannot be blank!";
                                FIELD.setError(MESSAGE);
                            }
                        }
                        // If the fields aren't null and the database result has come back
                        if (!nullFieldDetected && NOTE_SELECTED != null) {
                            // Get the form data
                            final String NEW_TITLE = NOTE_TITLE.getText().toString();
                            final String NEW_BODY = NOTE_MESSAGE_BODY.getText().toString();
                            // Set the new values
                            NOTE_SELECTED.setTitle(NEW_TITLE);
                            NOTE_SELECTED.setMessageBody(NEW_BODY);
                            // Update the database
                            VIEW_MODEL.updateNote(NOTE_SELECTED);
                            // Go back to the previous screen
                            finish();
                        }
                        break;
                    // Delete button was chosen
                    case R.id.note_detail_delete:
                        // Setting up an alert to caution this action
                        final String ALERT_TITLE = "Are you sure you want to delete this Note?";
                        final String ALERT_MSG = "Deleting this Note is a final act; " +
                                "proceed with caution.";
                        final String BTN_DEL_TEXT = "Delete";
                        final String BTN_CNCL_TEXT = "Close";
                        final AlertDialog.Builder BUILDER = new AlertDialog.Builder(this);
                        BUILDER.setTitle(ALERT_TITLE);
                        BUILDER.setMessage(ALERT_MSG);
                        // Delete button is prssed, delete the Note from the database
                        BUILDER.setPositiveButton(BTN_DEL_TEXT, ((dialog, which) -> {
                            // If the database result has come back
                            if (NOTE_SELECTED != null) {
                                // Remove observers to prevent null object reference
                                LIVE_CURRENT_NOTE.removeObservers(this);
                                // Delete from the database
                                VIEW_MODEL.deleteNote(NOTE_SELECTED);
                                // Go back to previous screen
                                finish();
                            }
                        }));
                        BUILDER.setNegativeButton(BTN_CNCL_TEXT, ((dialog, which) -> dialog
                                .cancel()));
                        BUILDER.show();
                        break;
                }
            });
        }
        setContentView(binding.getRoot());
    }
}

