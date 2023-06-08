package com.wheel.admin;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private DocumentReference totalRef, betAmountRef, betResultRef, adminAmountRef;
    private Calendar calendar;
    private String withdrawTime;
    private Button btn_app_on, btn_app_Off;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_app_Off = (Button) findViewById(R.id.btnappOff);
        btn_app_on = findViewById(R.id.btnappOn);

        calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd-MM-yy");
        withdrawTime = simpleDateFormat1.format(calendar.getTime());

        adminAmountRef = FirebaseFirestore.getInstance().collection("Today_Income").document(withdrawTime);

        betAmountRef = FirebaseFirestore.getInstance().collection("TotalBetAmount").document("color");
        betResultRef = FirebaseFirestore.getInstance().collection("TotalBetAmount").document("result");
        totalRef = FirebaseFirestore.getInstance().collection("App_Utils").document("totalAmount");


        betAmountRef.addSnapshotListener((value, error) -> {
            ((TextView) findViewById(R.id.textView3)).setText("Blue:-" + value.getString("blue"));
            ((TextView) findViewById(R.id.textView5)).setText("Pink:-" + value.getString("pink"));
            ((TextView) findViewById(R.id.textView4)).setText("Red:-" + value.getString("red"));
            ((TextView) findViewById(R.id.textView2)).setText("Yelow:-" + value.getString("yellow"));
        });

        betResultRef.addSnapshotListener((value, error) -> {
            ((TextView) findViewById(R.id.textView7)).setText("C Rslt:-" + value.getString("current"));
            ((TextView) findViewById(R.id.textView6)).setText("Pre Rslt:-" + value.getString("previous"));
        });

        totalRef.addSnapshotListener((value, error) -> {
            ((TextView) findViewById(R.id.textView9)).setText("T Income:-" + value.getString("totalIncome"));
            ((TextView) findViewById(R.id.textView8)).setText("T Payout:-" + value.getString("totalPayout"));
        });

        adminAmountRef.addSnapshotListener((value, error) -> {
            ((TextView) findViewById(R.id.textview10)).setText("Td Incm:-" + value.getString("totalIncome"));
            ((TextView) findViewById(R.id.textview11)).setText("Td Pout:-" + value.getString("totalPayout"));
        });

        ((Button) findViewById(R.id.timerBtn)).setOnClickListener(view -> {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setPositiveButton("Yes", (dialog, which) -> {
                dialog.dismiss();
                startActivity(new Intent(MainActivity.this, AlwanysOpenActivity.class));
            }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

            Utils.showAlertdialog(MainActivity.this, alertDialog, "Are you sure! You want to Enter Next Page?", "Click yes to Confirm!");

        });

        ((Button) findViewById(R.id.btn_withdraw)).setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, WithdrawActivity.class));
          //  finish();
        });

        btn_app_Off.setOnClickListener(view -> {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setPositiveButton("Yes", (dialog, which) -> {
                dialog.dismiss();
                createDatabase("OFF", "Due to Tecchinical Error");
                Toast.makeText(MainActivity.this, "App is off", Toast.LENGTH_SHORT).show();
            }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

            Utils.showAlertdialog(MainActivity.this, alertDialog, "App Off Alert!","Are you sure! You want to Enter Next Page?");
        });

        btn_app_on.setOnClickListener(v -> {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setPositiveButton("Yes", (dialog, which) -> {
                dialog.dismiss();
                createDatabase("ON", "reason");
                Toast.makeText(MainActivity.this, "App is on", Toast.LENGTH_SHORT).show();
            }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

            Utils.showAlertdialog(MainActivity.this, alertDialog, "App ON Alert!","Are you sure! You want to Enter Next Page?");
        });
    }



    private void createDatabase(String string, String reason) {

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("App_Utils").document("Utils");
        Map map = new HashMap();
        map.put("appOnOff", string);

        documentReference.update(map);
    }


}