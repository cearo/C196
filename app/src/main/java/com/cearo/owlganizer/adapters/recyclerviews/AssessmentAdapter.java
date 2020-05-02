package com.cearo.owlganizer.adapters.recyclerviews;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cearo.owlganizer.databinding.AssessmentItemBinding;
import com.cearo.owlganizer.models.Assessment;
import com.cearo.owlganizer.utils.listeners.ItemClickListener;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

public class AssessmentAdapter extends RecyclerView
        .Adapter<AssessmentAdapter.AssessmentViewHolder> {

    private ItemClickListener itemClickListener;

    private List<Assessment> courseAssessments;

    public AssessmentAdapter(List<Assessment> assessments) {
        this.courseAssessments = assessments;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setCourseAssessments(List<Assessment> courseAssessments) {
        this.courseAssessments = courseAssessments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AssessmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AssessmentItemBinding binding = AssessmentItemBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new AssessmentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AssessmentViewHolder holder, int position) {
        Assessment assess = courseAssessments.get(position);
        AssessmentItemBinding binding = holder.binding;
        binding.assessmentTitle.setText(assess.getTitle());
        final DateTimeFormatter FORMATTER = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
        binding.assessmentDueDate.setText(FORMATTER.format(assess.getDueDate()));
    }

    @Override
    public int getItemCount() {
        return courseAssessments != null ? courseAssessments.size() : 0;
    }

    class AssessmentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        AssessmentItemBinding binding;

        AssessmentViewHolder(@NonNull AssessmentItemBinding binding) {
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
