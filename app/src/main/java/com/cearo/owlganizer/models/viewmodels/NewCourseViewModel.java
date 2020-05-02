package com.cearo.owlganizer.models.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.cearo.owlganizer.database.repositories.CourseRepository;
import com.cearo.owlganizer.database.repositories.MentorRepository;
import com.cearo.owlganizer.models.Course;
import com.cearo.owlganizer.models.Mentor;

import java.util.List;

public class NewCourseViewModel extends AndroidViewModel {

    private final CourseRepository COURSE_REPO;

    private final MentorRepository MENTOR_REPO;

//    private final TermRepository TERM_REPO;

    private final LiveData<List<Mentor>> ALL_MENTORS;

    public NewCourseViewModel(@NonNull Application application) {
        super(application);
        COURSE_REPO = new CourseRepository(application);
        MENTOR_REPO = new MentorRepository(application);
//        TERM_REPO = new TermRepository(application);

        ALL_MENTORS = MENTOR_REPO.getAllMentors();
    }

    public LiveData<List<Mentor>> getAllMentors() {
        return ALL_MENTORS;
    }

    public void insertCourse(Course course) {
        COURSE_REPO.insertCourse(course);
    }

//    public LiveData<Term> getTermById(long id) {
//        return TERM_REPO.getTermById(id);
//    }
}
