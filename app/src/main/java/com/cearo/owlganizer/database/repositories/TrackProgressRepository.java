package com.cearo.owlganizer.database.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.cearo.owlganizer.database.AppDatabase;
import com.cearo.owlganizer.database.daos.AssessmentDao;
import com.cearo.owlganizer.database.daos.CourseDao;
import com.cearo.owlganizer.models.Assessment;
import com.cearo.owlganizer.models.Course;

import java.util.List;

public class TrackProgressRepository {

    private final CourseDao COURSE_DAO;

    private final AssessmentDao ASSESS_DAO;

    private final LiveData<List<Course>> ALL_COURSES;

    private final LiveData<List<Assessment>> ALL_ASSESSMENTS;

    public TrackProgressRepository(Application application) {
        final AppDatabase DB = AppDatabase.getDatabase(application);

        COURSE_DAO = DB.courseDao();
        ASSESS_DAO = DB.assessmentDao();

        ALL_COURSES = COURSE_DAO.getAllCourses();
        ALL_ASSESSMENTS = ASSESS_DAO.getAllAssessments();
    }

    public LiveData<List<Course>> getALL_COURSES() {
        return ALL_COURSES;
    }

    public LiveData<List<Assessment>> getALL_ASSESSMENTS() {
        return ALL_ASSESSMENTS;
    }
}
