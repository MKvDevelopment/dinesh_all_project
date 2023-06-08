package com.dinesh.adminwrokfromhome.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.dinesh.adminwrokfromhome.R;
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

public class MainActivity extends AppCompatActivity {

    private TextView spAmount, apAmount, ppAmount, TotalAmount, spUser, apUser, ppUser, totalUser, ftAmount, ftUser, edAmount, edUser, spinAmount, spinUser;
    private Button addWebLink, getUserdata, update, btn_app_on, btn_app_Off, fullTime, withdrawRequst, btn_everydayuser;
    private EditText blockReason;
    private String[] listItem;
    private Switch refreshTask, fullTimeWork, everydaywrok;
    private ProgressDialog progressDialog;
    private CollectionReference dc;
    private DocumentReference everydayworkstatus, fulltime;
    private String block_reason, appstatus, advanceIncome, premiumIncome, standardIncome, fullIncome, everydayIncome,spinIncome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findView();

        dc = FirebaseFirestore.getInstance().collection("App_utils");

        DocumentReference reference = dc.document("Totalincome");
        reference.addSnapshotListener((documentSnapshot, e) -> {
            standardIncome = documentSnapshot.getString("Standardincome");
            advanceIncome = documentSnapshot.getString("Advanceincome");
            premiumIncome = documentSnapshot.getString("Premiumincome");
            fullIncome = documentSnapshot.getString("Fulltimeincome");
            everydayIncome = documentSnapshot.getString("Everydayincome");
            spinIncome = documentSnapshot.getString("SpinIncome" );
            setGraphData(standardIncome, advanceIncome, premiumIncome, fullIncome, everydayIncome,spinIncome);
        });

        DocumentReference refresh = dc.document("App_status");
        refresh.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                String ref = documentSnapshot.getString("task_refresh");
                if (ref.equals("ON")) {
                    refreshTask.setChecked(true);
                } else {
                    // refreshTask.setChecked(false);
                }
            }
        });

        fulltime = FirebaseFirestore.getInstance().collection("Plan").document("Full_Time_Plan");
        fulltime.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                String full = documentSnapshot.getString("workstatus");
                if (full.equals("ON")) {
                    fullTimeWork.setChecked(true);
                } else {
                    //   fullTimeWork.setChecked(false);
                }
            }
        });

        everydayworkstatus = FirebaseFirestore.getInstance().collection("Plan").document("Everyday Plan");
        everydayworkstatus.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                String work = documentSnapshot.getString("workstatus");
                if (work.equals("ON")) {
                    everydaywrok.setChecked(true);
                }
            }
        });


        DocumentReference documentReference = dc.document("App_status");
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                block_reason = documentSnapshot.getString("reason");
                blockReason.setText(block_reason);
            }
        });


        btn_app_Off.setOnClickListener(view -> {
            String reason = blockReason.getText().toString();
            if (reason.isEmpty()) {
                blockReason.setError("Plese enter reason");
            } else {
                createDatabase("OFF", reason);
                Toast.makeText(MainActivity.this, "App is off", Toast.LENGTH_SHORT).show();
            }
        });

        btn_app_on.setOnClickListener(v -> {
            String reason = blockReason.getText().toString();
            createDatabase("ON", reason);
            Toast.makeText(MainActivity.this, "App is on", Toast.LENGTH_SHORT).show();
        });

        getUserdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, GetDataActivity.class));
            }
        });


        btn_everydayuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, EverydayUserRecyclerActivity.class));
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AllPlanRecyclerviewActivity.class));
            }
        });

        refreshTask.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    refresh.update("task_refresh", "ON");
                } else {
                    refresh.update("task_refresh", "OFF");
                }
            }
        });

        fullTimeWork.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    fulltime.update("workstatus", "ON");
                } else {
                    fulltime.update("workstatus", "OFF");
                }
            }
        });

        everydaywrok.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isCheckd) {
                if (isCheckd) {
                    everydayworkstatus.update("workstatus", "ON");
                } else {
                    everydayworkstatus.update("workstatus", "OFF");
                }
            }
        });
        addWebLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listItem = new String[]{"Free Plan", "Standard Plan", "Advance Plan", "Super Premium Plan"};
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Choose any Plan");
                builder.setSingleChoiceItems(listItem, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        switch (listItem[i]) {
                            case "Free Plan":
                                activityStart(listItem[i]);
                                dialog.dismiss();
                                break;
                            case "Standard Plan":
                                activityStart(listItem[i]);
                                dialog.dismiss();
                                break;
                            case "Advance Plan":
                                activityStart(listItem[i]);
                                dialog.dismiss();
                                break;
                            case "Super Premium Plan":
                                activityStart(listItem[i]);
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
        });

        fullTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, FullTimeRecyclerView.class));
            }
        });

        withdrawRequst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, WithdrawReqeustActivity.class));
            }
        });
        findViewById(R.id.notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,NotificationActivity.class));
            }
        });

    }

    private void setGraphData(String standardIncome, String advanceIncome, String premiumIncome, String fullIncome, String everydayIncome, String spinIncome) {
        spAmount.setText(standardIncome);
        apAmount.setText(advanceIncome);
        ppAmount.setText(premiumIncome);
        ftAmount.setText(fullIncome);
        edAmount.setText(everydayIncome);
        spinAmount.setText(spinIncome);
        //total amount
        int total = Integer.parseInt(standardIncome) + Integer.parseInt(advanceIncome)
                + Integer.parseInt(premiumIncome) + Integer.parseInt(fullIncome)
                + Integer.parseInt(everydayIncome)+ Integer.parseInt(spinIncome);
        TotalAmount.setText("Rs. " + total + "/-");

        //total user
        Integer spTotal = Integer.parseInt(standardIncome) / 200;
        Integer apTotal = Integer.parseInt(advanceIncome) / 500;
        Integer ppTotal = Integer.parseInt(premiumIncome) / 1000;
        Integer ftTotal = Integer.parseInt(fullIncome) / 2000;
        Integer edTotal = Integer.parseInt(everydayIncome) / 150;
        Integer spinTotal = Integer.parseInt(spinIncome) / 100;

        ppUser.setText(String.valueOf(ppTotal));
        apUser.setText(String.valueOf(apTotal));
        spUser.setText(String.valueOf(spTotal));
        ftUser.setText(String.valueOf(ftTotal));
        edUser.setText(String.valueOf(edTotal));
        spinUser.setText(String.valueOf(spinTotal));

        Integer totalUsers = ppTotal + apTotal + spTotal + ftTotal + edTotal+ spinTotal;
        totalUser.setText(String.valueOf(totalUsers) + " users");
    }

    private void findView() {

        spinUser = findViewById(R.id.textView44);
        spinAmount = findViewById(R.id.textView43);
        btn_everydayuser = findViewById(R.id.everUserdata);
        everydaywrok = findViewById(R.id.everyDayuser);
        withdrawRequst = findViewById(R.id.refreshTaskDetal);
        spAmount = findViewById(R.id.textView8);
        apAmount = findViewById(R.id.textView11);
        ppAmount = findViewById(R.id.textView14);
        edAmount = findViewById(R.id.textView37);
        ftAmount = findViewById(R.id.textView20);
        TotalAmount = findViewById(R.id.textView16);
        spUser = findViewById(R.id.textView9);
        apUser = findViewById(R.id.textView12);
        edUser = findViewById(R.id.textView38);
        ftUser = findViewById(R.id.textView21);
        ppUser = findViewById(R.id.textView15);
        totalUser = findViewById(R.id.textView17);
        progressDialog = new ProgressDialog(this);
        getUserdata = (Button) findViewById(R.id.getUserdata);
        update = (Button) findViewById(R.id.update);
        addWebLink = (Button) findViewById(R.id.add_webLink);
        btn_app_Off = (Button) findViewById(R.id.btnappOff);
        btn_app_on = findViewById(R.id.btnappOn);
        refreshTask = findViewById(R.id.refresh_task);
        fullTimeWork = findViewById(R.id.fulltime);
        blockReason = findViewById(R.id.ed_block_reason);
        fullTime = findViewById(R.id.fullTimeUser);
    }

    private void createDatabase(String string, String reason) {

        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("App_utils");
        DocumentReference documentReference = collectionReference.document("App_status");
        Map map = new HashMap();
        map.put("App_is", string);
        map.put("reason", reason);

        documentReference.set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "App status successfully change", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void activityStart(String s) {

        Intent intent = new Intent(MainActivity.this, AddWeblinkActivity.class);
        intent.putExtra("plan", s);
        startActivity(intent);

    }

    @Override
    protected void onStart() {
        super.onStart();
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        DocumentReference reference = dc.document("App_status");

        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                block_reason = documentSnapshot.getString("reason");
                appstatus = documentSnapshot.getString("App_is");
                setdataOnView(block_reason, appstatus);
            }
        });
    }

    private void setdataOnView(String block_reason, String appstatus) {

        progressDialog.dismiss();
        if (appstatus.equals("ON")) {
            btn_app_on.setText("App is already On");
            btn_app_Off.setText("You can Off Now");
            blockReason.setText(block_reason);

        } else {
            btn_app_Off.setText("App is Off");
            btn_app_on.setText("Click to On App");
            blockReason.setText(block_reason);
        }


    }

}
