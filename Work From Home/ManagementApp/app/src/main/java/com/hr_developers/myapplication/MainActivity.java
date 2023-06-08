package com.hr_developers.myapplication;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private CollectionReference reference;
    private EditText email, common_email;
    private Button find_btn, findComonData;
    public static ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        progressDialog = new ProgressDialog(this);
        firestore = FirebaseFirestore.getInstance();
        reference = firestore.collection("users");


        find_btn = findViewById(R.id.gd_find_btn);
        findComonData = findViewById(R.id.button);
        common_email = findViewById(R.id.editTextTextPersonName);
        email = findViewById(R.id.gd_user_email);
        find_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userMail = email.getText().toString().toLowerCase().replace(" ", "");
                progressDialog.setMessage("Loading data....");
                progressDialog.show();
                findUserDetail(userMail);
            }
        });

        findComonData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String commonEmail = common_email.getText().toString().toLowerCase().replace(" ", "");
                progressDialog.setMessage("Loading data....");
                progressDialog.show();
                findCommonDetail(commonEmail);
            }
        });

    }

    private void findCommonDetail(String commonEmail) {

        DocumentReference reference = FirebaseFirestore.getInstance().collection("common_data").document(commonEmail);
        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Alert!")
                            .setMessage("Are you sure ? You want to reset data.")
                            .setCancelable(false)
                            .setPositiveButton("Reset Now", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    progressDialog.show();
                                    dialogInterface.dismiss();
                                    dialogInterface.cancel();
                                    //reset data
                                    resetUserData(commonEmail);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .show();


            }
        });
    }

    private void resetUserData(String commonEmail) {

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("common_data").document(commonEmail);
        progressDialog.dismiss();
        documentReference.update("ad_seen_by_user", "0");
        Toast.makeText(this, "Data Reset successfully", Toast.LENGTH_SHORT).show();


    }

    private void findUserDetail(String userMail) {

        reference.whereEqualTo("email", userMail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                Intent intent = new Intent(MainActivity.this, UserDetailActivity.class);
                                intent.putExtra("email", documentSnapshot.getString("email"));
                                startActivity(intent);
                                email.setText("");
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "This email is not available on server", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "This email is not available on server", Toast.LENGTH_SHORT).show();
            }
        });


    }

}