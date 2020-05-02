package com.cearo.owlganizer.database.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.cearo.owlganizer.models.Assessment;

import java.util.List;

@Dao
public interface AssessmentDao {

    @Insert
    void insertAssessment(Assessment assessment);

    @Update
    void updateAssessment(Assessment assessment);

    @Delete
    void deleteAssessment(Assessment assessment);

    @Query("DELETE FROM assessments WHERE parent_course_id = :id")
    void deleteAllAssessmentsByCourse(long id);

    @Query("SELECT * from assessments WHERE parent_course_id = :id")
    LiveData<List<Assessment>> getAssessmentsByCourse(long id);

    @Query("SELECT * FROM assessments WHERE assessment_id = :id")
    LiveData<Assessment> getAssessmentById(long id);

}
