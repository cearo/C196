package com.cearo.owlganizer.models.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.cearo.owlganizer.database.repositories.CourseRepository;
import com.cearo.owlganizer.database.repositories.TermRepository;
import com.cearo.owlganizer.models.Course;
import com.cearo.owlganizer.models.Term;

import java.util.List;

public class TermDetailViewModel extends AndroidViewModel {

    private final MutableLiveData<Long> CURRENT_TERM_ID;
    // The Term this Detail View is for.
    private final LiveData<Term> CURRENT_TERM;

    private final LiveData<List<Course>> TERM_COURSES;
    // Getting Repository for Term DB operations.
    private final TermRepository TERM_REPO;


    // Constructor
    public TermDetailViewModel(@NonNull Application application) {
        super(application);
        this.TERM_REPO = new TermRepository(application);
        // Getting Repository for Course DB operations.
        CourseRepository COURSE_REPO = new CourseRepository(application);
        CURRENT_TERM_ID = new MutableLiveData<>();
        CURRENT_TERM = Transformations.switchMap(CURRENT_TERM_ID,
                TERM_REPO::getTermById);
        TERM_COURSES = Transformations.switchMap(CURRENT_TERM_ID,
                COURSE_REPO::getCoursesByTerm);
    }
    // Returns the Term object backing the LiveData.
    public LiveData<Term> getCurrentTerm() {
        return this.CURRENT_TERM;
    }
    // Gets a Term from the DB by ID. If id is 0, it'll return the current LiveData object.
    public LiveData<Term> getTermById(long id) {
        if (id != 0) {
            CURRENT_TERM_ID.setValue(id);
        }
        return this.CURRENT_TERM;
    }

    public LiveData<List<Course>> getTermCourses() {
        return this.TERM_COURSES;
    }
    // Updates the Term's record in the DB.
    public void updateTerm(@NonNull Term term) {
        TERM_REPO.updateTerm(term);
    }
    // Deletes the Term's record from the DB.
    public void deleteTerm(@NonNull Term term) {
        TERM_REPO.deleteTerm(term);
    }

}
