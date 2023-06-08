package com.perfect.traders.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.perfect.traders.Adatper.IntradayCallAdapter;
import com.perfect.traders.Constant.NetworkChangeReceiver;
import com.perfect.traders.Model.TodayCallModel;
import com.perfect.traders.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class IntradayCallActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private final ArrayList<TodayCallModel> list1 = new ArrayList<>();
    private final ArrayList<TodayCallModel> list2 = new ArrayList<>();
    private final ArrayList<TodayCallModel> list3 = new ArrayList<>();
    private RecyclerView todayRecyclerView, yesterdayRecycler, previousRecycler;
    private IntradayCallAdapter adapter;
    private DocumentReference utilsRef;
    private String today_holidayText,yesterday_holidaytext;
    private TextView tvTimeRemain, tvNotice;
    private NetworkChangeReceiver broadcastReceiver;
    private CollectionReference todayRef, yesterdayRef, allCallRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intraday_call);

     //   getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);


        //check network connectivity
        broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);


        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait!");
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        initviews();

        todayRef = FirebaseFirestore.getInstance().collection("Today_Call");
        yesterdayRef = FirebaseFirestore.getInstance().collection("Yesterday_Call");
        allCallRef = FirebaseFirestore.getInstance().collection("All_Previous_Call");
        utilsRef = FirebaseFirestore.getInstance().collection("App_Utils").document("utils");

        utilsRef.addSnapshotListener((value, error) -> {
            today_holidayText = value.getString("today_no_call_text");
            yesterday_holidaytext = value.getString("yesterday_no_call");
        });

        getAllData();

        checkPlanExpire();

        tvNotice.setOnClickListener(view -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(IntradayCallActivity.this);
            alertDialogBuilder.setTitle("Notice:-");
            alertDialogBuilder
                    .setMessage("As you already read our terms and condition we give different calls in different plans. So maybe a possible free plan call didn't match with your plan.")
                    .setCancelable(false)
                    .setPositiveButton("Ok",
                            (dialog, id) -> {
                                dialog.dismiss();
                            });
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
            Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
            nbutton.setTextColor(ContextCompat.getColor(this, R.color.primary));
            Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setTextColor(ContextCompat.getColor(this, R.color.primary));

        });

        findViewById(R.id.btn_subscribe).setOnClickListener(view -> {
            startActivity(new Intent(IntradayCallActivity.this, MembershipActivity.class));
            finish();
        });
    }

    private void checkPlanExpire() {

        Calendar calendarr = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
        String currentTime = simpleDateFormat2.format(calendarr.getTime());

        //  Toast.makeText(this, currentTime + " --" + usubEndDate, Toast.LENGTH_LONG).show();
        try {
            Date date1 = simpleDateFormat2.parse(currentTime);
            Date date2 = simpleDateFormat2.parse(MainActivity.SUBSCRIPTION_END);


            if (date1.compareTo(date2) < 0) {
                //  subEndDate is smaller then new date
                //  Toast.makeText(FullTimeActivity.this, " date1 is smaller than  date2", Toast.LENGTH_SHORT).show();
            } else if (date1.compareTo(date2) > 0) {
                String uid = FirebaseAuth.getInstance().getUid();
                DocumentReference documentReference = FirebaseFirestore.getInstance().collection("User_List").document(uid);
                documentReference.update("subscritpion", "NO");
            } else {
                Toast.makeText(this, "Your Plan will Expiring Tommorrow.", Toast.LENGTH_SHORT).show();
            }
        } catch (ParseException e) {
            e.printStackTrace();

        }
    }

    private void initviews() {
        todayRecyclerView = findViewById(R.id.recycler_intraday);
        yesterdayRecycler = findViewById(R.id.recycler_yesterday);
        previousRecycler = findViewById(R.id.recycler_previous);
        tvTimeRemain = findViewById(R.id.textView14);
        tvNotice = findViewById(R.id.textView15);
        tvTimeRemain.setText("Plan Expire On: " + MainActivity.SUBSCRIPTION_END);
    }

    private void getAllData() {
        if (MainActivity.SUBSCRIPTION.equals("NO")) {
            findViewById(R.id.recycler_intraday).setVisibility(View.GONE);
            findViewById(R.id.cardView).setVisibility(View.VISIBLE);
            tvTimeRemain.setVisibility(View.GONE);
            tvNotice.setVisibility(View.GONE);
            findViewById(R.id.textView10).setVisibility(View.VISIBLE);
            yesterdayRef.orderBy("time").addSnapshotListener((value, error) -> {
                if (!value.isEmpty()) {
                    list2.clear();
                    findViewById(R.id.cardview8).setVisibility(View.GONE);
                    findViewById(R.id.recycler_yesterday).setVisibility(View.VISIBLE);
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd/MM/yy");
                    String subStartDate = simpleDateFormat1.format(calendar.getTime());
                    String subEndDate = null;
                    try {
                        Date date = simpleDateFormat1.parse(subStartDate);
                        assert date != null;
                        calendar.setTime(date);
                        calendar.add(Calendar.DATE, -1);
                        subEndDate = simpleDateFormat1.format(calendar.getTime());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    for (DocumentSnapshot documentSnapshot : value.getDocuments()) {

                        Date date = new Date(Long.parseLong(documentSnapshot.getString("time")));
                        DateFormat f1 = new SimpleDateFormat("dd/MM/yy");

                        TodayCallModel model = new TodayCallModel();

                        if (f1.format(date).equals(subEndDate)) {
                           // Toast.makeText(getApplicationContext(), "sss", Toast.LENGTH_SHORT).show();
                            findViewById(R.id.cardview8).setVisibility(View.GONE);
                            model.setC_name(documentSnapshot.getString("c_name"));
                            model.setTarget1(documentSnapshot.getString("target1"));
                            model.setTarget2(documentSnapshot.getString("target2"));
                            model.setStop_loss(documentSnapshot.getString("stop_loss"));
                            model.setCall_type(documentSnapshot.getString("call_type"));
                            model.setUid(documentSnapshot.getString("uid"));
                            model.setTime(documentSnapshot.getString("time"));
                            model.setT1(documentSnapshot.getString("t1"));
                            model.setT2(documentSnapshot.getString("t2"));
                            model.setSl(documentSnapshot.getString("sl"));
                            list2.add(model);
                        }
                    }
                    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                    layoutManager.setOrientation(RecyclerView.VERTICAL);
                    layoutManager.setReverseLayout(true);
                    layoutManager.setStackFromEnd(true);

                    adapter = new IntradayCallAdapter(list2);
                    yesterdayRecycler.setLayoutManager(layoutManager);
                    yesterdayRecycler.setHasFixedSize(true);
                    yesterdayRecycler.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }else if (list2.size()==0){
                    findViewById(R.id.cardview8).setVisibility(View.VISIBLE);
                    findViewById(R.id.recycler_yesterday).setVisibility(View.GONE);
                    ((TextView) findViewById(R.id.textview49)).setText(yesterday_holidaytext);
                }
            });
            allCallRef.orderBy("time").addSnapshotListener((value, error) -> {

                if (!value.isEmpty()) {
                    list3.clear();

                    for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                        TodayCallModel model = new TodayCallModel();
                        model.setC_name(documentSnapshot.getString("c_name"));
                        model.setTarget1(documentSnapshot.getString("target1"));
                        model.setTarget2(documentSnapshot.getString("target2"));
                        model.setStop_loss(documentSnapshot.getString("stop_loss"));
                        model.setCall_type(documentSnapshot.getString("call_type"));
                        model.setUid(documentSnapshot.getString("uid"));
                        model.setTime(documentSnapshot.getString("time"));
                        model.setT1(documentSnapshot.getString("t1"));
                        model.setT2(documentSnapshot.getString("t2"));
                        model.setSl(documentSnapshot.getString("sl"));
                        list3.add(model);

                    }
                    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                    layoutManager.setOrientation(RecyclerView.VERTICAL);
                    layoutManager.setReverseLayout(true);
                    layoutManager.setStackFromEnd(true);

                    adapter = new IntradayCallAdapter(list3);
                    previousRecycler.setLayoutManager(layoutManager);
                    previousRecycler.setHasFixedSize(true);
                    previousRecycler.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            });
        } else {

            findViewById(R.id.textView11).setVisibility(View.GONE);
            findViewById(R.id.recycler_yesterday).setVisibility(View.GONE);
            findViewById(R.id.textView12).setVisibility(View.GONE);
            findViewById(R.id.recycler_previous).setVisibility(View.GONE);
            findViewById(R.id.cardView).setVisibility(View.GONE);
            tvTimeRemain.setVisibility(View.VISIBLE);
            tvNotice.setVisibility(View.VISIBLE);

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd/MM/yy");
            String today_date = simpleDateFormat1.format(calendar.getTime());

            todayRef.orderBy("time").addSnapshotListener((value, error) -> {
                if (!value.isEmpty()) {
                    list1.clear();
                    for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                        TodayCallModel model = new TodayCallModel();
                        Date date = new Date(Long.parseLong(documentSnapshot.getString("time")));
                        DateFormat f1 = new SimpleDateFormat("dd/MM/yy");

                        if (today_date.equals(f1.format(date))) {
                            model.setC_name(documentSnapshot.getString("c_name"));
                            model.setTarget1(documentSnapshot.getString("target1"));
                            model.setTarget2(documentSnapshot.getString("target2"));
                            model.setStop_loss(documentSnapshot.getString("stop_loss"));
                            model.setCall_type(documentSnapshot.getString("call_type"));
                            model.setUid(documentSnapshot.getString("uid"));
                            model.setTime(documentSnapshot.getString("time"));
                            model.setT1(documentSnapshot.getString("t1"));
                            model.setT2(documentSnapshot.getString("t2"));
                            model.setSl(documentSnapshot.getString("sl"));
                            list1.add(model);
                        } else {
                            findViewById(R.id.tv_text).setVisibility(View.VISIBLE);
                            findViewById(R.id.imageView9).setVisibility(View.VISIBLE);
                            ((TextView) findViewById(R.id.tv_text)).setText(today_holidayText);
                            Toast.makeText(this, "NO call", Toast.LENGTH_SHORT).show();
                        }


                    }
                    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                    layoutManager.setOrientation(RecyclerView.VERTICAL);
                    layoutManager.setReverseLayout(true);
                    layoutManager.setStackFromEnd(true);

                    adapter = new IntradayCallAdapter(list1);
                    todayRecyclerView.setLayoutManager(layoutManager);
                    todayRecyclerView.setHasFixedSize(true);
                    todayRecyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            });

        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}