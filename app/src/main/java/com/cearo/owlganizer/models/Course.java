package com.cearo.owlganizer.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

@Entity(tableName = "courses")
public class Course {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "course_id")
    private long courseId;
    @NonNull
    private String title;
    @NonNull
    @ColumnInfo(name = "start_date")
    private LocalDate startDate;
    @NonNull
    @ColumnInfo(name = "end_date")
    private LocalDate endDate;
    @Ignore
    private static final String[] STATUS_OPTIONS =
            {"In Progress", "Plan to Take", "Completed", "Dropped"};
    @NonNull
    private String status;
    @ColumnInfo(name = "parent_term_id")
    private long parentTermId;

    public Course(){};

    public Course(@NonNull String title, @NonNull LocalDate start,
                  @NonNull LocalDate end, @NonNull String status, long parentTermId) {
        this.title = title;
        this.startDate = start;
        this.endDate = end;
        this.status = status;
        this.parentTermId = parentTermId;
        this.courseId = 0;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
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

    public static String[] getSTATUS_OPTIONS() {
        return STATUS_OPTIONS;
    }

    @NonNull
    public String getStatus() {
        return status;
    }

    public void setStatus(@NonNull String status) {
        this.status = status;
    }

    public long getParentTermId() {
        return parentTermId;
    }

    public void setParentTermId(long parentTermId) {
        this.parentTermId = parentTermId;
    }

}
