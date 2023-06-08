package com.hr_developers.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class UserDetailActivity extends AppCompatActivity {

    private TextView name, email, withdrawCount;
    private EditText plan, wallet, block_reason;
    private Button updtButton;
    private FirebaseFirestore firestore;
    private CollectionReference collectionReference;
    private DocumentReference documentReference,resetDataReference;
    private String user_email;
    private ProgressDialog progressDialog;
    private RadioGroup userStatus;
    private RadioButton block;
    private RadioButton unblock;
    private String[] listItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        //get intent from GetDataActivtiy
        user_email = getIntent().getStringExtra("email");

        //initiate firebase
        firestore = FirebaseFirestore.getInstance();
        collectionReference = firestore.collection("users");
        documentReference = collectionReference.document(user_email);

        //initiate views
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        updtButton = findViewById(R.id.update_btn);
        name = (TextView) findViewById(R.id.name);
        email = (TextView) findViewById(R.id.email);
        plan = (EditText) findViewById(R.id.plan);
        wallet = (EditText) findViewById(R.id.wallet);
        withdrawCount = (TextView) findViewById(R.id.withdraw_count);
        userStatus = (RadioGroup) findViewById(R.id.user_status);
        block = (RadioButton) findViewById(R.id.block);
        unblock = (RadioButton) findViewById(R.id.unblock);
        block_reason = findViewById(R.id.block_reson);

        //retvrive data
        retriveData(documentReference);

        plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickPlan();
            }
        });

        //event
        updtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setTitle("Please Wait..");
                progressDialog.setMessage("Updating data....");
                progressDialog.show();
                UpdateData(documentReference);
            }
        });


    }

    private void pickPlan() {
        listItem = new String[]{"Free Plan", "Standard Plan", "Advance Plan", "Super Premium Plan"};
        AlertDialog.Builder builder = new AlertDialog.Builder(UserDetailActivity.this);
        builder.setTitle("Choose any Plan");
        builder.setSingleChoiceItems(listItem, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                switch (listItem[i]) {
                    case "Free Plan":
                        plan.setText("Free Plan");
                        dialog.dismiss();
                        break;
                    case "Standard Plan":
                        plan.setText("Standard Plan");
                        dialog.dismiss();
                        break;
                    case "Advance Plan":
                        plan.setText("Advance Plan");
                        dialog.dismiss();
                        break;
                    case "Super Premium Plan":
                        plan.setText("Super Premium Plan");
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


    //update data on server
    private void UpdateData(DocumentReference documentReference) {

        //getting new updated data
        final String status = ((RadioButton) findViewById(userStatus.getCheckedRadioButtonId())).getText().toString();
        String new_amount = wallet.getText().toString().trim();
        String new_plan = plan.getText().toString().trim();
        String blockReason = block_reason.getText().toString();

        //wrapping data
        Map map = new HashMap();
        map.put("plan", new_plan);
        map.put("wallet", new_amount);
        map.put("user_status", status);
        map.put("block_reason", blockReason);

        documentReference
                .update(map)
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(UserDetailActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                            //reset web data according to plan
                            resetDataReference=FirebaseFirestore.getInstance().collection("common_data").document(user_email);
                            resetDataReference.delete();
                            Toast.makeText(UserDetailActivity.this, "Data reset successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(UserDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    //retrive data form server
    private void retriveData(final DocumentReference documentReference) {

        documentReference
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        progressDialog.dismiss();
                        if (documentSnapshot != null) {
                            String name = documentSnapshot.getString("name");
                            String email = documentSnapshot.getString("email");
                            String mob = documentSnapshot.getString("mobile");
                            String wallet = documentSnapshot.getString("wallet");
                            String withdraCuount = documentSnapshot.getString("withdraw_count");
                            String plan = documentSnapshot.getString("plan");
                            String status = documentSnapshot.getString("user_status");
                            String block_des = documentSnapshot.getString("block_reason");

                            setDataToView(name, email, mob, wallet, withdraCuount, plan, status, block_des);

                        } else {
                            Toast.makeText(UserDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    //set data to screen
    private void setDataToView(String nm, String eml, String mob, String walet, String withdraCuount, String plane, String stats, String reson) {
        name.setText(nm);
        email.setText(eml);
        wallet.setText(walet);
        withdrawCount.setText(withdraCuount);
        plan.setText(plane);
        block_reason.setText(reson);
        if (stats.equals("Block"))         //gender value checking
        {
            block.setChecked(true);            //setting value of gender
        }
        if (stats.equals("Unblock")) {
            unblock.setChecked(true);
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        progressDialog.setMessage("Loading data....");
        progressDialog.show();

    }

}