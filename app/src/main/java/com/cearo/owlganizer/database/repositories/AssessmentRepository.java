package com.cearo.owlganizer.database.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.cearo.owlganizer.database.AppDatabase;
import com.cearo.owlganizer.database.daos.AssessmentDao;
import com.cearo.owlganizer.models.Assessment;

import java.util.List;

public class AssessmentRepository {

    private final AssessmentDao ASSESSMENT_DAO;

    public AssessmentRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        ASSESSMENT_DAO = db.assessmentDao();
    }

    public void insertAssessment(Assessment assessment) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            ASSESSMENT_DAO.insertAssessment(assessment);
        });
    }

    public void updateAssessment(Assessment assessment) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            ASSESSMENT_DAO.updateAssessment(assessment);
        });
    }

    public void deleteAssessment(Assessment assessment) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            ASSESSMENT_DAO.deleteAssessment(assessment);
        });
    }

    public LiveData<List<Assessment>> getAllAssessmentsByCourse(long id) {
        return ASSESSMENT_DAO.getAssessmentsByCourse(id);
    }

    public LiveData<Assessment> getAssessmentById(long id) {
        return ASSESSMENT_DAO.getAssessmentById(id);
    }
}
