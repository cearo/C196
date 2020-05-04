package com.cearo.owlganizer.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cearo.owlganizer.R;
import com.cearo.owlganizer.adapters.recyclerviews.TermAdapter;
import com.cearo.owlganizer.databinding.ActivityMainBinding;
import com.cearo.owlganizer.models.Mentor;
import com.cearo.owlganizer.models.Term;
import com.cearo.owlganizer.models.viewmodels.TermViewModel;
import com.cearo.owlganizer.utils.Constants;
import com.cearo.owlganizer.utils.listeners.ItemClickListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final LayoutInflater INFLATER = LayoutInflater.from(this);
        final ActivityMainBinding BINDING = ActivityMainBinding.inflate(INFLATER);

        BINDING.manageTerms.setOnClickListener(view -> {
            final Intent TO_TERM_LIST = new Intent(this, TermListActivity.class);
            startActivity(TO_TERM_LIST);
        });
        createNotificationChannel();
        setContentView(BINDING.getRoot());
    }
    private void createNotificationChannel() {
        final String CHANNEL_ID = "1";
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}
