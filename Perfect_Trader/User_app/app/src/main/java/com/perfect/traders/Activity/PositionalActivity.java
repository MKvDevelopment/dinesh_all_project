package com.perfect.traders.Activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.perfect.traders.Adatper.PositionalAdapter;
import com.perfect.traders.Constant.NetworkChangeReceiver;
import com.perfect.traders.Model.PositionalModel;
import com.perfect.traders.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class PositionalActivity extends AppCompatActivity {

    private PositionalAdapter adapter;
    private DocumentReference utilsRef;
    private String holidayText;
    private NetworkChangeReceiver broadcastReceiver;
    private CollectionReference positionalRef, previousPostionalRef;
    private final ArrayList<PositionalModel> list1 = new ArrayList<>();
    private final ArrayList<PositionalModel> list2 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_positional);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        //check network connectivity
        broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);


        Toolbar toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        positionalRef = FirebaseFirestore.getInstance().collection("Today_Positional_Call");
        utilsRef = FirebaseFirestore.getInstance().collection("App_Utils").document("utils");

        utilsRef.addSnapshotListener((value, error) -> {
            holidayText = value.getString("no_call_text");
        });
        previousPostionalRef = FirebaseFirestore.getInstance().collection("Previous_Positional_Call");

        getAllData();

        ((Button)findViewById(R.id.btn_subscribe_positional)).setOnClickListener(view -> {
            startActivity(new Intent(PositionalActivity.this, MembershipActivity.class));
            finish();
        });
    }

    private void getAllData() {
        RecyclerView today_recyclerView = findViewById(R.id.recycler_positional);
        RecyclerView previous_recyclerView = findViewById(R.id.recycler_previous_positional);

        if (MainActivity.SUBSCRIPTION.contains("YES")){
            ((CardView)findViewById(R.id.cardview7)).setVisibility(View.GONE);
            (findViewById(R.id.recycler_positional)).setVisibility(View.VISIBLE);
            positionalRef.orderBy("sDate").addSnapshotListener((value, error) -> {
                if (!value.isEmpty()) {
                    list1.clear();
                    findViewById(R.id.noCallCardView).setVisibility(View.GONE);

                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd/MM/yy");
                    String today_date = simpleDateFormat1.format(calendar.getTime());

                    for (DocumentSnapshot documentSnapshot : value.getDocuments()) {

                        Date date = new Date(Long.parseLong(documentSnapshot.getString("sDate")));
                        DateFormat f1 = new SimpleDateFormat("dd/MM/yy");

                        PositionalModel model = new PositionalModel();
                        if (f1.format(date).equals(today_date)) {
                            model.setcName(documentSnapshot.getString("cName"));
                            model.setTarget1(documentSnapshot.getString("target1"));
                            model.setTarget2(documentSnapshot.getString("target2"));
                            model.setStoploss(documentSnapshot.getString("stoploss"));
                            model.setCallType(documentSnapshot.getString("callType"));
                            model.setUid(documentSnapshot.getString("uid"));
                            model.setsDate(documentSnapshot.getString("sDate"));
                            model.seteDate(documentSnapshot.getString("eDate"));
                            model.setT1(documentSnapshot.getString("t1"));
                            model.setT2(documentSnapshot.getString("t2"));
                            model.setSl(documentSnapshot.getString("sl"));
                            model.setStatus(documentSnapshot.getString("status"));
                            list1.add(model);
                        }else {
                            findViewById(R.id.noCallCardView).setVisibility(View.VISIBLE);
                            ((TextView)findViewById(R.id.textview48)).setText(holidayText);
                        }

                    }
                    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                    layoutManager.setOrientation(RecyclerView.VERTICAL);
                    layoutManager.setReverseLayout(true);
                    layoutManager.setStackFromEnd(true);

                    adapter = new PositionalAdapter(list1);
                    today_recyclerView.setLayoutManager(layoutManager);
                    today_recyclerView.setHasFixedSize(true);
                    today_recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }else if (list1.size()==0){
                    findViewById(R.id.noCallCardView).setVisibility(View.VISIBLE);
                    ((TextView)findViewById(R.id.textview48)).setText(holidayText);
                }
            });
        }else {
            ((CardView)findViewById(R.id.cardview7)).setVisibility(View.VISIBLE);
            (findViewById(R.id.recycler_positional)).setVisibility(View.GONE);
        }



        previousPostionalRef.orderBy("sDate").addSnapshotListener((value, error) -> {
            if (!value.isEmpty()) {
                list2.clear();

                for (DocumentSnapshot documentSnapshot : value.getDocuments()) {

                    PositionalModel model = new PositionalModel();
                    model.setcName(documentSnapshot.getString("cName"));
                    model.setTarget1(documentSnapshot.getString("target1"));
                    model.setTarget2(documentSnapshot.getString("target2"));
                    model.setStoploss(documentSnapshot.getString("stoploss"));
                    model.setCallType(documentSnapshot.getString("callType"));
                    model.setUid(documentSnapshot.getString("uid"));
                    model.setsDate(documentSnapshot.getString("sDate"));
                    model.seteDate(documentSnapshot.getString("eDate"));
                    model.setT1(documentSnapshot.getString("t1"));
                    model.setT2(documentSnapshot.getString("t2"));
                    model.setSl(documentSnapshot.getString("sl"));
                    model.setStatus(documentSnapshot.getString("status"));
                    list2.add(model);
                }
                LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                layoutManager.setOrientation(RecyclerView.VERTICAL);
                layoutManager.setReverseLayout(true);
                layoutManager.setStackFromEnd(true);

                adapter = new PositionalAdapter(list2);
                previous_recyclerView.setLayoutManager(layoutManager);
                previous_recyclerView.setHasFixedSize(true);
                previous_recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}