package com.cearo.owlganizer.models.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.cearo.owlganizer.database.repositories.AssessmentRepository;
import com.cearo.owlganizer.models.Assessment;

public class AssessmentViewModel extends AndroidViewModel {

    private final AssessmentRepository ASSESS_REPO;

    private final LiveData<Assessment> CURRENT_ASSESSMENT;

    private final MutableLiveData<Long> CURRENT_ASSESSMENT_ID;

    public AssessmentViewModel(@NonNull Application application) {
        super(application);
        ASSESS_REPO = new AssessmentRepository(application);

        CURRENT_ASSESSMENT_ID = new MutableLiveData<>();

        CURRENT_ASSESSMENT = Transformations.switchMap(CURRENT_ASSESSMENT_ID,
                ASSESS_REPO::getAssessmentById);
    }

    public LiveData<Assessment> getAssessmentById(long id) {

        if (id != 0) {
            CURRENT_ASSESSMENT_ID.setValue(id);
        }
        return CURRENT_ASSESSMENT;
    }

    public void insertAssessment(@NonNull Assessment assessment) {
        ASSESS_REPO.insertAssessment(assessment);
    }

    public void updateAssessment(@NonNull Assessment assessment) {
        ASSESS_REPO.updateAssessment(assessment);
    }

    public void deleteAssessment(@NonNull Assessment assessment) {
        ASSESS_REPO.deleteAssessment(assessment);
    }
}
