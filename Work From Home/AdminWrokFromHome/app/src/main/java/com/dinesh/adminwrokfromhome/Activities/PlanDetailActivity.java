package com.dinesh.adminwrokfromhome.Activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class PlanDetailActivity extends AppCompatActivity {

    private Button ads_update;
    private EditText perAdIncom, adSeenLimit, secondLimit, firstLimit;
    private TextView planName;
    ;
    private String name;
    private String first_withdraw_limit, seconde_withdrw_limit, total_ad_seen, webvisit_time, per_ad_income, webTitle, weblink, webIncome;

    private FirebaseFirestore firestore;
    private CollectionReference collectionReference;
    private DocumentReference documentReference;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_detail);

        name = getIntent().getStringExtra("name");

        //initiate views
        progressDialog=new ProgressDialog(this);
        ads_update = (Button) findViewById(R.id.pd_update_ads);
        perAdIncom = (EditText) findViewById(R.id.pd_per_ad_incom);
        adSeenLimit = (EditText) findViewById(R.id.pd_ad_seen_limit);
        secondLimit = (EditText) findViewById(R.id.pd_second_limit);
        firstLimit = (EditText) findViewById(R.id.pd_first_limit);
        planName = (TextView) findViewById(R.id.pd_plan_name);

        //initiate firebse
        firestore = FirebaseFirestore.getInstance();
        collectionReference = firestore.collection("Plan");
        documentReference = collectionReference.document(name);

        retriveData(collectionReference);

        ads_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkFields();

            }
        });


    }

    private void retriveData(CollectionReference collectionReference) {

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                progressDialog.dismiss();
                if (documentSnapshot != null) {

                    first_withdraw_limit = documentSnapshot.getString("first_withdraw_limit");
                    name = documentSnapshot.getString("name");
                    seconde_withdrw_limit = documentSnapshot.getString("second_withdraw_limit");
                    total_ad_seen = documentSnapshot.getString("total_ad_seen");
                    per_ad_income = documentSnapshot.getString("per_ad_income");
                    setDataToViews();


                } else {
                    Toast.makeText(PlanDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    private void setDataToViews() {

        perAdIncom.setText(per_ad_income);
        adSeenLimit.setText(total_ad_seen);
        secondLimit.setText(seconde_withdrw_limit);
        firstLimit.setText(first_withdraw_limit);
        planName.setText(name);

    }

    private void setDataToServer(CollectionReference collectionReference) {


        Map map = new HashMap();
        map.put("name", name);
        map.put("first_withdraw_limit", first_withdraw_limit);
        map.put("second_withdraw_limit", seconde_withdrw_limit);
        map.put("total_ad_seen", total_ad_seen);
        map.put("per_ad_income", per_ad_income);


        documentReference.update(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(PlanDetailActivity.this, "Update successful", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(PlanDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void checkFields() {

        //getting fields
        name = planName.getText().toString();
        first_withdraw_limit = firstLimit.getText().toString();
        seconde_withdrw_limit = secondLimit.getText().toString();
        total_ad_seen = adSeenLimit.getText().toString();
        per_ad_income = perAdIncom.getText().toString();


        if (name.isEmpty()) {
            planName.setError("Enter plan name");
        } else if (first_withdraw_limit.isEmpty()) {
            firstLimit.setError("Enter First limit");
        } else if (total_ad_seen.isEmpty()) {
            adSeenLimit.setError("Enter limit of ads");
        } else if (per_ad_income.isEmpty()) {
            perAdIncom.setError("Enter per ads income");
        }else {
            progressDialog.setMessage("Uploading....");
            progressDialog.show();
            setDataToServer(collectionReference);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        progressDialog.setMessage("Loading data...");
        progressDialog.show();
    }
}
