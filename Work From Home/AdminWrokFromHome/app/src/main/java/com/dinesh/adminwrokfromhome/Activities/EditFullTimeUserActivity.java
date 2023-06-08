package com.dinesh.adminwrokfromhome.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.dinesh.adminwrokfromhome.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class EditFullTimeUserActivity extends AppCompatActivity {

    private EditText email, wallet, viewpost, startdate, enddate, userstatus, reason;
    private Button update, block;
    private ProgressDialog dialog;
    private String[] listItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_full_time_user);

        block = findViewById(R.id.button4);
        reason = findViewById(R.id.editTextTextPersonName6);
        email = findViewById(R.id.editTextTextPersonName7);
        userstatus = findViewById(R.id.editTextTextPersonName10);
        wallet = findViewById(R.id.editTextTextPersonName8);
        viewpost = findViewById(R.id.editTextTextPersonName5);
        startdate = findViewById(R.id.editTextTextPersonName9);
        enddate = findViewById(R.id.editTextTextPersonName4);
        update = findViewById(R.id.button);

        userstatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickPlan();
            }
        });


        setdataonView();

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("full_time_user").document(getIntent().getStringExtra("email"));

        block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map map = new HashMap();
                map.put("status", userstatus.getText().toString());
                map.put("reason", reason.getText().toString());

                documentReference.update(map).addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        Toast.makeText(EditFullTimeUserActivity.this, "updated", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new ProgressDialog(EditFullTimeUserActivity.this);
                dialog.setMessage("Updating");
                dialog.show();
                updateData();
            }
        });

    }

    private void updateData() {
        String eml = email.getText().toString();
        String bal = wallet.getText().toString();
        String post = viewpost.getText().toString();
        String start = startdate.getText().toString();
        String end = enddate.getText().toString();

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("full_time_user").document(eml);
        Map map = new HashMap();
        map.put("email", eml);
        map.put("plan_end_date", end);
        map.put("plan_start_date", start);
        map.put("view_post", post);
        map.put("wallet", bal);

        documentReference.update(map).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                dialog.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(EditFullTimeUserActivity.this, "Successfull Update", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditFullTimeUserActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(EditFullTimeUserActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void pickPlan() {
        listItem = new String[]{"Block", "Unblock"};
        AlertDialog.Builder builder = new AlertDialog.Builder(EditFullTimeUserActivity.this);
        builder.setTitle("Choose any Plan");
        builder.setSingleChoiceItems(listItem, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                switch (listItem[i]) {
                    case "Block":
                        userstatus.setText("Block");
                        dialog.dismiss();
                        break;
                    case "Unblock":
                        userstatus.setText("Unblock");
                        dialog.dismiss();
                        break;
                }

            }
        }).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setdataonView() {
        email.setText(getIntent().getStringExtra("email"));
        wallet.setText(getIntent().getStringExtra("wallet"));
        viewpost.setText(getIntent().getStringExtra("post"));
        startdate.setText(getIntent().getStringExtra("start"));
        enddate.setText(getIntent().getStringExtra("end"));
        reason.setText(getIntent().getStringExtra("reason"));
        userstatus.setText(getIntent().getStringExtra("status"));
    }
}