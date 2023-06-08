package com.dinesh.adminwrokfromhome.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.dinesh.adminwrokfromhome.Adapters.AllPlanAdapter;
import com.dinesh.adminwrokfromhome.Models.AllPlanDataModel;
import com.dinesh.adminwrokfromhome.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class AllPlanRecyclerviewActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private CollectionReference reference;
    private AllPlanAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_plan);

        firestore = FirebaseFirestore.getInstance();
        reference=firestore.collection("Plan");

        setUpRecyclerView();
    }


    private void setUpRecyclerView() {

        Query query=reference;
        FirestoreRecyclerOptions<AllPlanDataModel> options = new FirestoreRecyclerOptions
                .Builder<AllPlanDataModel>()
                .setQuery(query, AllPlanDataModel.class)
                .build();

        adapter = new AllPlanAdapter(options);
        RecyclerView recyclerView = findViewById(R.id.update_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {

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
