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

    @Update
    void updateNote(Note note);

    @Delete
    void deleteNote(Note note);

    @Query("SELECT * FROM notes WHERE parent_course_id = :id")
    LiveData<List<Note>> getAllNotesByCourse(long id);

    @Query("SELECT * FROM notes WHERE note_id = :id")
    LiveData<Note> getNoteById(long id);
}
