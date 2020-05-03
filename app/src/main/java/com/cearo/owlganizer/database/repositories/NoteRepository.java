package com.cearo.owlganizer.database.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.cearo.owlganizer.database.AppDatabase;
import com.cearo.owlganizer.database.daos.NoteDao;
import com.cearo.owlganizer.models.Note;

import java.util.List;

public class NoteRepository {

    private final NoteDao NOTE_DAO;

    public NoteRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        NOTE_DAO = db.noteDao();
    }

    public void insertNote(Note note) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            NOTE_DAO.insertNote(note);
        });
    }

    public void updateNote(Note note) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            NOTE_DAO.updateNote(note);
        });
    }

    public void deleteNote(Note note) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            NOTE_DAO.deleteNote(note);
        });
    }

    public LiveData<List<Note>> getAllNotesByCourse(long id) {
        return NOTE_DAO.getAllNotesByCourse(id);
    }

    public LiveData<Note> getNoteById(long id) {
        return NOTE_DAO.getNoteById(id);
    }
}
