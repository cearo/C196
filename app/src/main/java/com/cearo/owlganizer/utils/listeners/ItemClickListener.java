package com.cearo.owlganizer.utils.listeners;

import android.view.View;

// This interface will be used to implement custom onClickListeners for RecyclerView items

public interface ItemClickListener {
    // What to do when clicked
    void onClick(View view, int position);
}
