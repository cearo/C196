package com.cearo.owlganizer.database.repositories;


/*
    This class, though not officially part of the Room framework, is a best practice
    implementation suggested by the Android Dev team. This class will serve as the API layer for
    ViewModels to perform database operations.

    This class was created using this guide as reference:
    https://codelabs.developers.google.com/codelabs/android-room-with-a-view/#7
 */

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.cearo.owlganizer.database.AppDatabase;
import com.cearo.owlganizer.database.daos.CourseDao;
import com.cearo.owlganizer.database.relationships.CoursesWithAssessments;
import com.cearo.owlganizer.models.Course;

import java.util.List;

public class CourseRepository {

    // DAO provided by the AppDatabase.
    private CourseDao courseDao;

    // Constructor
    public CourseRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        courseDao = db.courseDao();
    }

    // **** Database operation implementations ****

    public void insertCourse(Course course) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            courseDao.insertCourse(course);
        });
    }

    public void insertCourses(Course[] courses) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            courseDao.insertCourses(courses);
        });
    }

    public void updateCourse(Course course) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            courseDao.updateCourse(course);
        });
    }

    public void deleteCourse(Course course) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            courseDao.deleteCourse(course);
        });
    }

    public LiveData<Course> getCourseById(long id) {
        return courseDao.getCourseById(id);
    }

    public LiveData<List<Course>> getCoursesByTerm(long id) {
        return courseDao.getCoursesByTerm(id);
    }

    public LiveData<CoursesWithAssessments> getCoursesWithAssessments() {
        return courseDao.getCoursesWithAssessments();
    }
}
