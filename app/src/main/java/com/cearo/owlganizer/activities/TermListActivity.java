package com.cearo.owlganizer.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.cearo.owlganizer.adapters.recyclerviews.TermAdapter;
import com.cearo.owlganizer.databinding.ActivityMainBinding;
import com.cearo.owlganizer.databinding.ActivityTermListBinding;
import com.cearo.owlganizer.models.Mentor;
import com.cearo.owlganizer.models.Term;
import com.cearo.owlganizer.models.viewmodels.TermViewModel;
import com.cearo.owlganizer.utils.Constants;
import com.cearo.owlganizer.utils.listeners.ItemClickListener;

import java.util.List;

public class TermListActivity extends AppCompatActivity implements ItemClickListener {
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Obtaining a ViewModel
        TermViewModel termViewModel = new ViewModelProvider(this).get(TermViewModel.class);

        final LiveData<List<Mentor>> ALL_MENTORS = termViewModel.getAllMentors();

        ALL_MENTORS.observe(this, Constants.ALL_MENTORS::addAll);
        // LayoutInflater to inflate bindings
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        // Inflating activity_main.xml.
        // View-binding for activity_main.xml layout
        final ActivityTermListBinding BINDING = ActivityTermListBinding.inflate(layoutInflater);

        // **** Begin RecyclerView setup ****

        // Using the ViewModel to retrieve the LiveData<List<Terms>> then extracting the List
        List<Term> allTerms = termViewModel.getAllTerms().getValue();

        // Obtaining a reference to the RecyclerView layout from the activity_main.xml layout binding.
        RecyclerView recyclerView = BINDING.termList;
        // Creating the Adapter for the RecyclerView
        final TermAdapter ADAPTER = new TermAdapter(allTerms);
        // Setting the click listener in the adapter using the onClick method implementation below
        ADAPTER.setItemClickListener(this);
        recyclerView.setAdapter(ADAPTER);
        // Creating a layout manager with this context reference for the RecyclerView.
        // LinearLayoutManager will display list items in a linear format.
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // **** End RecyclerView setup ****

        // Observing the LiveData for all the terms for changes
        // When a change is observed...
        // Pass the RecyclerView Adapter the updated list.
        termViewModel.getAllTerms().observe(this, ADAPTER::setAllTerms);

        BINDING.addNewTerm.setOnClickListener(view -> {
            Intent intent = new Intent(this, NewTermActivity.class);
            startActivity(intent);
        });

        // Passing the View using the binding.
        setContentView(BINDING.getRoot());
    }

    @Override
    public void onClick(View view, int position) {
        // Getting the ViewModel
        TermViewModel termViewModel = new ViewModelProvider(this).get(TermViewModel.class);
        // Getting the LiveData list of terms
        List<Term> allTerms = termViewModel.getAllTerms().getValue();
        // Initializing here so I can set while null checking allTerms.
        Term termSelected = null;
        // Null safety
        if (allTerms != null) {
            // Getting the Term the user selected from the UI.
            termSelected = allTerms.get(position);
        }
        // Null Safety
        if (termSelected != null) {
            // Getting the Term ID to pass to the TermDetailActivity
            long termId = termSelected.getTermId();
            // Passing the ID and changing screens
            Intent intent = new Intent(this, TermDetailActivity.class);
            intent.putExtra("termId", termId);
            startActivity(intent);
        }
    }
}
