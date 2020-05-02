package com.cearo.owlganizer.database.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.cearo.owlganizer.models.Note;

import java.util.List;

@Dao
public interface NoteDao {

    @Insert
    void insertNote(Note note);

    @Insert
    void insertAllNotes(Note[] notes);

    @Update
    void updateNote(Note note);

    @Update
    void updateNotes(List<Note> notes);

    @Delete
    void deleteNote(Note note);

    @Query("DELETE FROM notes WHERE parent_course_id = :id")
    void deleteAllNotesByCourse(long id);

    @Query("SELECT * FROM notes WHERE parent_course_id = :id")
    LiveData<List<Note>> getAllNotesByCourse(long id);

    @Query("SELECT * FROM notes WHERE note_id = :id")
    LiveData<Note> getNoteById(long id);
}
