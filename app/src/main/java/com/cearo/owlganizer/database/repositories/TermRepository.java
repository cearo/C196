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
import com.cearo.owlganizer.database.daos.TermDao;
import com.cearo.owlganizer.models.Term;

import java.util.List;

public class TermRepository {

    // DAO provided by AppDatabase
    private TermDao termDao;
    // All Terms from the Database
    private LiveData<List<Term>> allTerms;

    // Constructor
    public TermRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        this.termDao = db.termDao();
        // Allowed w/o threading because it returns LiveData
        this.allTerms = termDao.getAllTerms();
    }

    // Getter for allTerms
    public LiveData<List<Term>> getAllTerms() {
        return allTerms;
    }

    // **** Database operation implementations ****

    public LiveData<Term> getTermById(long id) {
        // Allowed w/o threading because it returns LiveData
        return termDao.getTermById(id);
    }

    public void insertTerm(Term term) {
        AppDatabase.databaseWriteExecutor.execute(() -> termDao.insertTerm(term));
    }

    public void updateTerm(Term term) {
        AppDatabase.databaseWriteExecutor.execute(() -> termDao.updateTerm(term));
    }

    public void deleteTerm(Term term) {
        AppDatabase.databaseWriteExecutor.execute(() -> termDao.deleteTerm(term));
    }
}
