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
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.skincarestudio.skincarestudioadmin.Adapter.GridProductViewAdapter;
import com.skincarestudio.skincarestudioadmin.Model.Product_item_model;
import com.skincarestudio.skincarestudioadmin.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AddProductActivity extends AppCompatActivity {

    //refer code generate code
    private static final int PICK_IMAGE = 1;
    private final String LETTER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final char[] ALPHANUMERIC = (LETTER + LETTER.toLowerCase() + "0123456789").toCharArray();

    private String[] listItem;
    private Uri imageUri;
    private ImageView imageView;
    private EditText ed_title, ed_description, ed_price, ed_link,ed_sbtitle;
    private RecyclerView recyclerView;
    private AlertDialog custom_dialog;
    private ProgressDialog progressDialog;
    private StorageReference storageReference;
    private FirebaseFirestore firestore;
    private StorageTask mUploadTask;
    private CollectionReference collectionReference;
    private DocumentReference documentReference;
    private GridProductViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        progressDialog = new ProgressDialog(this);
        firestore = FirebaseFirestore.getInstance();
        collectionReference = firestore.collection("product_list");
        storageReference=FirebaseStorage.getInstance().getReference("Images/Product/");
        recyclerView = findViewById(R.id.product_lst_view);
        FloatingActionButton actionButton = findViewById(R.id.product_floatingActionButton);
        actionButton.setOnClickListener(v -> getInputAndSetData());


        setUpRecyclerView();

    }

    @SuppressLint("NotifyDataSetChanged")
    private void setUpRecyclerView() {

        Query query = collectionReference;
        FirestoreRecyclerOptions<Product_item_model> options = new FirestoreRecyclerOptions
                .Builder<Product_item_model>()
                .setQuery(query, Product_item_model.class)
                .build();

        adapter = new GridProductViewAdapter(options);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(null);

        adapter.setOnItemClickListner((documentSnapshot, position) -> {
            final Product_item_model model = documentSnapshot.toObject(Product_item_model.class);
            assert model != null;
            listItem = new String[]{"Detail", "Edit", "Delete"};
            AlertDialog.Builder builder = new AlertDialog.Builder(AddProductActivity.this);
            builder.setTitle("Choose any One!");
            builder.setSingleChoiceItems(listItem, -1, (dialog, i) -> {
                switch (listItem[i]) {
                    case "Detail":
                        Intent intent = new Intent(AddProductActivity.this, ProductDetailsActivity.class);

                        intent.putExtra("title", model.getProduct_title());
                        intent.putExtra("des", model.getProduct_des());
                        intent.putExtra("img", model.getProduct_img());
                        intent.putExtra("sbtitle", model.getSb_title());
                        intent.putExtra("link", model.getLink());
                        intent.putExtra("price", model.getProduct_price());

                        startActivity(intent);
                        dialog.dismiss();
                        break;
                    case "Edit":
                        Intent intent1 = new Intent(AddProductActivity.this, EditProductDetailActivity.class);
                        intent1.putExtra("title", model.getProduct_title());
                        intent1.putExtra("id",documentSnapshot.getId());
                        intent1.putExtra("des", model.getProduct_des());
                        intent1.putExtra("img", model.getProduct_img());
                        intent1.putExtra("sbtitle",model.getSb_title());
                        intent1.putExtra("link",model.getLink());
                        intent1.putExtra("price",model.getProduct_price());
                        startActivity(intent1);
                        dialog.dismiss();
                        break;

                    case "Delete":
                        storageReference.child(model.getProduct_title()).delete();
                        documentSnapshot.getReference().delete();
                        Toast.makeText(AddProductActivity.this, "deleted", Toast.LENGTH_SHORT).show();
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

        AlertDialog.Builder alert = new AlertDialog.Builder(AddProductActivity.this);
        View view = getLayoutInflater().inflate(R.layout.add_product_dialog, null);

        ed_sbtitle=view.findViewById(R.id.toast_subtitle);
        ed_link = view.findViewById(R.id.toast_link);
        imageView = view.findViewById(R.id.toast_image);
        ed_price = view.findViewById(R.id.toast_price);
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
                Toast.makeText(AddProductActivity.this, "previous upload in progress", Toast.LENGTH_SHORT).show();
            } else {
                UploadFile();
            }
        });

    }

    private void UploadFile() {

        if (imageUri != null) {
            progressDialog.setMessage("Uploading Data....");
            progressDialog.show();

            final String sbTitle=ed_sbtitle.getText().toString();
            final String link = ed_link.getText().toString();
            final String price = ed_price.getText().toString();
            final String title = ed_title.getText().toString();
            final String des = ed_description.getText().toString();

            final StorageReference fileReference = FirebaseStorage.getInstance().getReference("Images/Product/").child(title);

            mUploadTask = fileReference.putFile(imageUri).addOnSuccessListener(
                    taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(
                            uri -> {

                                Map<String, String> map = new HashMap<>();
                                map.put("product_title", title);
                                map.put("product_des", des);
                                map.put("product_price", price);
                                map.put("link", link);
                                map.put("sb_title",sbTitle);
                                map.put("product_img", uri.toString());

                                documentReference = firestore.collection("product_list").document(generateRandomNumber());

                                documentReference
                                        .set(map)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                progressDialog.dismiss();
                                                custom_dialog.dismiss();
                                                Toast.makeText(AddProductActivity.this, "Product Added!", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(e -> {
                                            progressDialog.dismiss();
                                            Toast.makeText(AddProductActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                        });
                            })).addOnFailureListener(e -> {
                                progressDialog.dismiss();
                                Toast.makeText(AddProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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