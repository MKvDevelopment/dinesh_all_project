package com.dinesh.adminwrokfromhome.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dinesh.adminwrokfromhome.Adapters.FullTimeRecyclerAdapter;
import com.dinesh.adminwrokfromhome.Models.Product_item_model;
import com.dinesh.adminwrokfromhome.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

public class FullTimeRecyclerView extends AppCompatActivity implements Filterable {

    private String[] listItem;
    private RecyclerView recyclerView;
    private FullTimeRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_time_recycler_view);

        recyclerView = findViewById(R.id.product_lst_view);

        setUpRecyclerView();

    }


    private void setUpRecyclerView() {

        Query query = FirebaseFirestore.getInstance().collection("full_time_user");
        FirestoreRecyclerOptions<Product_item_model> options = new FirestoreRecyclerOptions
                .Builder<Product_item_model>()
                .setQuery(query, Product_item_model.class)
                .build();

        adapter = new FullTimeRecyclerAdapter(options);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListner((documentSnapshot, position) -> {
            final Product_item_model model = documentSnapshot.toObject(Product_item_model.class);


            listItem = new String[]{"Edit", "Delete"};
            AlertDialog.Builder builder = new AlertDialog.Builder(FullTimeRecyclerView.this);
            builder.setTitle("Choose any One!");
            builder.setSingleChoiceItems(listItem, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    switch (listItem[i]) {

                        case "Edit":
                            Intent intent1 = new Intent(FullTimeRecyclerView.this, EditFullTimeUserActivity.class);
                            intent1.putExtra("email", model.getEmail());
                            intent1.putExtra("end", model.getPlan_end_date());
                            intent1.putExtra("start", model.getPlan_start_date());
                            intent1.putExtra("status", model.getStatus());
                            intent1.putExtra("reason", model.getReason());
                            intent1.putExtra("post", model.getView_post());
                            intent1.putExtra("wallet", model.getWallet());
                            startActivity(intent1);
                            dialog.dismiss();
                            break;

                        case "Delete":
                            documentSnapshot.getReference().delete();
                            Toast.makeText(FullTimeRecyclerView.this, "deleted", Toast.LENGTH_SHORT).show();
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

    @Override
    public Filter getFilter() {
        return null;
    }
}