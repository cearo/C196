package com.cearo.owlganizer.models.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.cearo.owlganizer.database.repositories.TermRepository;
import com.cearo.owlganizer.models.Term;

public class TermDetailViewModel extends AndroidViewModel {

    private LiveData<Term> currentTerm;
    private TermRepository termRepository;

    public TermDetailViewModel(@NonNull Application application) {
        super(application);
        termRepository = new TermRepository(application);
    }

    public Term getCurrentTerm() {
        return this.currentTerm.getValue();
    }
    public LiveData<Term> getTermById(long id) {
        if (currentTerm == null ) {
            currentTerm = termRepository.getTermById(id);
        }

        return this.currentTerm;
    }

    public void updateTerm(Term term) {
        termRepository.updateTerm(term);
    }
}
