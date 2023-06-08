package com.wheel.colorgame.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;
import com.wheel.colorgame.Model.SpinModel;
import com.wheel.colorgame.NetworkChangeReceiver;
import com.wheel.colorgame.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import rubikstudio.library.LuckyWheelView;
import rubikstudio.library.model.LuckyItem;

public class SpinActivity extends AppCompatActivity {

    private MediaPlayer spinSound;
    private MediaPlayer coinSound;
    private Button spinButton;
    private LuckyWheelView wheelView;
    private AlertDialog custom_dialog;
    private final NetworkChangeReceiver broadcastReceiver = new NetworkChangeReceiver();
    private String  depositbal, winBal, previousIndex,uid;
    private DocumentReference userSpinReference,userReference;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spin);

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        //broadcast receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, filter);
        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Spin to Earn");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("Please Wait!");
        progressDialog.setMessage("Fetching Data...");

        uid=Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        userReference = FirebaseFirestore.getInstance().collection("User_List") .document(uid);

        databaseReference= FirebaseDatabase.getInstance().getReference("Spin_User").child(uid);

        getDataFromFirebase();

        ((Button)findViewById(R.id.btn_trans_money)).setOnClickListener(v -> {
            if (Integer.parseInt(winBal)==0){
                Toast.makeText(getApplicationContext(), "Balance Not Sufficient", Toast.LENGTH_SHORT).show();
            }else if (Integer.parseInt(winBal)>=450){

              double total=  Double.parseDouble(depositbal)+Double.parseDouble(winBal);
              userReference.update("wallet",String.valueOf(total)).addOnSuccessListener(unused -> {
                  Map map=new HashMap();
                  map.put("winning_balence","0");
                  databaseReference.updateChildren(map);
                  //getDataFromFirebase();
                  Toast.makeText(getApplicationContext(), "Transfer Successfully", Toast.LENGTH_SHORT).show();
              }).addOnFailureListener(e -> {
                  Toast.makeText(getApplicationContext(), "Transfer Failed", Toast.LENGTH_SHORT).show();
              });

            }else{
                Toast.makeText(getApplicationContext(), "Minimum Balance Required 450 for Transfer!", Toast.LENGTH_LONG).show();
            }
        });

        wheelView = findViewById(R.id.wheel);
        spinSound = MediaPlayer.create(this, R.raw.spinsoundeffect);
        coinSound = MediaPlayer.create(this, R.raw.coin);

        int[] items = {200,350,470,75,180,500,230,25,550,250,420,125};
        int[] colors = {0xffff6698, 0xffffb366, 0xffffff66, 0xff98ff66, 0xff6698ff, 0xffff00ff};
        int[] targetIndex = {4,11,3,7,3};


        List<LuckyItem> data = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            LuckyItem luckyItem = new LuckyItem();
            luckyItem.topText = ""+ items[i];
            luckyItem.secondaryText="\uD83D\uDCB0";
            luckyItem.color = colors[i % 6];
            data.add(luckyItem);
        }
        wheelView.setData(data);
        wheelView.setRound(10);
        wheelView.setTouchEnabled(false);

        wheelView.setLuckyRoundItemSelectedListener(index -> {
            spinButton.setEnabled(true);
            spinSound.stop();

            progressDialog.setMessage("Updating Balance...");
            progressDialog.show();

            new Handler().postDelayed(() -> {
                progressDialog.dismiss();
                showWinningDialog(items[index],targetIndex);
            }, 3000);

        });

        spinButton = findViewById(R.id.spin_button);
        spinButton.setOnClickListener(v -> {

            Double bal = Double.parseDouble(depositbal);
            if (bal < 100) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SpinActivity.this, R.style.AppCompatAlertDialogStyle);
                alertDialogBuilder.setTitle("Add Money");
                alertDialogBuilder
                        .setMessage("Minimum Rs 100 required in Available Balance to spin.")
                        .setCancelable(false)
                        .setPositiveButton("Add Money", (dialog, id) ->
                                startActivity(new Intent(SpinActivity.this, PaymentActivity.class)))
                        .setNegativeButton("Cancel", (dialog, id) ->
                                dialog.cancel());

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            } else if ( Integer.parseInt(previousIndex)>=5){
                Toast.makeText(getApplicationContext(), "No Spin Available!", Toast.LENGTH_SHORT).show();
            }else {

                int indexx = Integer.parseInt(previousIndex);

                wheelView.startLuckyWheelWithTargetIndex(targetIndex[indexx]);

                spinButton.setEnabled(false);
                spinSound = MediaPlayer.create(SpinActivity.this, R.raw.spinsoundeffect);
                spinSound.start();
            }
        });
    }

    private void getDataFromFirebase() {

        progressDialog.show();

        userReference.addSnapshotListener((value, error) -> {
            depositbal=value.getString("wallet");
            ((TextView)findViewById(R.id.avl_bal)).setText("Available Balance = ₹ "+depositbal);

        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();

                SpinModel model=snapshot.getValue(SpinModel.class);

                previousIndex=model.getIndex();
                winBal=model.getWinning_balence();

                ((TextView)findViewById(R.id.win_bal)).setText("Winning Balance = ₹ "+winBal);

                if (Integer.parseInt(previousIndex)>=5){

                    ((TextView)findViewById(R.id.t1)).setVisibility(View.GONE);
                    ((TextView)findViewById(R.id.t2)).setVisibility(View.GONE);
                    ((TextView)findViewById(R.id.t3)).setVisibility(View.VISIBLE);
                    ((TextView)findViewById(R.id.t4)).setVisibility(View.VISIBLE);
                }else {
                    ((TextView)findViewById(R.id.t1)).setVisibility(View.VISIBLE);
                    ((TextView)findViewById(R.id.t2)).setVisibility(View.VISIBLE);
                    ((TextView)findViewById(R.id.t3)).setVisibility(View.GONE);
                    ((TextView)findViewById(R.id.t4)).setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showWinningDialog(int selectedItem, int[] targetIndex) {


        FancyGifDialog build = new FancyGifDialog.Builder(this)
                .setTitle("Congrats!!  \uD83E\uDD70, You win \u20B9" + selectedItem + ".")
                .setMessage("Amount will be added to your wallet.")
                .setGifResource(R.drawable.trophy)
                .OnPositiveClicked(() ->
                        updateUserBal(selectedItem,targetIndex))
                .setPositiveBtnText("Spin More")
                .isCancellable(false)
                .setNegativeBtnText("Cancel").OnNegativeClicked(() ->
                        updateUserBal(selectedItem,targetIndex)).build();

        coinSound.start();
    }

    private void updateUserBal(Object selectedItem, int[] targetIndex) {

        int indexx = Integer.parseInt(previousIndex);

        int i = ++indexx;

        double remain = Double.parseDouble(depositbal) - 100.0;
        long hh = Long.parseLong(winBal);
        int winbale = (int) (hh + Long.parseLong(String.valueOf(selectedItem)));

        Map map = new HashMap();
        map.put("winning_balence", String.valueOf(winbale));
        map.put("index", String.valueOf(i));

        userReference.update("wallet",String.valueOf(remain));

        databaseReference.updateChildren(map).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                progressDialog.dismiss();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}