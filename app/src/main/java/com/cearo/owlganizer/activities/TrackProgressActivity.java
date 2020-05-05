package com.cearo.owlganizer.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.cearo.owlganizer.databinding.ActivityTrackProgressBinding;
import com.cearo.owlganizer.models.Assessment;
import com.cearo.owlganizer.models.Course;
import com.cearo.owlganizer.models.viewmodels.TrackProgressViewModel;

import java.util.List;

public class TrackProgressActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final LayoutInflater INFLATER = LayoutInflater.from(this);
        final ActivityTrackProgressBinding BINDING = ActivityTrackProgressBinding
                .inflate(INFLATER);

        final TextView COURSE_PLANNED_COUNT = BINDING.prrogressPlannedCount;
        final TextView COURSE_COMPLETED_COUNT = BINDING.progressCompletedCount;
        final TextView COURSE_DROPPED_COUNT = BINDING.progressDroppedCount;
        final TextView ASSESS_OBJ_COUNT = BINDING.progressObjCount;
        final TextView ASSESS_PERFORM_COUNT = BINDING.progressPerformCount;

        final TrackProgressViewModel VIEW_MODEL = new ViewModelProvider(this)
                .get(TrackProgressViewModel.class);

        final LiveData<List<Course>> LIVE_ALL_COURSES = VIEW_MODEL.getALL_COURSES();
        final LiveData<List<Assessment>> LIVE_ALL_ASSESS = VIEW_MODEL.getALL_ASSESSMENTS();

        LIVE_ALL_COURSES.observe(this, COURSE_LIST -> {

            for (Course COURSE : COURSE_LIST) {
                final String COURSE_STATUS = COURSE.getStatus();

                switch(COURSE_STATUS) {

                    case "Planned":
                    case "In Progress":
                        int PLANNED_COUNTER = Integer.parseInt(COURSE_PLANNED_COUNT
                                .getText().toString());
                        PLANNED_COUNTER += 1;
                        COURSE_PLANNED_COUNT.setText(String.valueOf(PLANNED_COUNTER));
                        break;
                    case "Completed":
                        int COMPLETED_COUNTER = Integer.parseInt(COURSE_COMPLETED_COUNT
                        .getText().toString());
                        COMPLETED_COUNTER += 1;
                        COURSE_COMPLETED_COUNT.setText(String.valueOf(COMPLETED_COUNTER));
                        break;
                    case "Dropped":
                        int DROPPED_COUNTER = Integer.parseInt(COURSE_DROPPED_COUNT
                        .getText().toString());
                        DROPPED_COUNTER += 1;
                        COURSE_DROPPED_COUNT.setText(String.valueOf(DROPPED_COUNTER));

                }
            }
        });

        LIVE_ALL_ASSESS.observe(this, ASSES_LIST -> {

            for (Assessment ASSESS : ASSES_LIST) {
                final String ASSESS_TYPE = ASSESS.getType();

                switch (ASSESS_TYPE) {

                    case "Objective":
                        int OBJECTIVE_COUNTER = Integer.parseInt(ASSESS_OBJ_COUNT
                        .getText().toString());
                        OBJECTIVE_COUNTER += 1;
                        ASSESS_OBJ_COUNT.setText(String.valueOf(OBJECTIVE_COUNTER));
                        break;
                    case "Performance":
                        int PERFORMANCE_COUNTER = Integer.parseInt(ASSESS_PERFORM_COUNT
                        .getText().toString());
                        PERFORMANCE_COUNTER += 1;
                        ASSESS_PERFORM_COUNT.setText(String.valueOf(PERFORMANCE_COUNTER));
                        break;
                }
            }
        });
        setContentView(BINDING.getRoot());
    }
}
