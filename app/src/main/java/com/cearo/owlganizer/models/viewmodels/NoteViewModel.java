package com.cearo.owlganizer.models.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.cearo.owlganizer.database.repositories.NoteRepository;
import com.cearo.owlganizer.models.Note;

public class NoteViewModel extends AndroidViewModel {

    private final NoteRepository NOTE_REPO;

    private final MutableLiveData<Long> CURRENT_NOTE_ID;

    private final LiveData<Note> CURRENT_NOTE;

    public NoteViewModel(@NonNull Application application) {
        super(application);
        NOTE_REPO = new NoteRepository(application);

        CURRENT_NOTE_ID = new MutableLiveData<>();

        CURRENT_NOTE = Transformations.switchMap(CURRENT_NOTE_ID,
                NOTE_REPO::getNoteById);
    }

    public LiveData<Note> getNoteById(long id) {

        if (id != 0) {
            CURRENT_NOTE_ID.setValue(id);
        }

        return CURRENT_NOTE;
    }

    public void insertNote(@NonNull Note note) {
        NOTE_REPO.insertNote(note);
    }

    public void updateNote(@NonNull Note note) {
        NOTE_REPO.updateNote(note);
    }

    public void deleteNote(@NonNull Note note) {
        NOTE_REPO.deleteNote(note);
    }
}
