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

    @Insert
    void insertAssessments(Assessment[] assessments);

    @Update
    void updateAssessment(Assessment assessment);

    @Delete
    void deleteAssessment(Assessment assessment);

    @Query("SELECT * FROM assessments")
    LiveData<List<Assessment>> getAllAssessments();

    @Query("SELECT * from assessments WHERE parent_course_id = :id ORDER BY due_date DESC")
    LiveData<List<Assessment>> getAssessmentsByCourse(long id);

    @Query("SELECT * FROM assessments WHERE assessment_id = :id")
    LiveData<Assessment> getAssessmentById(long id);

}
