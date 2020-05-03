package com.cearo.owlganizer.database.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.cearo.owlganizer.models.Course;

import java.util.List;

/*
    Room will use this class to build the SQL queries for the SQLite database.

    This class was created using this guide as reference:
    https://codelabs.developers.google.com/codelabs/android-room-with-a-view/#4
 */

// Room Annotation declaring this class is a DAO (Data Access Object)
@Dao
public interface CourseDao {

    // Method for inserting a new Course into the database.
    @Insert
    void insertCourse(Course course);

    // Method for inserting multiple new Courses into the database.
    @Insert
    void insertCourses(Course[] courses);

    // Method for updating an existing Course object in the database.
    @Update
    void updateCourse(Course course);

    // Method for deleting an existing Course object from the database.
    @Delete
    void deleteCourse(Course course);

    // Method for pulling a specific course from the database by ID.
    @Query("SELECT * FROM courses WHERE course_id = :id")
    LiveData<Course> getCourseById(long id);

    @Query("SELECT * FROM courses WHERE parent_term_id = :id ORDER BY start_date, end_date")
    LiveData<List<Course>> getCoursesByTerm(long id);
}
