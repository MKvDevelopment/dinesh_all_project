package com.document.security;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.an.biometric.BiometricCallback;
import com.an.biometric.BiometricManager;
import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.document.dbsecurity.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class PinActivity extends AppCompatActivity {
    public static final String TAG = "PinLockView";

    private PinLockView mPinLockView;
    private IndicatorDots mIndicatorDots;
    private   ProgressDialog  dialog;
   BiometricCallback biometricCallback= new BiometricCallback() {
       @Override
       public void onSdkVersionNotSupported() {
           /*
            *  Will be called if the device sdk version does not support Biometric authentication
            */
       }

       @Override
       public void onBiometricAuthenticationNotSupported() {
           /*
            *  Will be called if the device does not contain any fingerprint sensors
            */
       }

       @Override
       public void onBiometricAuthenticationNotAvailable() {
           /*
            *  The device does not have any biometrics registered in the device.
            */
       }

       @Override
       public void onBiometricAuthenticationPermissionNotGranted() {
           /*
            *  android.permission.USE_BIOMETRIC permission is not granted to the app
            */
       }

       @Override
       public void onBiometricAuthenticationInternalError(String error) {
           /*
            *  This method is called if one of the fields such as the title, subtitle,
            * description or the negative button text is empty
            */
       }

       @Override
       public void onAuthenticationFailed() {
           /*
            * When the fingerprint doesn’t match with any of the fingerprints registered on the device,
            * then this callback will be triggered.
            */
       }

       @Override
       public void onAuthenticationCancelled() {
           /*
            * The authentication is cancelled by the user.
            */
       }

       @Override
       public void onAuthenticationSuccessful() {
           /*
            * When the fingerprint is has been successfully matched with one of the fingerprints
            * registered on the device, then this callback will be triggered.
            */
           startActivity(new Intent(getApplicationContext(),MainActivity.class));
           finish();
       }

       @Override
       public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
           /*
            * This method is called when a non-fatal error has occurred during the authentication
            * process. The callback will be provided with an help code to identify the cause of the
            * error, along with a help message.
            */
       }

       @Override
       public void onAuthenticationError(int errorCode, CharSequence errString) {
           /*
            * When an unrecoverable error has been encountered and the authentication process has
            * completed without success, then this callback will be triggered. The callback is provided
            * with an error code to identify the cause of the error, along with the error message.
            */
       }
   };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_pin);
        mPinLockView = (PinLockView) findViewById(R.id.pin_lock_view);
        mIndicatorDots = (IndicatorDots) findViewById(R.id.indicator_dots);
        dialog = new ProgressDialog(PinActivity.this);
        dialog.setMessage("Please wait...");
        dialog.setCanceledOnTouchOutside(false);

        mPinLockView.attachIndicatorDots(mIndicatorDots);
        mPinLockView.setPinLockListener(mPinLockListener);

        //mPinLockView.setCustomKeySet(new int[]{2, 3, 1, 5, 9, 6, 7, 0, 8, 4});
        mPinLockView.enableLayoutShuffling();

        mPinLockView.setPinLength(4);
        mPinLockView.setTextColor(ContextCompat.getColor(this, R.color.white));

        mIndicatorDots.setIndicatorType(IndicatorDots.IndicatorType.FILL_WITH_ANIMATION);


        new BiometricManager.BiometricBuilder(PinActivity.this)
                .setTitle("Biometric Authentication")
                .setSubtitle("Please Biometric Authentication")
                .setDescription("If Don't want to go Biometric press Cancel ")
                .setNegativeButtonText("Cancel")
                .build()
                .authenticate(biometricCallback);
    }


    private PinLockListener mPinLockListener = new PinLockListener() {
        @Override
        public void onComplete(String pin) {
            dialog.show();
            FirebaseDatabase.getInstance().getReference().child("Admin").child("Admin_detail").child("password_key").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String key =snapshot.getValue().toString();
                    if(key.equals(pin)){
                        dialog.dismiss();
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        finish();
                    }else {
                        dialog.dismiss();
                        Toast.makeText(PinActivity.this, "Wrong Pin", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

           /* FirebaseFirestore.getInstance().collection("App_utlil").document("Admin_detail").addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    Toast.makeText(PinActivity.this, "Wrong Pin"+value, Toast.LENGTH_SHORT).show();

                 *//*   String key =value.getString("password_key");
                    if(key.equals(pin)){
                        dialog.dismiss();
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    }else {
                        dialog.dismiss();
                        Toast.makeText(PinActivity.this, "Wrong Pin", Toast.LENGTH_SHORT).show();
                    }*//*
                }
            });*/
        }

        @Override
        public void onEmpty() {
        }

        @Override
        public void onPinChange(int pinLength, String intermediatePin) {
         }
    };
}