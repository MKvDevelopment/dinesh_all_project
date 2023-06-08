package admin.perfect.trader.Activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
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

public class PositionalDetailActivity extends AppCompatActivity {

    private TextInputEditText edCName, edT1, edT2, edSL;
    private TextView tv_timee, tv_date, tv_ctime, tv_cdate;
    private Button btnSave, btnDelete, btnMove;
    private Switch sT1, sT2, sSL, time_date, time_currentDate;
    private String uid, cName, ssl, sl, t1, t2, target1, target2, stime, etime, callType, status;
    private DocumentReference documentReference;
    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_positional_detail);

        initViews();

        Toolbar toolbar = findViewById(R.id.toolbar5);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        uid = getIntent().getStringExtra("id");

        if (getIntent().getStringExtra("data").equals("1")) {
            documentReference = FirebaseFirestore.getInstance().collection("Today_Positional_Call").document(uid);
        } else {
            documentReference = FirebaseFirestore.getInstance().collection("Previous_Positional_Call").document(uid);
        }

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

        btnSave.setOnClickListener(view -> {
            String sl = Objects.requireNonNull(edSL.getText()).toString().trim();
            String t2 = Objects.requireNonNull(edT2.getText()).toString().trim();
            String t1 = Objects.requireNonNull(edT1.getText()).toString().trim();
            String cname = Objects.requireNonNull(edCName.getText()).toString().trim();

            String date = tv_date.getText().toString();
            String time = tv_timee.getText().toString();
            String cdate = tv_cdate.getText().toString();
            String ctime = tv_ctime.getText().toString();

            if (time_currentDate.isChecked()) {

                if (cdate.equals("date") || ctime.equals("time")) {
                    Toast.makeText(view.getContext(), "Please Select Current Date and Time", Toast.LENGTH_SHORT).show();
                } else {
                    //convert date to timestamp
                    String cuttent_date = cdate + " " + ctime;
                    Date c_datee = null;
                    DateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm");
                    try {
                        c_datee = (Date) formatter.parse(cuttent_date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (time_date.isChecked()) {
                        if (date.equals("Date")) {
                            Toast.makeText(this, "Date cann't be Empty!", Toast.LENGTH_SHORT).show();
                        } else if (time.equals("Time")) {
                            Toast.makeText(this, "Time cann't be Empty!", Toast.LENGTH_SHORT).show();
                        } else {
                            //convert date to timestamp
                            String str_date = date + " " + time;
                            Date e_dateee = null;
                            DateFormat formatterr = new SimpleDateFormat("dd/MM/yy HH:mm");
                            try {
                                e_dateee = (Date) formatterr.parse(str_date);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            //convert date to timestamp
                            if (sl.isEmpty() || t2.isEmpty() || t1.isEmpty() || cname.isEmpty()) {
                                Toast.makeText(this, "Field cann't be Empty", Toast.LENGTH_SHORT).show();
                            } else {
                                Map<String, Object> map = new HashMap<>();
                                map.put("stoploss", sl);
                                map.put("target1", t1);
                                map.put("target2", t2);
                                map.put("cName", cname);
                                map.put("status", "");
                                map.put("eDate", String.valueOf(e_dateee.getTime()));
                                map.put("sDate", String.valueOf(c_datee.getTime()));
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
                        map.put("status", "");
                        map.put("c_name", cname);
                        map.put("sDate", String.valueOf(c_datee.getTime()));
                        documentReference.update(map).addOnSuccessListener(o -> {
                            Toast.makeText(this, "Update Successfully", Toast.LENGTH_SHORT).show();
                            getdata();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }
                }

            } else {
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
                            map.put("stoploss", sl);
                            map.put("target1", t1);
                            map.put("target2", t2);
                            map.put("cName", cname);
                            map.put("status", "");
                            map.put("eDate", String.valueOf(datee.getTime()));
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
                    map.put("status", "");
                    map.put("c_name", cname);
                    documentReference.update(map).addOnSuccessListener(o -> {
                        Toast.makeText(this, "Update Successfully", Toast.LENGTH_SHORT).show();
                        getdata();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
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
                            startActivity(new Intent(PositionalDetailActivity.this, PositionalActivity.class));
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

        DatePickerDialog.OnDateSetListener e_date = (view, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateExpireDateLabel();
        };

        DatePickerDialog.OnDateSetListener c_date = (view, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateCurrentDateLabel();
        };

        tv_date.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            new DatePickerDialog(PositionalDetailActivity.this, e_date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        tv_cdate.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            new DatePickerDialog(PositionalDetailActivity.this, c_date, myCalendar
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

        tv_ctime.setOnClickListener(v -> {
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

                            tv_ctime.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        });


        btnMove.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Call Move Alert!")
                    .setMessage("Are you Sure? You want to Move this Call on All Previous Call!")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        dialog.dismiss();

                        Map map = new HashMap<>();
                        map.put("cName", cName);
                        map.put("target1", target1);
                        map.put("target2", target2);
                        map.put("stoploss", sl);
                        map.put("callType", callType);
                        map.put("uid", uid);
                        map.put("t1", t1);
                        map.put("t2", t2);
                        map.put("sl", ssl);
                        map.put("status", status);
                        map.put("sDate", stime);
                        map.put("eDate", etime);


                        DocumentReference moveReference = FirebaseFirestore.getInstance().collection("Previous_Positional_Call").document(uid);
                        moveReference.set(map).addOnSuccessListener(unused -> {

                            startActivity(new Intent(PositionalDetailActivity.this, PositionalActivity.class));
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

    }

    private void updateCurrentDateLabel() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        tv_cdate.setText(sdf.format(myCalendar.getTime()));

    }

    private void updateExpireDateLabel() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        tv_date.setText(sdf.format(myCalendar.getTime()));

    }

    private void getdata() {
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                cName = task.getResult().getString("cName");
                ssl = task.getResult().getString("sl");
                sl = task.getResult().getString("stoploss");
                t1 = task.getResult().getString("t1");
                t2 = task.getResult().getString("t2");
                target1 = task.getResult().getString("target1");
                target2 = task.getResult().getString("target2");
                stime = task.getResult().getString("sDate");
                etime = task.getResult().getString("eDate");
                status = task.getResult().getString("status");
                callType = task.getResult().getString("callType");

                setdataOnViews();
            }
        });


    }

    private void setdataOnViews() {

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
        btnMove = findViewById(R.id.btnmove);
        btnSave = findViewById(R.id.button9);
        sSL = findViewById(R.id.sl);
        sT1 = findViewById(R.id.target1);
        sT2 = findViewById(R.id.target2);
        tv_timee = findViewById(R.id.tv_time);
        tv_date = findViewById(R.id.tv_date);
        time_date = findViewById(R.id.target4);
        tv_cdate = findViewById(R.id.tv_cdate);
        tv_ctime = findViewById(R.id.tv_ctime);
        time_currentDate = findViewById(R.id.target5);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}