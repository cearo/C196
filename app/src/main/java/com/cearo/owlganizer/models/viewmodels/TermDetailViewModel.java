package com.cearo.owlganizer.models.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.cearo.owlganizer.database.repositories.TermRepository;
import com.cearo.owlganizer.models.Term;

public class TermDetailViewModel extends AndroidViewModel {
    // The Term this Detail View is for.
    private LiveData<Term> currentTerm;
    // Getting Repository for DB operations.
    private TermRepository termRepository;
    // Constructor
    public TermDetailViewModel(@NonNull Application application) {
        super(application);
        termRepository = new TermRepository(application);
    }
    // Returns the Term object backing the LiveData.
    public Term getCurrentTerm() {
        return this.currentTerm.getValue();
    }
    // Gets a Term from the DB by ID. If id is 0, it'll return the current LiveData object.
    public LiveData<Term> getTermById(long id) {
        if (currentTerm == null && id != 0) {
            currentTerm = termRepository.getTermById(id);
        }

        return this.currentTerm;
    }
    // Updates the Term's record in the DB.
    public void updateTerm(Term term) {
        termRepository.updateTerm(term);
    }
    // Deletes the Term's record from the DB.
    public void deleteTerm(Term term) {
        termRepository.deleteTerm(term);
    }
}
