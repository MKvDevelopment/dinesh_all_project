package com.typingwork.admintypingwork.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.messaging.FirebaseMessaging;
import com.typingwork.admintypingwork.Model.User;
import com.typingwork.admintypingwork.R;
import com.typingwork.admintypingwork.holder.UserHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private FirebaseRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private String currentUserId;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = findViewById(R.id.toolbar6);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setTitle("Please wait!");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
        // RecyclerView related

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView = findViewById(R.id.recyclerView1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(mDividerItemDecoration);

        // Initializing Users database

        DatabaseReference usersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        userRef = FirebaseDatabase.getInstance().getReference().child("admin");
        // usersDatabase.keepSynced(true); // For offline use

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(usersDatabase.orderByChild("date"), User.class).build();
        checkCurrentUserToken();
        adapter = new FirebaseRecyclerAdapter<User, UserHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final UserHolder holder, int position, User model) {
                final String userid = getRef(position).getKey();
                progressDialog.dismiss();
                holder.setHolder(userid);
                holder.getView().setOnClickListener(view -> {
                    //Toast.makeText(view.getContext(), userid, Toast.LENGTH_SHORT).show();
                    Intent userProfileIntent = new Intent(ChatActivity.this, ChatDetailActivity.class);
                    userProfileIntent.putExtra("userid", userid);
                    userProfileIntent.putExtra("token", model.getToken());
                    startActivity(userProfileIntent);
                });
                holder.getView().setOnLongClickListener(v -> {

                    AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                    builder.setTitle("Delete User!");
                    builder.setMessage("Are you want to delete this user?");
                    builder.setPositiveButton("Yes", (dialog, id) -> {
                        dialog.dismiss();
                        DatabaseReference databaseReference = usersDatabase.child(userid);
                        databaseReference.removeValue();
                        Toast.makeText(ChatActivity.this, "Customer Deleted", Toast.LENGTH_SHORT).show();

                    });
                    builder.setNegativeButton("No", (dialog, id) ->
                            dialog.dismiss());
                    AlertDialog dialog = builder.create();
                    dialog.show();

                    return false;
                });
            }

            @Override
            public UserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user, parent, false);

                return new UserHolder(ChatActivity.this, view, getApplicationContext());
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void checkCurrentUserToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
            Map map=new HashMap();
            map.put("token",token);
            userRef.updateChildren(map);
        }).addOnFailureListener(e -> {
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Map<String,Object> map = new HashMap<>();
        map.put("online", "true");
        FirebaseDatabase.getInstance().getReference().child("admin").updateChildren(map);
    }
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Map<String,Object> map = new HashMap<>();
        map.put("online", ServerValue.TIMESTAMP);
        FirebaseDatabase.getInstance().getReference().child("admin").updateChildren(map);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


}