package com.cearo.owlganizer.adapters.recyclerviews;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cearo.owlganizer.databinding.CourseItemBinding;
import com.cearo.owlganizer.models.Course;
import com.cearo.owlganizer.utils.listeners.ItemClickListener;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;


/*
    This class is used by the RecyclerView to create ViewHolder objects for every
    object stored in allTerms.

    This class was created using these guides as reference:

    No View Binding: https://codelabs.developers.google.com/codelabs/android-room-with-a-view/#10
    https://stackoverflow.com/questions/60313719/how-to-use-android-view-binding-with-recyclerview
 */


public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    // A click listener for when an item in the RecyclerView is clicked.
    private ItemClickListener itemClickListener;

    // A list of all Courses for a particular Term which will populate the RecyclerView.
    private List<Course> termCourses;

    public CourseAdapter(List<Course> termCourses) {
        this.termCourses = termCourses;
    }

    // A setter that will be called by the LiveData Observer.
    public void setTermCourses(List<Course> termCourses) {
        this.termCourses = termCourses;
        notifyDataSetChanged();
    }

    // A setter for the ItemClickListener
    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CourseItemBinding binding = CourseItemBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CourseViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = termCourses.get(position);
        CourseItemBinding binding = holder.binding;

        binding.courseTitle.setText(course.getTitle());

        final DateTimeFormatter DATE_FORMAT = DateTimeFormatter
                .ofLocalizedDate(FormatStyle.MEDIUM);
        binding.courseStart.setText(DATE_FORMAT.format(course.getStartDate()));
        binding.courseEnd.setText(DATE_FORMAT.format(course.getEndDate()));
        binding.courseStatus.setText(course.getStatus());
    }

    @Override
    public int getItemCount() {
        return termCourses != null ? termCourses.size() : 0;
    }

    class CourseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CourseItemBinding binding;

         CourseViewHolder(@NonNull CourseItemBinding binding) {
            super(binding.getRoot());
            View view = binding.getRoot();
            view.setOnClickListener(this);
            this.binding = binding;
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) itemClickListener.onClick(v, getAdapterPosition());
        }
    }
}
