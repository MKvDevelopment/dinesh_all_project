package admin.perfect.trader.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import admin.perfect.trader.Adatper.IntradayCallAdapter;
import admin.perfect.trader.Model.TodayCallModel;
import admin.perfect.trader.R;

public class IntradayCallActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private final ArrayList<TodayCallModel> list1 = new ArrayList<>();
    private final ArrayList<TodayCallModel> list2 = new ArrayList<>();
    private final ArrayList<TodayCallModel> list3 = new ArrayList<>();
    private RecyclerView todayRecyclerView, yesterdayRecycler, previousRecycler;
    private IntradayCallAdapter adapter;
    private CollectionReference todayRef, yesterdayRef, allCallRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intraday_call);

        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait!");
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        initviews();

        todayRef = FirebaseFirestore.getInstance().collection("Today_Call");
        yesterdayRef = FirebaseFirestore.getInstance().collection("Yesterday_Call");
        allCallRef = FirebaseFirestore.getInstance().collection("All_Previous_Call");

        getAllData();

        findViewById(R.id.floatingActionButton3).setOnClickListener(view -> showAddingDialogBox(todayRef));
        findViewById(R.id.btn_yesAdd).setOnClickListener(view -> showAddingDialogBox(yesterdayRef));
        findViewById(R.id.btn_alladd).setOnClickListener(view -> showAddingDialogBox(allCallRef));


    }

    private void showAddingDialogBox(CollectionReference type) {

        AlertDialog custome_dialog;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view1 = LayoutInflater.from(this).inflate(R.layout.intraday_add_data_item, null, false);

        Button btnBuy = view1.findViewById(R.id.btn_buy);
        Button btnSell = view1.findViewById(R.id.btn_sell);
        EditText edName = view1.findViewById(R.id.ed_company_name);
        EditText edT1 = view1.findViewById(R.id.ed_target1);
        EditText edT2 = view1.findViewById(R.id.ed_target2);
        EditText edSl = view1.findViewById(R.id.ed_sl);

        builder.setView(view1);

        custome_dialog = builder.create();
        custome_dialog.setCanceledOnTouchOutside(false);
        custome_dialog.show();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd/MM/yy");
        String subStartDate = simpleDateFormat1.format(calendar.getTime());

        btnBuy.setOnClickListener(v -> {

            String name = edName.getText().toString().trim();
            String t1 = edT1.getText().toString();
            String t2 = edT2.getText().toString();
            String sl = edSl.getText().toString();

            if (name.isEmpty() || t1.isEmpty() || t2.isEmpty() || sl.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Field cann't be Empty!", Toast.LENGTH_SHORT).show();
            } else {
                custome_dialog.dismiss();
                addCallData("buy", name, t1, t2, sl,type);
            }
        });

        btnSell.setOnClickListener(v -> {

            String name = edName.getText().toString().trim();
            String t1 = edT1.getText().toString();
            String t2 = edT2.getText().toString();
            String sl = edSl.getText().toString();

            if (name.isEmpty() || t1.isEmpty() || t2.isEmpty() || sl.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Field cann't be Empty!", Toast.LENGTH_SHORT).show();
            } else {
                custome_dialog.dismiss();
                addCallData("sell", name, t1, t2, sl,type);
            }
        });

        view1.findViewById(R.id.imageView8).setOnClickListener(view2 -> {
            custome_dialog.dismiss();
        });
    }

    private void initviews() {
        todayRecyclerView = findViewById(R.id.recycler_intraday);
        yesterdayRecycler = findViewById(R.id.recycler_yesterday);
        previousRecycler = findViewById(R.id.recycler_previous);
    }

    private void addCallData(String type, String name, String t1, String t2, String sl, CollectionReference ref) {
        progressDialog.setMessage("Uploading New Call");
        progressDialog.show();
        final String uuid = UUID.randomUUID().toString().replace("-", "");

        Map map = new HashMap<>();
        map.put("c_name", name);
        map.put("target1", t1);
        map.put("target2", t2);
        map.put("stop_loss", sl);
        map.put("call_type", type);
        map.put("uid", uuid);
        map.put("t1", "");
        map.put("t2", "");
        map.put("sl", "");
        map.put("time", Timestamp.now().getSeconds() + "000");

        ref.document(uuid).set(map).addOnSuccessListener(unused -> {
            progressDialog.dismiss();
            Toast.makeText(this, "Uploaded", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });


    }

    private void getAllData() {

        todayRef.orderBy("time").addSnapshotListener((value, error) -> {

            if (!value.isEmpty()) {
                list1.clear();
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
                    list1.add(model);

                }
                LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                layoutManager.setOrientation(RecyclerView.VERTICAL);
                layoutManager.setReverseLayout(true);
                layoutManager.setStackFromEnd(true);

                adapter = new IntradayCallAdapter(list1);
                todayRecyclerView.setLayoutManager(layoutManager);
                todayRecyclerView.setHasFixedSize(true);
                todayRecyclerView.setAdapter(adapter);
                adapter.setListner(position -> {
                    Intent intent=new Intent(IntradayCallActivity.this,IntradayDetailActivity.class);
                    intent.putExtra("key","Today_Call");
                    intent.putExtra("type","today");
                    intent.putExtra("id",list1.get(position).getUid());
                    startActivity(intent);
                });
                adapter.notifyDataSetChanged();
            }

        });

        yesterdayRef.orderBy("time").addSnapshotListener((value, error) -> {

            if (!value.isEmpty()) {
                list2.clear();

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
                    list2.add(model);

                }
                LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                layoutManager.setOrientation(RecyclerView.VERTICAL);
                layoutManager.setReverseLayout(true);
                layoutManager.setStackFromEnd(true);

                adapter = new IntradayCallAdapter(list2);
                yesterdayRecycler.setLayoutManager(layoutManager);
                yesterdayRecycler.setHasFixedSize(true);
                yesterdayRecycler.setAdapter(adapter);
                adapter.setListner(position -> {
                    Intent intent=new Intent(IntradayCallActivity.this,IntradayDetailActivity.class);
                    intent.putExtra("key","Yesterday_Call");
                    intent.putExtra("type","yes");
                    intent.putExtra("id",list2.get(position).getUid());
                    startActivity(intent);
                });
                adapter.notifyDataSetChanged();

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
                adapter.setListner(position -> {
                    Intent intent=new Intent(IntradayCallActivity.this,IntradayDetailActivity.class);
                    intent.putExtra("key","All_Previous_Call");
                    intent.putExtra("type","all");
                    intent.putExtra("id",list3.get(position).getUid());
                    startActivity(intent);
                });
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