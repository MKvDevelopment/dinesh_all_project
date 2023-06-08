package com.typingwork.admintypingwork.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.typingwork.admintypingwork.R;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class AdminDataChangeActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_data_change);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("Please Wait!");
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("App_Utils").document("App_Utils");

        documentReference.addSnapshotListener((value, error) -> {
            if (value.exists()) {
                ((EditText) findViewById(R.id.ed1)).setText(value.getString("chat_off_reason"));
                ((EditText) findViewById(R.id.ed2)).setText(value.getString("ads_payment_fee"));
                ((EditText) findViewById(R.id.ed3)).setText(value.getString("wrong_entry_panelty_amount"));
                ((EditText) findViewById(R.id.ed4)).setText(value.getString("razorpay_upi"));
                progressDialog.dismiss();
            }
        });


        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("admin").child("total_ads_install");

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String install = snapshot.getValue().toString();

                ((TextView)findViewById(R.id.tv1)).setText("Total Install:- "+install);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminDataChangeActivity.this, "Data Fetching Error!", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

        findViewById(R.id.button1).setOnClickListener(view -> {
            progressDialog.show();
            Map map = new HashMap();
            map.put("chat_off_reason", ((EditText) findViewById(R.id.ed1)).getText().toString().trim());
            map.put("ads_payment_fee", ((EditText) findViewById(R.id.ed2)).getText().toString().trim());
            map.put("wrong_entry_panelty_amount", ((EditText) findViewById(R.id.ed3)).getText().toString().trim());
            map.put("razorpay_upi", ((EditText) findViewById(R.id.ed4)).getText().toString().trim());


            documentReference.update(map).addOnSuccessListener(o -> {
                progressDialog.dismiss();
            });

        });

    }
}