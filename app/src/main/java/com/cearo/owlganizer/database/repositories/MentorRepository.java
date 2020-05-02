package com.cearo.owlganizer.database.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.cearo.owlganizer.database.AppDatabase;
import com.cearo.owlganizer.database.daos.MentorDao;
import com.cearo.owlganizer.models.Mentor;

import java.util.List;

public class MentorRepository {

    private final MentorDao MENTOR_DAO;

    public MentorRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        MENTOR_DAO = db.mentorDao();
    }

    public LiveData<List<Mentor>> getAllMentors() {
        return MENTOR_DAO.getAllMentors();
    }

    public LiveData<Mentor> getMentorById(long id) {
        return MENTOR_DAO.getMentorById(id);
    }
}
