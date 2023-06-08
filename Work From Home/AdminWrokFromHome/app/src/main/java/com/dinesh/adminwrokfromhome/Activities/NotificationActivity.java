package com.dinesh.adminwrokfromhome.Activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.dinesh.adminwrokfromhome.R;

public class NotificationActivity extends AppCompatActivity {

    private Button sendButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        sendButton = findViewById(R.id.send_button);
        sendButton.setOnClickListener(v -> {

            String message = ((TextView) findViewById(R.id.notification_text)).getText().toString();
            String description = ((TextView) findViewById(R.id.description_text)).getText().toString();


        });

    }

}