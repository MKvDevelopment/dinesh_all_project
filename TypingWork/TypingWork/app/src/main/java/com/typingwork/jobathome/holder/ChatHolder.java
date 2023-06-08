package com.typingwork.jobathome.holder;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.typingwork.jobathome.R;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatHolder extends RecyclerView.ViewHolder
{
    private final String TAG = "CA/ChatHolder";

    private Activity activity;
    private View view;
    private Context context;

    // Will handle user data

    private DatabaseReference userDatabase;
    private ValueEventListener userListener;

    public ChatHolder(Activity activity, View view, Context context)
    {
        super(view);

        this.activity = activity;
        this.view = view;
        this.context = context;
    }

    public View getView()
    {
        return view;
    }


    public void setHolder(String userid, String message, long timestamp, long seen)
    {
        final TextView userName = view.findViewById(R.id.user_name);
        final TextView userStatus = view.findViewById(R.id.user_status);
        final TextView userTime = view.findViewById(R.id.user_timestamp);
        final CircleImageView userImage = view.findViewById(R.id.user_image);
        final ImageView userOnline = view.findViewById(R.id.user_online);

        userStatus.setText(message);

        userTime.setVisibility(View.VISIBLE);
        userTime.setText(new SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(timestamp));

        if(seen == 0L)
        {
            userStatus.setTypeface(null, Typeface.BOLD);
            userTime.setTypeface(null, Typeface.BOLD);
        }
        else
        {
            userStatus.setTypeface(null, Typeface.NORMAL);
            userTime.setTypeface(null, Typeface.NORMAL);
        }

        if(userDatabase != null && userListener != null)
        {
            userDatabase.removeEventListener(userListener);
        }

        // Initialize/Update user data

        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
        userListener = new ValueEventListener()
        {
            Timer timer; // Will be used to avoid flickering online status when changing activity

            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                try
                {
                    final String name = dataSnapshot.child("name").getValue().toString();
                    final String image = dataSnapshot.child("image").getValue().toString();

                    if(dataSnapshot.hasChild("online"))
                    {
                        String online = dataSnapshot.child("online").getValue().toString();

                        if(online.equals("true"))
                        {
                            if(timer != null)
                            {
                                timer.cancel();
                                timer = null;
                            }

                            userOnline.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            if(userName.getText().toString().equals(""))
                            {
                                userOnline.setVisibility(View.INVISIBLE);
                            }
                            else
                            {
                                timer = new Timer();
                                timer.schedule(new TimerTask()
                                {
                                    @Override
                                    public void run()
                                    {
                                        activity.runOnUiThread(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                userOnline.setVisibility(View.INVISIBLE);
                                            }
                                        });
                                    }
                                }, 2000);
                            }
                        }
                    }

                    userName.setText(name);
                }
                catch(Exception e)
                {
                    Log.d(TAG, "userListener exception: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Log.d(TAG, "userListener failed: " + databaseError.getMessage());
            }
        };
        userDatabase.addValueEventListener(userListener);
    }
}