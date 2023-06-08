package com.dinesh.adminwrokfromhome.Activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.dinesh.adminwrokfromhome.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class WithdrawReqeustActivity extends AppCompatActivity {

    private Button deldate;
    private CalendarView calendarView;
    private EditText editText, selectdate;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private String[] listItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_reqeust);

        selectdate = findViewById(R.id.editTextTextPersonName);
        calendarView = findViewById(R.id.calendarView2);
        deldate = findViewById(R.id.button3);
        editText = findViewById(R.id.eddate);


        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                String date = i2 + "-" + (i1 + 1) + "-" + i;

                editText.setText(date);

            }
        });

        selectdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickPlan();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference(selectdate.getText().toString());

        deldate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(WithdrawReqeustActivity.this);
                dialog.setTitle("Are you sure?");
                dialog.setMessage("Deleting this account will result in completely removing your " +
                        "account from the system and you won't be able to access the app.");
                dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        databaseReference.child(editText.getText().toString()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(WithdrawReqeustActivity.this, "Data Deleted", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(WithdrawReqeustActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).setCancelable(false)
                        .show();

            }
        });


    }

    private void pickPlan() {

        listItem = new String[]{"Free withdraw list", "Standard withdraw list", "Advance withdraw list", "Premium withdraw list", "FullTime_withdraw_list", "everyday_withdraw_list"};
        AlertDialog.Builder builder = new AlertDialog.Builder(WithdrawReqeustActivity.this);
        builder.setTitle("Choose any Plan");
        builder.setSingleChoiceItems(listItem, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                switch (listItem[i]) {
                    case "Free withdraw list":
                        selectdate.setText("Free withdraw list");
                        dialog.dismiss();
                        break;
                    case "Standard withdraw list":
                        selectdate.setText("Standard withdraw list");
                        dialog.dismiss();
                        break;
                    case "Advance withdraw list":
                        selectdate.setText("Advance withdraw list");
                        dialog.dismiss();
                        break;
                    case "Premium withdraw list":
                        selectdate.setText("Premium withdraw list");
                        dialog.dismiss();
                        break;

                    case "FullTime_withdraw_list":
                        selectdate.setText("FullTime_withdraw_list");
                        dialog.dismiss();
                        break;

                    case "everyday_withdraw_list":
                        selectdate.setText("everyday_withdraw_list");
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
}