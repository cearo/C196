package com.cearo.owlganizer.database.relationships;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.cearo.owlganizer.models.Course;
import com.cearo.owlganizer.models.Term;

import java.util.List;

public class TermsWithCourses {

    @Embedded public Term term;
    @Relation(
            parentColumn = "term_id",
            entityColumn = "parent_term_id",
            entity = Course.class
    )
    public List<Course> courses;
}
