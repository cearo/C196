package com.cearo.owlganizer.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "assessments", foreignKeys =
    @ForeignKey(
        entity = Course.class,
        parentColumns = "course_id",
        childColumns = "parent_course_id",
        onDelete = CASCADE
    )
)
public class Assessment {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "assessment_id")
    private long assessmentId;
    @NonNull
    private String title;
    @NonNull
    @ColumnInfo(name = "due_date")
    private LocalDate dueDate;
    private String type;
    @ColumnInfo(name = "parent_course_id")
    private long parentCourseId;

    public Assessment(@NonNull String title, @NonNull LocalDate dueDate, String type,
                      long parentCourseId) {
        this.title = title;
        this.dueDate = dueDate;
        this.parentCourseId = parentCourseId;
        this.type = type;
        this.assessmentId = 0;
    }

    public long getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(long assessmentId) {
        this.assessmentId = assessmentId;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    @NonNull
    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(@NonNull LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getParentCourseId() {
        return parentCourseId;
    }

    public void setParentCourseId(long parentCourseId) {
        this.parentCourseId = parentCourseId;
    }
}
