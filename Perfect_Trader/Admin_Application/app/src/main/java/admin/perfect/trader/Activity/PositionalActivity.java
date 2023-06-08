package admin.perfect.trader.Activity;

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

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import admin.perfect.trader.Adatper.PositionalAdapter;
import admin.perfect.trader.Model.PositionalModel;
import admin.perfect.trader.R;

public class PositionalActivity extends AppCompatActivity {

    private PositionalAdapter adapter;
    private CollectionReference positionalRef, previousPostionalRef;
    private final ArrayList<PositionalModel> list1 = new ArrayList<>();
    private final ArrayList<PositionalModel> list2 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_positional);

        Toolbar toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        positionalRef = FirebaseFirestore.getInstance().collection("Today_Positional_Call");
        previousPostionalRef = FirebaseFirestore.getInstance().collection("Previous_Positional_Call");
        getAllData();

        findViewById(R.id.appCompatButton4).setOnClickListener(view -> showAddingDialogBox("Today_Positional_Call"));

        findViewById(R.id.button).setOnClickListener(view -> showAddingDialogBox("Previous_Positional_Call"));


    }

    private void showAddingDialogBox(String field) {

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
                addCallData("buy", name, t1, t2, sl, field);
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
                addCallData("sell", name, t1, t2, sl, field);
            }
        });
        view1.findViewById(R.id.imageView8).setOnClickListener(view2 -> {
            custome_dialog.dismiss();
        });

    }

    private void addCallData(String type, String name, String t1, String t2, String sl, String field) {

        final String uuid = UUID.randomUUID().toString().replace("-", "");

        Map map = new HashMap<>();
        map.put("cName", name);
        map.put("target1", t1);
        map.put("target2", t2);
        map.put("stoploss", sl);
        map.put("callType", type);
        map.put("uid", uuid);
        map.put("t1", "");
        map.put("t2", "");
        map.put("sl", "");
        map.put("sDate", Timestamp.now().getSeconds() + "000");
        map.put("eDate", Timestamp.now().getSeconds() + "000");
        map.put("status", "In Progress");

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection(field).document(uuid);
        documentReference.set(map).addOnSuccessListener(unused -> {
            Toast.makeText(this, "Uploaded", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });


    }

    private void getAllData() {


        RecyclerView recyclerView1 = findViewById(R.id.recycler_positional);
        RecyclerView recyclerView2 = findViewById(R.id.recycler_previous_positional);

        positionalRef.orderBy("sDate").addSnapshotListener((value, error) -> {

            if (!value.isEmpty()) {
                list1.clear();
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
                    list1.add(model);

                }
                LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                layoutManager.setOrientation(RecyclerView.VERTICAL);
                layoutManager.setReverseLayout(true);
                layoutManager.setStackFromEnd(true);

                adapter = new PositionalAdapter(list1, "1");
                recyclerView1.setLayoutManager(layoutManager);
                recyclerView1.setHasFixedSize(true);
                recyclerView1.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

        });

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

                adapter = new PositionalAdapter(list2, "2");
                recyclerView2.setLayoutManager(layoutManager);
                recyclerView2.setHasFixedSize(true);
                recyclerView2.setAdapter(adapter);
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