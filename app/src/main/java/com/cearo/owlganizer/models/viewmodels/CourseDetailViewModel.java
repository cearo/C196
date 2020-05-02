package com.cearo.owlganizer.models.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.cearo.owlganizer.database.AppDatabase;
import com.cearo.owlganizer.database.repositories.AssessmentRepository;
import com.cearo.owlganizer.database.repositories.CourseRepository;
import com.cearo.owlganizer.database.repositories.MentorRepository;
import com.cearo.owlganizer.database.repositories.NoteRepository;
import com.cearo.owlganizer.models.Assessment;
import com.cearo.owlganizer.models.Course;
import com.cearo.owlganizer.models.Mentor;
import com.cearo.owlganizer.models.Note;

import java.util.List;

public class CourseDetailViewModel extends AndroidViewModel {

    private final MutableLiveData<Long> CURRENT_COURSE_ID;

    private final LiveData<Course> CURRENT_COURSE;

    private final LiveData<Mentor> COURSE_MENTOR;

    private final LiveData<List<Assessment>> COURSE_ASSESSMENTS;

    private final LiveData<List<Note>> COURSE_NOTES;

    private final CourseRepository COURSE_REPO;

    private final MentorRepository MENTOR_REPO;

    private final AssessmentRepository ASSESS_REPO;

    private final NoteRepository NOTE_REPO;

    public CourseDetailViewModel(@NonNull Application application) {
        super(application);
        this.COURSE_REPO = new CourseRepository(application);
        this.MENTOR_REPO = new MentorRepository(application);
        this.ASSESS_REPO = new AssessmentRepository(application);
        this.NOTE_REPO = new NoteRepository(application);
        this.CURRENT_COURSE_ID = new MutableLiveData<>();
        CURRENT_COURSE = Transformations.switchMap(CURRENT_COURSE_ID,
                COURSE_REPO::getCourseById);
        this.COURSE_MENTOR = Transformations.switchMap(CURRENT_COURSE,
                course -> MENTOR_REPO.getMentorById(course.getCourseMentorId()));
        this.COURSE_ASSESSMENTS = Transformations.switchMap(CURRENT_COURSE,
                course -> ASSESS_REPO.getAllAssessmentsByCourse(course.getCourseId()));
        this.COURSE_NOTES = Transformations.switchMap(CURRENT_COURSE,
                course -> NOTE_REPO.getAllNotesByCourse(course.getCourseId()));
    }

    public LiveData<Course> getCurrentCourse() {
        return this.CURRENT_COURSE;
    }

    public LiveData<Course> getCurrentCourse(long id) {
        CURRENT_COURSE_ID.setValue(id);
        return this.CURRENT_COURSE;
    }

    public void updateCourse(Course course) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            COURSE_REPO.updateCourse(course);
        });
    }

    public void deleteCourse(Course course) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            COURSE_REPO.deleteCourse(course);
        });
    }

    public LiveData<Mentor> getCourseMentor() {
        return this.COURSE_MENTOR;
    }

    public LiveData<List<Assessment>> getCourseAssessments() {
        return COURSE_ASSESSMENTS;
    }

    public LiveData<List<Note>> getCourseNotes() { return this.COURSE_NOTES; }

    public LiveData<Note> getNoteById(long id) {
        return NOTE_REPO.getNoteById(id);
    }

}
