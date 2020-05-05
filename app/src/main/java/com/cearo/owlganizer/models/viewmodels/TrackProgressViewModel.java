package com.cearo.owlganizer.models.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.cearo.owlganizer.database.repositories.TrackProgressRepository;
import com.cearo.owlganizer.models.Assessment;
import com.cearo.owlganizer.models.Course;

import java.util.List;

public class TrackProgressViewModel extends AndroidViewModel {

    private final LiveData<List<Course>> ALL_COURSES;

    private final LiveData<List<Assessment>> ALL_ASSESSMENTS;

    public TrackProgressViewModel(@NonNull Application application) {
        super(application);

        final TrackProgressRepository REPO = new TrackProgressRepository(application);

        ALL_COURSES = REPO.getALL_COURSES();
        ALL_ASSESSMENTS = REPO.getALL_ASSESSMENTS();
    }

    public LiveData<List<Course>> getALL_COURSES() {
        return ALL_COURSES;
    }

    public LiveData<List<Assessment>> getALL_ASSESSMENTS() {
        return ALL_ASSESSMENTS;
    }
}
