package com.dinesh.adminwrokfromhome.Activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dinesh.adminwrokfromhome.Adapters.AddWebRecyclerAdapter;
import com.dinesh.adminwrokfromhome.Models.WebDataModel;
import com.dinesh.adminwrokfromhome.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AddWeblinkActivity extends AppCompatActivity {

    private EditText ed_weblink, ed_webTime, ed_webTitle, ed_webIncome;
    private ProgressDialog progressDialog;
    private String plan_nm, link, title, time, incom;

    //refer code generate code
    private final String LETTER="ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final char[] ALPHANUMERIC=(LETTER+LETTER.toLowerCase() +"0123456789").toCharArray();


    private FloatingActionButton actionButton;
    private RecyclerView recyclerView;
    private AddWebRecyclerAdapter adapter;

    private FirebaseFirestore firestore;
    private DocumentReference documentReference;
    private CollectionReference collectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_weblink);

        plan_nm = getIntent().getStringExtra("plan");

        progressDialog = new ProgressDialog(this);
        firestore = FirebaseFirestore.getInstance();
        collectionReference = firestore.collection("Plan").document(plan_nm).collection("webdata");
        documentReference = firestore.collection("Plan").document(plan_nm).collection("webdata").document();

        recyclerView = findViewById(R.id.lst_view);
        actionButton = findViewById(R.id.floatingActionButton);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInputAndSetData();
            }
        });

        setUpRecyclerView();
    }


    private void setUpRecyclerView() {

        Query query = collectionReference;
        FirestoreRecyclerOptions<WebDataModel> options = new FirestoreRecyclerOptions
                .Builder<WebDataModel>()
                .setQuery(query, WebDataModel.class)
                .build();

        adapter = new AddWebRecyclerAdapter(options);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.deleteItems(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerView);

    }

    private void getInputAndSetData() {

        AlertDialog.Builder alert = new AlertDialog.Builder(AddWeblinkActivity.this);
        View view = getLayoutInflater().inflate(R.layout.custom_dialog_weblink, null);


        ed_weblink = (EditText) view.findViewById(R.id.toast_web_link);
        ed_webIncome = (EditText) view.findViewById(R.id.toast_web_income);
        ed_webTime = (EditText) view.findViewById(R.id.toast_web_time);
        ed_webTitle = (EditText) view.findViewById(R.id.toast_web_title);

        Button btn_cancel = (Button) view.findViewById(R.id.toast_cancel);
        Button btn_submit = (Button) view.findViewById(R.id.toast_submit);
        alert.setView(view);

        final AlertDialog custom_dialog = alert.create();
        custom_dialog.setCanceledOnTouchOutside(false);
        custom_dialog.show();

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                custom_dialog.dismiss();
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Uploading Data....");
                progressDialog.show();

                String generatedCode= generateRandomNumber(20);

                link = ed_weblink.getText().toString();
                title = ed_webTitle.getText().toString();
                incom = ed_webIncome.getText().toString();
                time = ed_webTime.getText().toString();


                Map map = new HashMap();
                map.put("web_title", title);
                map.put("weblink", "https://"+link);
                map.put("web_time", time);
                map.put("web_income", incom);
                map.put("web_id",generatedCode);

                collectionReference.document(generatedCode)
                        .set(map)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    Toast.makeText(AddWeblinkActivity.this, "updated", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(AddWeblinkActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
                custom_dialog.dismiss();
            }
        });

    }

    private String generateRandomNumber(int length) {

        StringBuilder result=new StringBuilder();
        for (int i=0;i<length;i++)
        {
            result.append(ALPHANUMERIC[new Random().nextInt(ALPHANUMERIC.length)]);
        }

        return result.toString();

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        progressDialog.setMessage("Fetching data...");
        // progressDialog.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
