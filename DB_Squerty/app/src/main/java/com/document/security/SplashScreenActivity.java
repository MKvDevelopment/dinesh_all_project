package com.document.security;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.document.dbsecurity.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        ImageView logoIv =(ImageView)findViewById(R.id.logoIv); // Declare an imageView to show the animation.
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_bottom); // Create the animation.
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {

                if(FirebaseAuth.getInstance().getCurrentUser()==null){
                    finish();
                    startActivity(new Intent(getApplicationContext(),AuthActivity.class));
                }else {
                    FirebaseDatabase.getInstance().getReference("Approved_users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int i=0;
                            for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                                if(FirebaseAuth.getInstance().getCurrentUser().getEmail().equals(dataSnapshot.getValue().toString())){
                                    ++i;
                                }
                            }

                            if(i>0){
                                startActivity(new Intent(SplashScreenActivity.this, PinActivity.class));
                                // HomeActivity.class is the activity to go after showing the splash screen.
                                finish();
                            }else {
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(getApplicationContext(),AuthActivity.class));
                                finish();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }



            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        logoIv.startAnimation(anim);
    }

}