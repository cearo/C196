package com.cearo.owlganizer.database.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.cearo.owlganizer.database.relationships.TermsWithCourses;
import com.cearo.owlganizer.models.Term;

import java.util.List;

/*
    Room will use this class to build the SQL queries for the SQLite database.

    This class was created using this guide as reference:
    https://codelabs.developers.google.com/codelabs/android-room-with-a-view/#4
 */

// Room Annotation declaring this class is a DAO (Data Access Object)
@Dao
public interface TermDao {

    // Method for inserting a new Term object into the database
    @Insert
    void insertTerm(Term term);

    // Method for inserting multiple new Term objects into the database
    @Insert
    void insertAllTerms(Term[] terms);

    // Method for updating an existing Term object in the database.
    @Update
    void updateTerm(Term term);

    // Method for deleting an existing Term object from the database
    @Delete
    void deleteTerm(Term term);

    // Method for retrieving all Terms from the database ordered by start and end date ASC
    @Query("SELECT * FROM terms ORDER BY start_date, end_date")
    LiveData<List<Term>> getAllTerms();

    // Method for retrieving a single Term by id
    @Query("SELECT * FROM terms WHERE term_id = :id")
    LiveData<Term> getTermById(long id);

    // Method for retrieving all Courses that are children of a specific Term.
    @Transaction
    @Query("SELECT * FROM terms")
    LiveData<TermsWithCourses> getTermsWithCourses();

    // Method for deleting all Terms from the database
    @Query("DELETE FROM terms")
    void deleteAllTerms();
}
