package com.cearo.owlganizer.adapters.recyclerviews;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cearo.owlganizer.databinding.TermItemBinding;
import com.cearo.owlganizer.models.Term;
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

public class TermAdapter extends RecyclerView.Adapter<TermAdapter.TermsViewHolder> {

    // A click listener for when an item in the RecyclerView is clicked
    private ItemClickListener itemClickListener;

    // A list of all terms from the database which will be used to populate the RecyclerView.
    private List<Term> allTerms;

    // Constructor
    public TermAdapter(List<Term> allTerms) {
        this.allTerms = allTerms;
    }

    // An API to set allTerms that will be called by a LiveData Observer
    public void setAllTerms(List<Term> allTerms) {
        this.allTerms = allTerms;
        notifyDataSetChanged();
    }

    // This method is called when the RecyclerView needs to create a new view holder for an item.
    @NonNull
    @Override
    public TermsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Creating a binding reference of term_item.xml layout.
        TermItemBinding binding = TermItemBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        // Return a newly created ViewHolder with a reference to the TermItemBinding.
        return new TermsViewHolder(binding);
    }

    // This method is called by the RecyclerView to display data at a certain position in the list.
    @Override
    public void onBindViewHolder(@NonNull TermsViewHolder holder, int position) {
        // Get the Term object from allTerms based on the view's position
        Term term = allTerms.get(position);
        // Storing a reference to the binding to keep it DRY
        TermItemBinding binding = holder.binding;

        // Using the binding to set the field values based on the current object
        binding.termTitle.setText(term.getTitle());

        // Used to create a formatted String representation of the LocalDate objects
        // Format will be "LLL dd, yyyy"
        DateTimeFormatter dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
        binding.termStart.setText(dateFormat.format(term.getStartDate()));
        binding.termEnd.setText(dateFormat.format(term.getEndDate()));
    }

    // Returns the number of items stored in allItems so the RecyclerView knows
    // how many ViewHolders to create
    @Override
    public int getItemCount() {
        // Null safety to prevent null object reference
        return allTerms != null ? allTerms.size() : 0;
    }

    // Setting the adapter's ItemClickListener field
    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    /*
        This ViewHolder takes in a view binding representation of term_item.xml to create a
        ViewHolder for each item in allTerms. View binding replaces calls to View.findViewById().
     */
    class TermsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Binding representing the layout term_item.xml
        TermItemBinding binding;

        // Constructor takes in a view binding as opposed to a View
        TermsViewHolder(@NonNull TermItemBinding binding) {
            // binding.getRoot() returns a view to pass to RecyclerView.ViewHolder's constructor.
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
