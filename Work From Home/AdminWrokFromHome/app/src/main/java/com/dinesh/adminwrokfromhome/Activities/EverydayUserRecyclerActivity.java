package com.dinesh.adminwrokfromhome.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Filter;
import android.widget.Toast;

import com.dinesh.adminwrokfromhome.Adapters.EveryDayRecyclerAdapter;
import com.dinesh.adminwrokfromhome.Adapters.FullTimeRecyclerAdapter;
import com.dinesh.adminwrokfromhome.Models.EveryDayModel;
import com.dinesh.adminwrokfromhome.Models.Product_item_model;
import com.dinesh.adminwrokfromhome.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class EverydayUserRecyclerActivity extends AppCompatActivity {

    private String[] listItem;
    private RecyclerView recyclerView;
    private EveryDayRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_everyday_user_recycler);

        recyclerView = findViewById(R.id.everyday_lst_view);

        setUpRecyclerView();

    }

    private void setUpRecyclerView() {

        Query query = FirebaseFirestore.getInstance().collection("everyday_users");
        FirestoreRecyclerOptions<EveryDayModel> options = new FirestoreRecyclerOptions
                .Builder<EveryDayModel>()
                .setQuery(query, EveryDayModel.class)
                .build();

        adapter = new EveryDayRecyclerAdapter(options);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListner(new EveryDayRecyclerAdapter.OnItemClickListner() {
            @Override
            public void onItemClick(final DocumentSnapshot documentSnapshot, int position) {
                final EveryDayModel model = documentSnapshot.toObject(EveryDayModel.class);


                listItem = new String[]{"Edit", "Delete"};
                AlertDialog.Builder builder = new AlertDialog.Builder(EverydayUserRecyclerActivity.this);
                builder.setTitle("Choose any One!");
                builder.setSingleChoiceItems(listItem, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        switch (listItem[i]) {

                            case "Edit":
                                Intent intent1 = new Intent(EverydayUserRecyclerActivity.this, EditEveryDayUserDetailActivity.class);
                                intent1.putExtra("email", model.getEmail());
                                intent1.putExtra("start", model.getPlan_start_date());
                                intent1.putExtra("post", model.getView_post());
                                intent1.putExtra("wallet", model.getWallet());
                                intent1.putExtra("status", model.getStatus());
                                intent1.putExtra("reason", model.getReason());
                                startActivity(intent1);
                                dialog.dismiss();
                                break;

                            case "Delete":
                                documentSnapshot.getReference().delete();
                                Toast.makeText(EverydayUserRecyclerActivity.this, "deleted", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                break;

                        }
                    }
                }).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });
        adapter.notifyDataSetChanged();

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}