package com.cearo.owlganizer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.appcompat.app.AppCompatActivity;

import com.cearo.owlganizer.databinding.ActivityNewNoteBinding;

public class NewNoteActivity extends AppCompatActivity {

    ActivityNewNoteBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final LayoutInflater INFLATER = LayoutInflater.from(this);
        binding = ActivityNewNoteBinding.inflate(INFLATER);

        final Intent FROM_COURSE_DETAIL = getIntent();
        final long PARENT_COURSE_ID =
                FROM_COURSE_DETAIL.getLongExtra("courseId", 0);

        binding.newNoteSave.setEnabled(false);
        setContentView(binding.getRoot());
    }
}
