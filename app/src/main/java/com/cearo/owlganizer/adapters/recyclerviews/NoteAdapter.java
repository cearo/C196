package com.cearo.owlganizer.adapters.recyclerviews;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cearo.owlganizer.databinding.NoteItemBinding;
import com.cearo.owlganizer.models.Note;
import com.cearo.owlganizer.utils.listeners.ItemClickListener;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder>{
    ItemClickListener itemClickListener;

    private List<Note> courseNotes;

    public NoteAdapter(List<Note> courseNotes) {
        this.courseNotes = courseNotes;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setCourseNotes(List<Note> courseNotes) {
        this.courseNotes = courseNotes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        NoteItemBinding binding = NoteItemBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new NoteViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = courseNotes.get(position);
        NoteItemBinding binding = holder.binding;

        binding.noteTitle.setText(note.getTitle());
    }

    @Override
    public int getItemCount() {
        return courseNotes != null ? courseNotes.size() : 0;
    }

    class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        NoteItemBinding binding;

        NoteViewHolder(@NonNull NoteItemBinding binding) {
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
