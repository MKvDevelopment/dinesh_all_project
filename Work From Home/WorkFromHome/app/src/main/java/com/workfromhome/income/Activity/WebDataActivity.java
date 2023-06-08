package com.workfromhome.income.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.workfromhome.income.Adapter.WebAdapter;
import com.workfromhome.income.Model.WebModel;
import com.workfromhome.income.OneTimeActivity.NetworkChangeReceiver;
import com.workfromhome.income.R;

import java.util.ArrayList;

public class WebDataActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView webRecyclerView;
    private WebAdapter adapter;
    private ArrayList<WebModel> list1;
    private RecyclerViewItemClick clickInterface;
    private String plan_name, email, balance,withdraw_request_count;
    private ProgressDialog dialog;
    private final int requestCode = 101;
    private FirebaseFirestore firestore;
    private CollectionReference globalRef, visitedReference;
    private DocumentReference userRef,adminRef;
    private NetworkChangeReceiver broadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_data);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);


        //check network connectivity
        broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);

        //getting pln name from main activity
        plan_name = getIntent().getStringExtra("plan");

        //progress dialog
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading....");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);


        //Toolbar
        toolbar = findViewById(R.id.web_data_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //initiate firebase
        firestore = FirebaseFirestore.getInstance();
        adminRef = firestore.collection("App_utils").document("App_status");
        globalRef = firestore.collection("Plan").document(plan_name).collection("webdata");
        visitedReference = firestore.collection("common_data").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).collection("user_visited_links");
        userRef = firestore.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        //finding recycler views
        list1 = new ArrayList<>();
        webRecyclerView = findViewById(R.id.lst_view);
        webRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        webRecyclerView.setItemAnimator(new DefaultItemAnimator());
        webRecyclerView.setHasFixedSize(true);

        clickInterface = new RecyclerViewItemClick() {
            @Override
            public void onItemClickListner(WebModel model) {
                dialog.show();

                Intent intent = new Intent(WebDataActivity.this, ShowWebsiteActivity.class);
                intent.putExtra("time", model.getWeb_time());
                intent.putExtra("id", model.getWeb_id());
                intent.putExtra("url", model.getWeblink());
                intent.putExtra("count",withdraw_request_count);
                intent.putExtra("wallet", model.getWeb_income());
                startActivityForResult(intent, requestCode);
            }
        };
        adapter = new WebAdapter(list1, clickInterface);
        webRecyclerView.setAdapter(adapter);


    }

    private void setUpSimpleRecyclerView() {

        globalRef.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                list1.clear();
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    WebModel webModel = documentSnapshot.toObject(WebModel.class);
                    update(webModel, webModel.getWeb_id());
                    list1.add(webModel);
                }
                adapter.notifyDataSetChanged();

            }
        });

    }

    private void update(final WebModel webModel, final String web_id) {

        visitedReference.get().addOnCompleteListener(task -> {

            if (!task.getResult().isEmpty()) {

                visitedReference.document(web_id).get().addOnSuccessListener(documentSnapshot -> {

                    Object isDone = documentSnapshot.getBoolean("isDone");
                    if (isDone == null) {
                        webModel.setDone(false);
                    } else {
                        webModel.setDone((boolean) isDone);
                    }
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(WebDataActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(WebDataActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public interface RecyclerViewItemClick {
        void onItemClickListner(WebModel model);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == this.requestCode) {
            dialog.show();
            setUpSimpleRecyclerView();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        dialog.show();
        checkTaskRef(adminRef);
        setUpSimpleRecyclerView();
    }

    private void checkTaskRef(DocumentReference adminRef) {
        adminRef.addSnapshotListener((documentSnapshot, e) -> {
            String appstatus = documentSnapshot.getString("task_refresh");
            if (appstatus.equals("OFF")) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(WebDataActivity.this);
                alertDialog.setTitle("Please Wait!");
                alertDialog.setMessage("Our app is Refresh your Previous Task. Please wait until task is refreshed (Almost 15 minutes).");
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                }).show();
            }
        });
    }

    private void checkwithdrawLimit() {

        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                balance = documentSnapshot.getString("wallet");
                email = documentSnapshot.getString("email");
                if (plan_name.equals("Free Plan")) {

                    withdraw_request_count = getIntent().getStringExtra("count");
                    if (withdraw_request_count.equals("0")) {
                        boolean bal = Float.parseFloat(balance) >= 7;
                        if (bal) {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(WebDataActivity.this);
                            alertDialog.setTitle("Alert!");
                            alertDialog.setMessage("You are eligible to withdraw your money.");
                            alertDialog.setCancelable(false);
                            alertDialog.setPositiveButton("Withdraw Now", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(WebDataActivity.this, WithdrawActivity.class);
                                    intent.putExtra("email", email);
                                    intent.putExtra("plan", plan_name);
                                    startActivity(intent);
                                    dialog.dismiss();
                                }
                            }).show();
                        }
                    }
                }

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        checkwithdrawLimit();
    }
}
