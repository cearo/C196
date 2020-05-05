package com.cearo.owlganizer.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.appcompat.app.AppCompatActivity;

import com.cearo.owlganizer.R;
import com.cearo.owlganizer.databinding.ActivityMainBinding;

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
        BINDING.trackProgress.setOnClickListener(view -> {
            final Intent TO_TRACK_PROGRESS = new Intent(this,
                    TrackProgressActivity.class);
            startActivity(TO_TRACK_PROGRESS);
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
