package admin.perfect.trader.Activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import admin.perfect.trader.R;

public class IntradayDetailActivity extends AppCompatActivity {

    private TextInputEditText edCName, edT1, edT2, edSL;
    private TextView tv_timee, tv_date;
    private Button btnSave, btnDelete, btnMove;
    private Switch sT1, sT2, sSL, time_date;
    private String uid, collectionName, cName, calltype, ssl, sl, t1, t2, target1, target2, time, calldays;
    private DocumentReference documentReference;
    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intraday_detail);

        initViews();

        Toolbar toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        uid = getIntent().getStringExtra("id");
        calldays = getIntent().getStringExtra("type");
        collectionName = getIntent().getStringExtra("key");

        documentReference = FirebaseFirestore.getInstance().collection(collectionName).document(uid);

        getdata();

        sSL.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                documentReference.update("sl", "hit");
            } else {
                documentReference.update("sl", "");
            }
        });
        sT1.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                documentReference.update("t1", "hit");
            } else {
                documentReference.update("t1", "");
            }
        });
        sT2.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                documentReference.update("t2", "hit");
            } else {
                documentReference.update("t2", "");
            }
        });

        btnMove.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Call Move Alert!")
                    .setMessage("Are you Sure? You want to Move this Call on All Previous Call!")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        dialog.dismiss();

                        Map<String, String> map = new HashMap<>();
                        map.put("c_name", cName);
                        map.put("call_type", calltype);
                        map.put("sl", ssl);
                        map.put("stop_loss", sl);
                        map.put("t1", t1);
                        map.put("t2", t2);
                        map.put("target1", target1);
                        map.put("target2", target2);
                        map.put("time", time);
                        map.put("uid", uid);

                        DocumentReference moveReference = FirebaseFirestore.getInstance().collection("All_Previous_Call").document(uid);
                        moveReference.set(map).addOnSuccessListener(unused -> {

                            startActivity(new Intent(IntradayDetailActivity.this, IntradayCallActivity.class));
                            documentReference.delete().addOnSuccessListener(unused1 -> {
                                Toast.makeText(this, "Item Deleted Successfully!", Toast.LENGTH_SHORT).show();
                                finish();
                            }).addOnFailureListener(e -> {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            });

                        }).addOnFailureListener(e -> {
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }).setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            AlertDialog alert = builder.create();
            alert.show();
            Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
            nbutton.setTextColor(ContextCompat.getColor(this, R.color.black));
            Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setTextColor(ContextCompat.getColor(this, R.color.black));


        });

        btnSave.setOnClickListener(view -> {
            String sl = Objects.requireNonNull(edSL.getText()).toString().trim();
            String t2 = Objects.requireNonNull(edT2.getText()).toString().trim();
            String t1 = Objects.requireNonNull(edT1.getText()).toString().trim();
            String cname = Objects.requireNonNull(edCName.getText()).toString().trim();

            String date = tv_date.getText().toString();
            String time = tv_timee.getText().toString();

            if (time_date.isChecked()) {
                if (date.equals("Date")) {
                    Toast.makeText(this, "Date cann't be Empty!", Toast.LENGTH_SHORT).show();
                } else if (time.equals("Time")) {
                    Toast.makeText(this, "Time cann't be Empty!", Toast.LENGTH_SHORT).show();
                } else {
                    //convert date to timestamp
                    String str_date = date + " " + time;
                    Date datee = null;
                    DateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm");
                    try {
                        datee = (Date) formatter.parse(str_date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    //convert date to timestamp
                    if (sl.isEmpty() || t2.isEmpty() || t1.isEmpty() || cname.isEmpty()) {
                        Toast.makeText(this, "Field cann't be Empty", Toast.LENGTH_SHORT).show();
                    } else {
                        Map<String, Object> map = new HashMap<>();
                        map.put("stop_loss", sl);
                        map.put("target1", t1);
                        map.put("target2", t2);
                        map.put("c_name", cname);
                        map.put("time", String.valueOf(datee.getTime()));
                        documentReference.update(map).addOnSuccessListener(o -> {
                            Toast.makeText(this, "Update Successfully", Toast.LENGTH_SHORT).show();
                            getdata();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            } else {
                Map<String, Object> map = new HashMap<>();
                map.put("stop_loss", sl);
                map.put("target1", t1);
                map.put("target2", t2);
                map.put("c_name", cname);
                documentReference.update(map).addOnSuccessListener(o -> {
                    Toast.makeText(this, "Update Successfully", Toast.LENGTH_SHORT).show();
                    getdata();
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }

        });

        btnDelete.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Delete Call Alert!")
                    .setMessage("Are you Sure? You want to Remove this Call Permanently!")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        dialog.dismiss();
                        documentReference.delete().addOnSuccessListener(unused -> {
                            Toast.makeText(this, "Item Deleted Successfully!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(IntradayDetailActivity.this, IntradayCallActivity.class));
                         finish();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }).setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            AlertDialog alert = builder.create();
            alert.show();
            Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
            nbutton.setTextColor(ContextCompat.getColor(this, R.color.black));
            Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setTextColor(ContextCompat.getColor(this, R.color.black));
        });

        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        };

        tv_date.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            new DatePickerDialog(IntradayDetailActivity.this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        tv_timee.setOnClickListener(v -> {
            // Get Current Time
            final Calendar c = Calendar.getInstance();
            int mHour = c.get(Calendar.HOUR_OF_DAY);
            int mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            tv_timee.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        });

    }

    private void getdata() {
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                cName = task.getResult().getString("c_name");
                calltype = task.getResult().getString("call_type");
                ssl = task.getResult().getString("sl");
                sl = task.getResult().getString("stop_loss");
                t1 = task.getResult().getString("t1");
                t2 = task.getResult().getString("t2");
                target1 = task.getResult().getString("target1");
                target2 = task.getResult().getString("target2");
                time = task.getResult().getString("time");

                setdataOnViews();
            }
        });
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        tv_date.setText(sdf.format(myCalendar.getTime()));

    }

    private void setdataOnViews() {

        if (calldays.equals("today") || calldays.equals("all")) {
            btnMove.setVisibility(View.GONE);
        } else {
            btnMove.setVisibility(View.VISIBLE);
        }

        edCName.setText(cName);
        edT1.setText(target1);
        edT2.setText(target2);
        edSL.setText(sl);

        if (t1.equals("")) {
            sT1.setChecked(false);
        } else {
            sT1.setChecked(true);
        }
        if (t2.equals("")) {
            sT2.setChecked(false);
        } else {
            sT2.setChecked(true);
        }
        if (ssl.equals("")) {
            sSL.setChecked(false);
        } else {
            sSL.setChecked(true);
        }
    }

    private void initViews() {

        edCName = findViewById(R.id.ed_com_name);
        edT1 = findViewById(R.id.ed_targt1);
        edT2 = findViewById(R.id.eed_target2);
        edSL = findViewById(R.id.eed_sl);
        btnDelete = findViewById(R.id.btndel);
        btnSave = findViewById(R.id.button9);
        sSL = findViewById(R.id.sl);
        sT1 = findViewById(R.id.target1);
        sT2 = findViewById(R.id.target2);
        tv_timee = findViewById(R.id.tv_time);
        tv_date = findViewById(R.id.tv_date);
        time_date = findViewById(R.id.target4);
        btnMove = findViewById(R.id.btnmove);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}