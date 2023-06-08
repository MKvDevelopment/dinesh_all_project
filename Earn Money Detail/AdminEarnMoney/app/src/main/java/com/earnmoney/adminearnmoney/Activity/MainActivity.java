package com.earnmoney.adminearnmoney.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.earnmoney.adminearnmoney.Model.User;
import com.earnmoney.adminearnmoney.R;
import com.earnmoney.adminearnmoney.holders.UserHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private FirebaseRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private String currentUserId;
    //  public static  DatabaseReference admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(mDividerItemDecoration);

        // Initializing Users database

        DatabaseReference usersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        usersDatabase.keepSynced(true); // For offline use

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(usersDatabase.orderByChild("date"), User.class).build();

        adapter = new FirebaseRecyclerAdapter<User, UserHolder>(options) {
            @Override
            protected void onBindViewHolder(final UserHolder holder, int position, User model) {
                final String userid = getRef(position).getKey();
                progressDialog.dismiss();
                holder.setHolder(userid);
                holder.getView().setOnClickListener(view -> {
                    Intent userProfileIntent = new Intent(MainActivity.this, ChatActivity.class);
                    userProfileIntent.putExtra("userid", userid);
                    userProfileIntent.putExtra("token", model.getToken());
                    startActivity(userProfileIntent);
                });
                holder.getView().setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Delete User!");
                        builder.setMessage("Are you want to delete this user?");
                        builder.setPositiveButton("Yes", (dialog, id) -> {
                            dialog.dismiss();
                            DatabaseReference databaseReference = usersDatabase.child(userid);
                            databaseReference.removeValue();
                            Toast.makeText(MainActivity.this, "Customer Deleted", Toast.LENGTH_SHORT).show();

                        });
                        builder.setNegativeButton("No", (dialog, id) ->
                                dialog.dismiss());
                        AlertDialog dialog = builder.create();
                        dialog.show();

                        return false;
                    }
                });
            }

            @Override
            public UserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user, parent, false);

                return new UserHolder(MainActivity.this, view, getApplicationContext());
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }


    public static String getTimeAgo(String time) {
        long timee = Long.getLong(time);
        final long diff = System.currentTimeMillis() - timee;

        if (diff < 1) {
            return " just now";
        }
        if (diff < 60 * 1000) {
            if (diff / 1000 < 2) {
                return diff / 1000 + " second ago";
            } else {
                return diff / 1000 + " seconds ago";
            }
        } else if (diff < 60 * (60 * 1000)) {
            if (diff / (60 * 1000) < 2) {
                return diff / (60 * 1000) + " minute ago";
            } else {
                return diff / (60 * 1000) + " minutes ago";
            }
        } else if (diff < 24 * (60 * (60 * 1000))) {
            if (diff / (60 * (60 * 1000)) < 2) {
                return diff / (60 * (60 * 1000)) + " hour ago";
            } else {
                return diff / (60 * (60 * 1000)) + " hours ago";
            }
        } else {
            if (diff / (24 * (60 * (60 * 1000))) < 2) {
                return diff / (24 * (60 * (60 * 1000))) + " day ago";
            } else {
                return diff / (24 * (60 * (60 * 1000))) + " days ago";
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Map map = new HashMap();
        map.put("online", "true");
        FirebaseDatabase.getInstance().getReference().child("admin").updateChildren(map);
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Map map = new HashMap();
        map.put("online", ServerValue.TIMESTAMP);
        FirebaseDatabase.getInstance().getReference().child("admin").updateChildren(map);
    }
}