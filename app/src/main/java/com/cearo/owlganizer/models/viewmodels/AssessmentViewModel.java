package com.cearo.owlganizer.models.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.cearo.owlganizer.database.repositories.AssessmentRepository;
import com.cearo.owlganizer.models.Assessment;

public class AssessmentViewModel extends AndroidViewModel {

    private final AssessmentRepository ASSESS_REPO;

    private LiveData<Assessment> ASSESSMENT;

    public AssessmentViewModel(@NonNull Application application) {
        super(application);
        ASSESS_REPO = new AssessmentRepository(application);
    }

    public LiveData<Assessment> getCurrentAssessment() {
        return ASSESSMENT;
    }

    public LiveData<Assessment> getAssessmentById(long id) {
        if (ASSESSMENT == null) {
            ASSESSMENT = ASSESS_REPO.getAssessmentById(id);
        }
        return ASSESSMENT;
    }

    public void insertAssessment(Assessment assessment) {
        ASSESS_REPO.insertAssessment(assessment);
    }

    public void updateAssessment(Assessment assessment) {
        ASSESS_REPO.updateAssessment(assessment);
    }

    public void deleteAssessment(Assessment assessment) {
        ASSESS_REPO.deleteAssessment(assessment);
    }
}
