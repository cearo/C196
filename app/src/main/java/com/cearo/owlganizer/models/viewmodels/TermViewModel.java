package com.cearo.owlganizer.models.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.cearo.owlganizer.database.repositories.TermRepository;
import com.cearo.owlganizer.models.Term;

import java.util.List;

/*
    This class is part of the Android Lifecycle components and a best practice when using Room.
    ViewModels survive the destruction of Activities during configuration changes. So when the
    Activity is destroyed and a new one is created, it will reference this still existing instance
    of ViewlModel to resync with the current data. This prevents having to re-make database calls
    and reinitialize variables.

    This class was created using this guide as reference:
    https://codelabs.developers.google.com/codelabs/android-room-with-a-view/#8
 */

public class TermViewModel extends AndroidViewModel {

    private TermRepository termRepository;
    private LiveData<List<Term>> allTerms;

    // Constructor
    public TermViewModel(@NonNull Application application) {
        super(application); // Passes app reference to AndroidViewModel's Constructor.
        this.termRepository = new TermRepository(application);
        this.allTerms = termRepository.getAllTerms();
    }

    // Getter for allTerms
    public LiveData<List<Term>> getAllTerms() {
        return allTerms;
    }

    // **** Database operation APIs for the UI ****

    public void insertTerm(Term term) {
        termRepository.insertTerm(term);
    }

    public void updateTerm(Term term) {
        termRepository.updateTerm(term);
    }

    public void deleteTerm(Term term) {
        termRepository.deleteTerm(term);
    }
}
