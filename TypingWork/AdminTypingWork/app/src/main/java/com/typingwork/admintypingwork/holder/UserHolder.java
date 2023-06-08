package com.typingwork.admintypingwork.holder;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.typingwork.admintypingwork.R;

import java.util.Timer;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserHolder extends RecyclerView.ViewHolder {
    private final String TAG = "CA/UserHolder";

    private Activity activity;
    private View view;
    private Context context;

    // Will handle user data

    private DatabaseReference userDatabase;
    private ValueEventListener userListener;

    public UserHolder(Activity activity, View view, Context context) {
        super(view);

        this.activity = activity;
        this.view = view;
        this.context = context;
    }

    public View getView() {
        return view;
    }

    public void setHolder(String userid) {
        final TextView userName = view.findViewById(R.id.user_name);
        final TextView userStatus = view.findViewById(R.id.user_status);
        final TextView userTime = view.findViewById(R.id.user_timestamp);
        final CircleImageView userImage = view.findViewById(R.id.user_image);
        final ImageView userOnline = view.findViewById(R.id.user_online);
        final TextView msg_seen = view.findViewById(R.id.user_seen);

        if (userDatabase != null & userListener != null) {
            userDatabase.removeEventListener(userListener);
        }

        // Initialize/Upadte user data

        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
        userListener = new ValueEventListener() {
            Timer timer; // Will be used to avoid flickering online status when changing activity

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    final String name = dataSnapshot.child("name").getValue().toString();
                    final String status = dataSnapshot.child("status").getValue().toString();
                    final String image = dataSnapshot.child("img").getValue().toString();
                    final String last_msg = dataSnapshot.child("last_msg").getValue().toString();
                    final String date = dataSnapshot.child("date").getValue().toString();
                    final String seen = dataSnapshot.child("seen").getValue().toString();

                    userName.setText(name);
                    userStatus.setText(last_msg);

                    if (image.equals("")){
                        userImage.setImageResource(R.mipmap.ic_logo);
                    }else {
                        Picasso.get()
                                .load(image)
                                .resize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()))
                                .centerCrop()
                                .placeholder(R.drawable.ic_smileman)
                                .error(R.drawable.ic_smileman)
                                .into(userImage);

                    }

                    if (seen.equals("no")) {
                        msg_seen.setVisibility(View.VISIBLE);
                        msg_seen.setText("New");
                    }else {
                        msg_seen.setVisibility(View.INVISIBLE);
                    }
                    if (status.equals("online")) {
                        userTime.setVisibility(View.VISIBLE);
                        userTime.setText("Online");
                        userOnline.setVisibility(View.VISIBLE);
                    } else {
                        userOnline.setVisibility(View.GONE);
                        userTime.setVisibility(View.VISIBLE);
                        userTime.setText(getTimeAgo(Long.parseLong(status)));

                    }
                } catch (Exception e) {
                    Log.d(TAG, "userListener exception: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "userListener failed: " + databaseError.getMessage());
            }
        };
        userDatabase.addValueEventListener(userListener);
    }

    private String getTimeAgo(long time) {
        final long diff = System.currentTimeMillis() - time;

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
}