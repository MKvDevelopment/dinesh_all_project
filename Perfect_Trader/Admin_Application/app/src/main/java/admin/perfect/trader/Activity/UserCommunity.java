package admin.perfect.trader.Activity;

import android.app.ProgressDialog;
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
import admin.perfect.trader.Adatper.CommunityAdapter;
import admin.perfect.trader.Model.CommunityModel;
import admin.perfect.trader.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserCommunity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private final ArrayList<CommunityModel> list = new ArrayList<>();
    private RecyclerView recyclerView;
    private CommunityAdapter adapter;
    private CollectionReference communityRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_community);

        Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait!");
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        recyclerView = findViewById(R.id.recycler_user_com);

        communityRef = FirebaseFirestore.getInstance().collection("User_Community");

        getAllData();

        findViewById(R.id.floatingActionButton2).setOnClickListener(view -> {
            AlertDialog custome_dialog;

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view1 = LayoutInflater.from(this).inflate(R.layout.community_enter_data_layout, null, false);

            Button submit = view1.findViewById(R.id.appCompatButton2);
            Button cancel = view1.findViewById(R.id.appCompatButton);
            EditText ed_title = view1.findViewById(R.id.editText);
            EditText ed_des = view1.findViewById(R.id.editText2);
            builder.setView(view1);

            custome_dialog = builder.create();
            custome_dialog.setCanceledOnTouchOutside(false);
            custome_dialog.show();

            submit.setOnClickListener(v -> {
                String title = ed_title.getText().toString().trim();
                String desc = ed_des.getText().toString();

                final String uuid = UUID.randomUUID().toString().replace("-", "");


                if (title.isEmpty() || desc.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Field cann't be Empty!", Toast.LENGTH_SHORT).show();
                } else {
                    custome_dialog.dismiss();
                   Map map=new HashMap<>();
                   map.put("title",title);
                   map.put("des",desc);
                   map.put("uid",uuid);
                   map.put("time", Timestamp.now().getSeconds()+"000");
                   map.put("img",MainActivity.USER_IMAGE);
                   communityRef.document(uuid).set(map);
                }
            });
            cancel.setOnClickListener(v -> {
                custome_dialog.dismiss();
            });
        });

    }

    private void getAllData() {

        communityRef.orderBy("time").addSnapshotListener((value, error) -> {
            if (!value.isEmpty()) {
                list.clear();

                for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                    CommunityModel model = new CommunityModel();
                    model.setTitle(documentSnapshot.getString("title"));
                    model.setDes(documentSnapshot.getString("des"));
                    model.setImg(documentSnapshot.getString("img"));
                    model.setUid(documentSnapshot.getString("uid"));
                    model.setTime(documentSnapshot.getString("time"));
                    list.add(model);

                }
                LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                layoutManager.setOrientation(RecyclerView.VERTICAL);
                layoutManager.setReverseLayout(true);
                layoutManager.setStackFromEnd(true);

                adapter = new CommunityAdapter(list);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            } else {
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}