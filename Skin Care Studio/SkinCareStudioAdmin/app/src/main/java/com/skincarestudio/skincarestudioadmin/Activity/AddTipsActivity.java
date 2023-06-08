package com.skincarestudio.skincarestudioadmin.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.skincarestudio.skincarestudioadmin.Adapter.AddWebRecyclerAdapter;
import com.skincarestudio.skincarestudioadmin.Model.WebDataModel;
import com.skincarestudio.skincarestudioadmin.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AddTipsActivity extends AppCompatActivity {

    //refer code generate code
    private static final int PICK_IMAGE = 1;
    private final String LETTER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final char[] ALPHANUMERIC = (LETTER + LETTER.toLowerCase() + "0123456789").toCharArray();

    private Uri imageUri;
    private String plan_nm;
    private ImageView imageView;
    private EditText ed_title, ed_description, ed_sbTitle;
    private RecyclerView recyclerView;
    private AlertDialog custom_dialog;
    private String[] listItem;
    private AddWebRecyclerAdapter adapter;
    private ProgressDialog progressDialog;

    private FirebaseFirestore firestore;
    public static StorageReference storageReference;

    private StorageTask mUploadTask;
    private CollectionReference collectionReference;
    private DocumentReference documentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tips);

        plan_nm = getIntent().getStringExtra("plan");

        progressDialog = new ProgressDialog(this);
        firestore = FirebaseFirestore.getInstance();
        collectionReference = firestore.collection(plan_nm);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("Images/Tips_Image/");

        recyclerView = findViewById(R.id.lst_view);
        FloatingActionButton actionButton = findViewById(R.id.floatingActionButton);
        actionButton.setOnClickListener(v -> getInputAndSetData());


        setUpRecyclerView();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setUpRecyclerView() {

        Query query = collectionReference;
        FirestoreRecyclerOptions<WebDataModel> options = new FirestoreRecyclerOptions
                .Builder<WebDataModel>()
                .setQuery(query, WebDataModel.class)
                .build();

        adapter = new AddWebRecyclerAdapter(options);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(null);

        adapter.setOnItemClickListner((documentSnapshot, position) -> {

            final WebDataModel model = documentSnapshot.toObject(WebDataModel.class);

            listItem = new String[]{"Detail", "Edit", "Delete"};
            AlertDialog.Builder builder = new AlertDialog.Builder(AddTipsActivity.this);
            builder.setTitle("Choose any One!");
            builder.setSingleChoiceItems(listItem, -1, (dialog, i) -> {
                assert model != null;
                switch (listItem[i]) {
                    case "Detail":
                        Intent intent = new Intent(AddTipsActivity.this, TipDetailActivity.class);

                        intent.putExtra("title", model.getProduct_title());
                        intent.putExtra("des", model.getProduct_des());
                        intent.putExtra("img", model.getProduct_img());
                        startActivity(intent);
                        dialog.dismiss();
                        break;
                    case "Edit":
                        Intent intent1 = new Intent(AddTipsActivity.this, EditTipDetailActivity.class);
                        intent1.putExtra("title", model.getProduct_title());
                        intent1.putExtra("id",documentSnapshot.getId());
                        intent1.putExtra("plan",plan_nm);
                        intent1.putExtra("des", model.getProduct_des());
                        intent1.putExtra("img", model.getProduct_img());
                        intent1.putExtra("sbtitle",model.getSb_title());
                        startActivity(intent1);
                        dialog.dismiss();
                        break;

                    case "Delete":
                        AddTipsActivity.storageReference.child(model.getProduct_title()).delete();
                        documentSnapshot.getReference().delete();
                        Toast.makeText(AddTipsActivity.this, "deleted", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        break;

                }
            }).setNeutralButton("Cancel", (dialog, which) -> dialog.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();

        });
        adapter.notifyDataSetChanged();

    }

    private void getInputAndSetData() {

        AlertDialog.Builder alert = new AlertDialog.Builder(AddTipsActivity.this);
        View view = getLayoutInflater().inflate(R.layout.custom_dialog_weblink, null);

        ed_sbTitle = view.findViewById(R.id.toast_sb_title);
        imageView = view.findViewById(R.id.toast_image);
        ed_title = (EditText) view.findViewById(R.id.toast_title);
        ed_description = (EditText) view.findViewById(R.id.toast_des);

        Button btn_cancel = (Button) view.findViewById(R.id.toast_cancel);
        Button btn_submit = (Button) view.findViewById(R.id.toast_submit);
        alert.setView(view);

        custom_dialog = alert.create();
        custom_dialog.setCanceledOnTouchOutside(false);
        custom_dialog.show();

        imageView.setOnClickListener(view1 -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        });

        btn_cancel.setOnClickListener(v -> custom_dialog.dismiss());

        btn_submit.setOnClickListener(v -> {
            if (mUploadTask != null && mUploadTask.isInProgress()) {
                Toast.makeText(AddTipsActivity.this, "previous upload in progress", Toast.LENGTH_SHORT).show();
            } else {
                UploadFile();
            }
        });

    }

    private void UploadFile() {

        if (imageUri != null) {
            progressDialog.setMessage("Uploading Data....");
            progressDialog.show();

            final String sbtitle = ed_sbTitle.getText().toString();
            final String title = ed_title.getText().toString();
            final String des = ed_description.getText().toString();

            final StorageReference fileReference = FirebaseStorage.getInstance().getReference("Images/Tips_Image/").child(title);

            mUploadTask = fileReference.putFile(imageUri).addOnSuccessListener(
                    taskSnapshot -> fileReference.getDownloadUrl().
                            addOnSuccessListener(uri -> {

                                Map<String,String> map = new HashMap<>();
                                map.put("product_title", title);
                                map.put("product_des", des);
                                map.put("sb_title", sbtitle);
                                map.put("product_img", uri.toString());

                                documentReference = firestore.collection(plan_nm).document(generateRandomNumber());

                                documentReference
                                        .set(map)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                progressDialog.dismiss();
                                                custom_dialog.dismiss();
                                                Toast.makeText(AddTipsActivity.this, "Tip Uploaded!", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(e -> {
                                            progressDialog.dismiss();
                                            Toast.makeText(AddTipsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                        });
                            })).addOnFailureListener(e -> {
                                progressDialog.dismiss();
                                Toast.makeText(AddTipsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }).addOnProgressListener(taskSnapshot -> {
                                double uplods = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                progressDialog.setProgress((int) uplods);
                                progressDialog.setMessage("Uploaded:" + uplods + " %");
                                progressDialog.show();
                            });

        } else {
            Toast.makeText(this, "plese select image", Toast.LENGTH_SHORT).show();
        }
    }

    private String generateRandomNumber() {

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            result.append(ALPHANUMERIC[new Random().nextInt(ALPHANUMERIC.length)]);
        }

        return result.toString();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            assert data != null;
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageView.setImageBitmap(bitmap);


            } catch (IOException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}
