package com.cearo.owlganizer.database.relationships;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.cearo.owlganizer.models.Assessment;
import com.cearo.owlganizer.models.Course;

import java.util.List;

public class CoursesWithAssessments {

    @Embedded private Course course;
    @Relation(
            parentColumn = "course_id",
            entityColumn = "parent_course_id",
            entity = Assessment.class
    )
    private List<Assessment> assessments;

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public List<Assessment> getAssessments() {
        return assessments;
    }

    public void setAssessments(List<Assessment> assessments) {
        this.assessments = assessments;
    }
}
