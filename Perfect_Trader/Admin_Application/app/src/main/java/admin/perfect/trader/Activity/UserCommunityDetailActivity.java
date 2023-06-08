package admin.perfect.trader.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import admin.perfect.trader.R;

public class UserCommunityDetailActivity extends AppCompatActivity {

    private Button btnSave, btnDelete;
    private EditText edTitle, edDesc;
    private String itemId, title, desc;
    private DocumentReference documentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_community_detail);

        Toolbar toolbar = findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        btnSave = findViewById(R.id.btn_save);
        btnDelete = findViewById(R.id.btn_delete);
        edTitle = findViewById(R.id.ed_title);
        edDesc = findViewById(R.id.ed_desc);

        itemId = getIntent().getStringExtra("id");
        title = getIntent().getStringExtra("title");
        desc = getIntent().getStringExtra("desc");

        edTitle.setText(title);
        edDesc.setText(desc);

        documentReference = FirebaseFirestore.getInstance().collection("User_Community")
                .document(itemId);

        btnSave.setOnClickListener(view -> {

            String title = edTitle.getText().toString();
            String desc = edDesc.getText().toString();

            if (title.isEmpty() || desc.isEmpty()) {
                Toast.makeText(this, "Field's cannt be Empty", Toast.LENGTH_SHORT).show();
            }

            Map<String, Object> map = new HashMap<>();
            map.put("title", title);
            map.put("des", desc);
            documentReference.update(map).addOnCompleteListener(task -> {
                Toast.makeText(this, "Update Successfully", Toast.LENGTH_SHORT).show();
            });
        });
        btnDelete.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Delete Community Alert!")
                    .setMessage("Are you Sure? You want to Remove this Community Permanently!")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        dialog.dismiss();
                        documentReference.delete().addOnSuccessListener(unused -> {
                            Toast.makeText(this, "Item Deleted Successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        }).addOnFailureListener(e ->
                                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
                    }).setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            AlertDialog alert = builder.create();
            alert.show();
            Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
            nbutton.setTextColor(ContextCompat.getColor(this, R.color.black));
            Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setTextColor(ContextCompat.getColor(this, R.color.black));
        });


    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}