package com.cearo.owlganizer.database.relationships;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.cearo.owlganizer.models.Course;
import com.cearo.owlganizer.models.Term;

import java.util.List;

public class TermsWithCourses {

    @Embedded private Term term;
    @Relation(
            parentColumn = "term_id",
            entityColumn = "parent_term_id",
            entity = Course.class
    )
    private List<Course> courses;

    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courseList) {
        this.courses = courseList;
    }
}
