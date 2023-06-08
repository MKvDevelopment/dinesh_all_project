package com.document.security;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.document.dbsecurity.R;
import com.document.security.adapter.F_Adapter;
import com.document.security.model.F_Model;
import com.document.security.my_dilog.F_Dilog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rc;
    ProgressDialog dialog;
    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dialog=new ProgressDialog(MainActivity.this);
        dialog.setMessage("Please Wait...");

        rc = (RecyclerView) findViewById(R.id.rc);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                F_Dilog f_dilog=new F_Dilog(MainActivity.this);
                f_dilog.show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        dialog.show();
        FirebaseDatabase.getInstance().getReference().child("main_data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> key =new ArrayList<>();
                List<F_Model> data =new ArrayList<>();

                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    F_Model f_model=dataSnapshot.getValue(F_Model.class);
                    key.add(dataSnapshot.getKey());
                    data.add(f_model);
                }
                dialog.dismiss();
                F_Adapter f_adapter=new F_Adapter(key,data);
                rc.setAdapter(f_adapter);
                rc.setHasFixedSize(true);
                rc.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}