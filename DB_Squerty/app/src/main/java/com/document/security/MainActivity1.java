package com.document.security;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.document.dbsecurity.R;
import com.document.security.adapter.C_Adapter;
import com.document.security.model.C_Model;
import com.document.security.my_dilog.C_dilog;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity1 extends AppCompatActivity {



    private   ProgressDialog  dialog;
    private Uri path;
    private RecyclerView rc;
    private FloatingActionButton floatingActionButton2;
    public static int count=0;
    public String key;
    public String type="";
    private  List<Uri> list=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
         key=getIntent().getStringExtra("key");
         type=getIntent().getStringExtra("key1");

        rc = (RecyclerView) findViewById(R.id.rc);
        floatingActionButton2 = (FloatingActionButton) findViewById(R.id.floatingActionButton2);

        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                C_dilog c_dilog=new C_dilog(MainActivity1.this,type);
                c_dilog.show();
                c_dilog.submit.setOnClickListener(v1 -> {
                    switch (C_dilog.REQUSE_CODE){
                        case 0:
                            sendData(c_dilog);
                            c_dilog.dismiss();
                            break;
                        case 1:
                            uplode_data(c_dilog,"image");
                            break;
                        case 2:
                            uplode_data(c_dilog,"video");
                            break;
                        case 3:
                            uplode_data(c_dilog,"pdf");
                            break;
                    }

                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        ProgressDialog dialogg=new ProgressDialog(MainActivity1.this);
        dialogg.setMessage("Please Wait...");
        dialogg.show();
        FirebaseDatabase.getInstance().getReference().child("child_data").child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> key1 =new ArrayList<>();
                List<C_Model> data =new ArrayList<>();

                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    C_Model c_model=dataSnapshot.getValue(C_Model.class);
                    key1.add(dataSnapshot.getKey());
                    data.add(c_model);
                }
                dialogg.dismiss();
                C_Adapter f_adapter=new C_Adapter(key1,data,type,key);
                rc.setAdapter(f_adapter);
                rc.setHasFixedSize(true);
                if(type.equals("image")){
                    rc.setLayoutManager(new GridLayoutManager(getApplicationContext(),3));
                }else if(type.equals("video")){
                    rc.setLayoutManager(new GridLayoutManager(getApplicationContext(),2));
                }
                else {
                rc.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialogg.dismiss();
                Toast.makeText(MainActivity1.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void sendData(C_dilog c_dilog) {
        String title=c_dilog.editTextTextPersonName.getText().toString();
        String dis=c_dilog.editTextTextPersonName2.getText().toString();
        if(title.isEmpty()){
            c_dilog.editTextTextPersonName.setError("Please Enter Something");
        }else if(dis.isEmpty()){
            c_dilog.editTextTextPersonName2.setError("Please Enter Something");
        }else {
            dialog = new ProgressDialog(this);
            dialog.setMessage("Please wait...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            C_Model c_model=new C_Model(title,dis,"text","No");
            FirebaseDatabase.getInstance().getReference().child("child_data").child(key).push().setValue(c_model).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Data Entered", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        if (resultCode == RESULT_OK && requestCode==1) {
               path = result.getData();
            list.clear();
            if (resultCode == RESULT_OK && result.getClipData() != null) {
                list.clear();
                path=null;
                int count = result.getClipData().getItemCount();
                int currentImage = 0;
                while (currentImage < count) {
                    Uri uri = result.getClipData().getItemAt(currentImage).getUri();
                    list.add(uri);
                    currentImage = currentImage + 1;
                }
            }
        }
        else if (resultCode == RESULT_OK && requestCode==2) {
            path = result.getData();
            list.clear();
            if (resultCode == RESULT_OK && result.getClipData() != null) {
                list.clear();
                path=null;
                int count = result.getClipData().getItemCount();
                int currentImage = 0;
                while (currentImage < count) {
                    Uri uri = result.getClipData().getItemAt(currentImage).getUri();
                    list.add(uri);
                    currentImage = currentImage + 1;
                }
            }
        }
        else if (resultCode == RESULT_OK && requestCode==3) {
            path = result.getData();
            list.clear();
            if (resultCode == RESULT_OK && result.getClipData() != null) {
                list.clear();
                path=null;
                int count = result.getClipData().getItemCount();
                int currentImage = 0;
                while (currentImage < count) {
                    Uri uri = result.getClipData().getItemAt(currentImage).getUri();
                    list.add(uri);
                    currentImage = currentImage + 1;
                }
            }
        }


    }

    public void uplode_data(C_dilog c_dilog, String image){
        if(image.equals("image")){
            if (!list.isEmpty())
            {
                dialog = new ProgressDialog(this);
                dialog.setMessage("Please wait...");
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

                for (int upload_count = 0; upload_count < list.size(); upload_count++) {
                    String s = FirebaseDatabase.getInstance().getReference().child("child_data").child(key).push().getKey();
                    Uri individualImage = list.get(upload_count);
                    StorageReference imgName = FirebaseStorage.getInstance().getReference().child("post").child(image).child(s);

                    int finalUpload_count = upload_count;
                    imgName.putFile(individualImage).addOnSuccessListener(taskSnapshot -> {
                        imgName.getDownloadUrl().addOnSuccessListener(uri1 -> {
                            String url = String.valueOf(uri1);
                            C_Model c_model=new C_Model("no","no",image, url);
                            FirebaseDatabase.getInstance().getReference().child("child_data").child(key).child(s).setValue(c_model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    if(finalUpload_count==list.size()-1){
                                        dialog.dismiss();
                                        c_dilog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Data Entered", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    dialog.dismiss();
                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        });
                    }).addOnProgressListener(snapshot -> {
                        double progress
                                = (100.0
                                * snapshot.getBytesTransferred()
                                / snapshot.getTotalByteCount());
                        dialog.setMessage(
                                (finalUpload_count +1)+"Uploaded "
                                        + (int) progress + "% / Total "+(list.size()+1));
                        dialog.show();
                    }).addOnFailureListener(e -> {
                        dialog.dismiss();
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

                }
            }
            else if(path!=null)
            {
                dialog = new ProgressDialog(this);
                dialog.setMessage("Please wait...");
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                String s = FirebaseDatabase.getInstance().getReference().child("child_data").child(key).push().getKey();
                final StorageReference filepath= FirebaseStorage.getInstance().getReference().child("post").child(image).child(Objects.requireNonNull(s));
                UploadTask uploadTask = (UploadTask) filepath.putFile(path);
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()){
                            dialog.dismiss();
                            Toast.makeText(MainActivity1.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            throw task.getException();
                        }
                        return filepath.getDownloadUrl();
                    }
                }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        C_Model c_model=new C_Model("no","no",image, uri.toString());
                        FirebaseDatabase.getInstance().getReference().child("child_data").child(key).child(s).setValue(c_model).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                dialog.dismiss();
                                c_dilog.dismiss();
                                Toast.makeText(getApplicationContext(), "Data Entered", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
            else {
                    Toast.makeText(this, "Select A File", Toast.LENGTH_SHORT).show();
            }

        }
        else if(image.equals("video") || image.equals("pdf")){
            String title=c_dilog.editTextTextPersonName.getText().toString();
            if(title.isEmpty()){
                c_dilog.editTextTextPersonName.setError("Please Enter Something");
            }else  if (!list.isEmpty())
            {
                dialog = new ProgressDialog(this);
                dialog.setMessage("Please wait...");
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

                for (int upload_count = 0; upload_count < list.size(); upload_count++) {
                    String s = FirebaseDatabase.getInstance().getReference().child("child_data").child(key).push().getKey();
                    Uri individualImage = list.get(upload_count);
                    StorageReference imgName = FirebaseStorage.getInstance().getReference().child("post").child(image).child(s);

                    int finalUpload_count = upload_count;
                    imgName.putFile(individualImage).addOnSuccessListener(taskSnapshot -> {
                        imgName.getDownloadUrl().addOnSuccessListener(uri1 -> {
                            String url = String.valueOf(uri1);
                            C_Model c_model=new C_Model(title,"no",image, url);
                            FirebaseDatabase.getInstance().getReference().child("child_data").child(key).child(s).setValue(c_model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    dialog.dismiss();
                                    c_dilog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Data Entered", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    dialog.dismiss();
                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        });
                    }).addOnProgressListener(snapshot -> {
                        double progress
                                = (100.0
                                * snapshot.getBytesTransferred()
                                / snapshot.getTotalByteCount());
                        dialog.setMessage(
                                (finalUpload_count +1)+"Uploaded "
                                        + (int) progress + "% / Total "+(list.size()+1));
                        dialog.show();
                    }).addOnFailureListener(e -> {
                        dialog.dismiss();
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

                }
            }
            else if(path!=null) {
                dialog = new ProgressDialog(this);
                dialog.setMessage("Please wait...");
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                String s = FirebaseDatabase.getInstance().getReference().child("child_data").child(key).push().getKey();
                final StorageReference filepath= FirebaseStorage.getInstance().getReference().child("post").child(image).child(Objects.requireNonNull(s));
                UploadTask uploadTask = (UploadTask) filepath.putFile(path);
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()){
                            dialog.dismiss();
                            Toast.makeText(MainActivity1.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            throw task.getException();
                        }
                        return filepath.getDownloadUrl();
                    }
                }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        C_Model c_model=new C_Model(title,"no",image, uri.toString());
                        FirebaseDatabase.getInstance().getReference().child("child_data").child(key).child(s).setValue(c_model).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                dialog.dismiss();
                                c_dilog.dismiss();
                                Toast.makeText(getApplicationContext(), "Data Entered", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
            else {
                   Toast.makeText(this, "Select A File", Toast.LENGTH_SHORT).show();

            }
        }

        }
    }
