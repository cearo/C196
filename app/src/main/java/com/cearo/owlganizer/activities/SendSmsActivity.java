package com.cearo.owlganizer.activities;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.cearo.owlganizer.databinding.ActivitySendSmsBinding;

import java.util.Formatter;
import java.util.Locale;

public class SendSmsActivity extends AppCompatActivity {

    private boolean isSmsPermissionGranted;

    private final int MY_PERMISSIONS_REQUEST_SEND_SMS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isSmsPermissionGranted = ContextCompat.checkSelfPermission(
                this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;

        final LayoutInflater INFLATER = LayoutInflater.from(this);
        final ActivitySendSmsBinding BINDING = ActivitySendSmsBinding.inflate(INFLATER);

        final EditText RECIPIENT = BINDING.smsRecipient;
        final EditText MESSAGE = BINDING.smsMessage;
        final TextView TITLE = BINDING.smsTitle;
        final Button SEND = BINDING.sendSms;
        // Disabling field
        MESSAGE.setKeyListener(null);
        // Will format phone number entered
        RECIPIENT.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        final Intent FROM_NOTE_DETAIL = getIntent();
        final String NOTE_TITLE = FROM_NOTE_DETAIL.getStringExtra("noteTitle");
        final String NOTE_BODY = FROM_NOTE_DETAIL.getStringExtra("noteBody");

        TITLE.setText(NOTE_TITLE);
        MESSAGE.setText(NOTE_BODY);

        SEND.setOnClickListener(view -> {
            final String RECIPIENT_ENTERED = RECIPIENT.getText().toString();
            final boolean IS_FIELD_EMPTY = RECIPIENT_ENTERED.isEmpty();

            if (!isSmsPermissionGranted) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.SEND_SMS)) {
                    final String RATIONALE_TITLE = "Why we need SMS Permissions";
                    final String RATIONALE_BODY = "In order to send Notes via SMS, we need " +
                            "permission to do so from you, our grand and glorious leader. " +
                            "Please sir, grant us this kindness";
                    final String CNCL_BTN_TEXT = "Close";
                    final AlertDialog.Builder BUILDER = new AlertDialog.Builder(this);
                    BUILDER.setTitle(RATIONALE_TITLE);
                    BUILDER.setMessage(RATIONALE_BODY);
                    BUILDER.setNegativeButton(CNCL_BTN_TEXT, (((dialog, which) -> {
                        dialog.cancel();
                    })));
                    BUILDER.show();
                    requestPermissions();
                } else {
                    requestPermissions();
                }
            }
            else if (!IS_FIELD_EMPTY) {
                final StringBuilder FORMATTER_BUILDER = new StringBuilder();
                final Formatter FORMATTER = new Formatter(FORMATTER_BUILDER, Locale.US);
                final String SMS_MESSAGE = FORMATTER
                        .format("Note Title: %s\nNote Body: %s", NOTE_TITLE, NOTE_BODY)
                        .toString();
                final SmsManager MANAGER = SmsManager.getDefault();
                final PendingIntent PENDING_INTENT = PendingIntent.getBroadcast(this, 0,
                        new Intent(SMS_MESSAGE), 0);
                MANAGER.sendTextMessage(RECIPIENT_ENTERED, null, SMS_MESSAGE,
                        PENDING_INTENT, null);

                final String SMS_SENT = "Note sent via SMS";
                final int TOAST_DURATION = Toast.LENGTH_SHORT;
                final Toast TOAST = Toast.makeText(this, SMS_SENT, TOAST_DURATION);
                TOAST.show();
            }
            else RECIPIENT.setError("Field cannot be blank!");
        });
        setContentView(BINDING.getRoot());
    }

    public void requestPermissions() {
        final String[] PERMISSION_REQUEST_PARAMS = {Manifest.permission.SEND_SMS};
        ActivityCompat.requestPermissions(this, PERMISSION_REQUEST_PARAMS,
                MY_PERMISSIONS_REQUEST_SEND_SMS);
    }
}
