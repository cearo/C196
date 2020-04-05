package com.cearo.owlganizer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cearo.owlganizer.adapter.TermAdapter;
import com.cearo.owlganizer.databinding.ActivityMainBinding;
import com.cearo.owlganizer.models.Term;
import com.cearo.owlganizer.models.viewmodels.TermViewModel;
import com.cearo.owlganizer.utils.listeners.ItemClickListener;

import java.util.List;

public class MainActivity extends AppCompatActivity implements ItemClickListener {

    // Tag for logging
    private final String TAG = "MainActivity";
    // View-binding for activity_main.xml layout
    private ActivityMainBinding activityMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater);

        // **** Begin RecyclerView setup ****

        // Obtaining a ViewModel
        TermViewModel termViewModel = new ViewModelProvider(this).get(TermViewModel.class);
        // Using the ViewModel to retrieve the LiveData<List<Terms>> then extracting the List
        List<Term> allTerms = termViewModel.getAllTerms().getValue();

        // Obtaining a reference to the RecyclerView layout from the activity_main.xml layout binding.
        RecyclerView recyclerView = activityMainBinding.termList;
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

        activityMainBinding.addNewTerm.setOnClickListener(view -> {
            Intent intent = new Intent(this, NewTermActivity.class);
            startActivity(intent);
        });

        // Passing the View using the binding.
        setContentView(activityMainBinding.getRoot());
    }

    @Override
    public void onClick(View view, int position) {
        TermViewModel termViewModel = new ViewModelProvider(this).get(TermViewModel.class);
        List<Term> allTerms = termViewModel.getAllTerms().getValue();
        Term termSelected = null;
//        String termTitle = null;
//        String termStart = null;
//        String termEnd = null;
        if (allTerms != null) {
            termSelected = allTerms.get(position);
        }
        if (termSelected != null) {
            long termId = termSelected.getTermId();
//            termTitle = termSelected.getTitle();
//            termStart = termSelected.getStartDate().toString();
//            termEnd = termSelected.getEndDate().toString();
            Intent intent = new Intent(this, TermDetailActivity.class);
            intent.putExtra("termId", termId);
//            intent.putExtra("termTitle", termTitle);
//            intent.putExtra("termStart", termStart);
//            intent.putExtra("termEnd", termEnd);
            startActivity(intent);
        }
    }
}
