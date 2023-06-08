package com.dinesh.adminwrokfromhome.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.dinesh.adminwrokfromhome.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class GetDataActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private TextView tv_email, tv_orderid, tv_plan;
    private CollectionReference email_reference, orderidRef,fullUserRef;
    private EditText email, orderid,fullUser;
    private CardView cardView;
    private Button find_btn, btnOrder,fulltime;
    public static ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_data);

        progressDialog = new ProgressDialog(this);
        firestore = FirebaseFirestore.getInstance();
        email_reference = firestore.collection("users");
        orderidRef = firestore.collection("payment_order_id");
        fullUserRef=firestore.collection("full_time_user");

        find_btn = findViewById(R.id.gd_find_btn);
        email = findViewById(R.id.gd_user_email);
        cardView = findViewById(R.id.cardv);
        btnOrder = findViewById(R.id.gd_btnorder);
        orderid = findViewById(R.id.gd_user_order);
        tv_email = findViewById(R.id.textView27);
        tv_orderid = findViewById(R.id.textView26);
        tv_plan = findViewById(R.id.textView25);
        fulltime=findViewById(R.id.gd_fullfind_btn);
        fullUser=findViewById(R.id.gd_fulluser_email);

        find_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userMail = email.getText().toString().toLowerCase().replace(" ", "");
                progressDialog.setMessage("Fetching data....");
                progressDialog.show();
                email.setText(" ");
                findUserDetail(userMail);
            }
        });

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String orderId = orderid.getText().toString();
                progressDialog.setMessage("Fetching data....");
                progressDialog.show();
                orderid.setText(" ");
                findorderId(orderId);
            }
        });

        fulltime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=fullUser.getText().toString().toLowerCase().replace(" ", "");
                progressDialog.setMessage("Fetching data...");
                progressDialog.show();
                fullUser.setText(" ");
                findfulluser(email);
            }
        });

    }

    private void findfulluser(String email) {

        fullUserRef
                .whereEqualTo("email",email+"@gmail.com")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progressDialog.dismiss();
                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                            if (task.isSuccessful()) {
                                Intent intent1 = new Intent(GetDataActivity.this, EditFullTimeUserActivity.class);
                                intent1.putExtra("email", documentSnapshot.getString("email"));
                                intent1.putExtra("end", documentSnapshot.getString("plan_end_date"));
                                intent1.putExtra("start", documentSnapshot.getString("plan_start_date"));
                                intent1.putExtra("status",documentSnapshot.getString("status"));
                                intent1.putExtra("post",documentSnapshot.getString("view_post"));
                                intent1.putExtra("wallet",documentSnapshot.getString("wallet"));
                                startActivity(intent1);
                            }{
                                Toast.makeText(GetDataActivity.this, "User doesn't exist", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(GetDataActivity.this, "User doesn't exist", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void findorderId(String orderId) {

        orderidRef
                .whereEqualTo("order_id", orderId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progressDialog.dismiss();
                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                            if (task.isSuccessful()) {
                                String email = documentSnapshot.getString("email");
                                String order = documentSnapshot.getString("order_id");
                                String plan = documentSnapshot.getString("user_updated_plan");

                                tv_email.setText(email);
                                tv_orderid.setText(order);
                                tv_plan.setText(plan);
                                cardView.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(GetDataActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void findUserDetail(String userMail) {

        email_reference.whereEqualTo("email", userMail+"@gmail.com")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progressDialog.dismiss();
                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(GetDataActivity.this, UserDetailActivity.class);
                                intent.putExtra("email", documentSnapshot.getString("email"));
                                startActivity(intent);
                            }else {
                                Toast.makeText(GetDataActivity.this,"User doesn't exist", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(GetDataActivity.this,"User doesn't exist", Toast.LENGTH_SHORT).show();
            }
        });


    }

}