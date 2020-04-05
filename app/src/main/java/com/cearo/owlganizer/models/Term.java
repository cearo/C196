package com.cearo.owlganizer.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

/*
    This class represents the student's college Term. College terms have:
        - Title: A String name chosen by the student.
        - Start Date: A Date obj chosen by the student representing when the term will start.
        - End Date: Same as Start Date except representing the term end.

    This class was created using this guide as reference:
    https://codelabs.developers.google.com/codelabs/android-room-with-a-view/#3
*/

// Room Annotation declaring this object is an Entity serving as a model for the db table
@Entity(tableName = "terms")
public class Term {

    // Room will take care of generating this value
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "term_id")
    private long termId; // Database column

    @NonNull
    private String title; // Database column

    @NonNull
    @ColumnInfo(name = "start_date")
    private LocalDate startDate; // Database column

    @NonNull
    @ColumnInfo(name = "end_date")
    private LocalDate endDate; // Database column

    // Constructor
    public Term(@NonNull String title, @NonNull LocalDate startDate, @NonNull LocalDate endDate) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // **** Getters and Setters ****

    public long getTermId() {
        return termId;
    }

    public void setTermId(long termId) {
        this.termId = termId;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    @NonNull
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(@NonNull LocalDate startDate) {
        this.startDate = startDate;
    }

    @NonNull
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(@NonNull LocalDate endDate) {
        this.endDate = endDate;
    }
}
