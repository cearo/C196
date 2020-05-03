package com.cearo.owlganizer.database.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.cearo.owlganizer.models.Mentor;

import java.util.List;

@Dao
public interface MentorDao {

    @Insert
    void insertMentors(Mentor[] mentors);

    @Query("SELECT * FROM mentors ORDER BY name")
    LiveData<List<Mentor>> getAllMentors();

    @Query("SELECT * FROM mentors WHERE mentor_id = :id")
    LiveData<Mentor> getMentorById(long id);

    @Query("DELETE FROM mentors")
    void deleteAllMentors();
}
