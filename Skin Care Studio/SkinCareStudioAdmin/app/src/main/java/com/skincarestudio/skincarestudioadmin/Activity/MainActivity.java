package com.skincarestudio.skincarestudioadmin.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.skincarestudio.skincarestudioadmin.R;

public class MainActivity extends AppCompatActivity {

    private String[] listItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addProduct = findViewById(R.id.button2);
        addProduct.setOnClickListener(view ->
                startActivity(new Intent(MainActivity.this,AddProductActivity.class)));
        Button addData = findViewById(R.id.button);
        addData.setOnClickListener(view -> {
            listItem = new String[]{"Hair Tips", "Skin Tips"};
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Choose any Plan");
            builder.setSingleChoiceItems(listItem, -1, (dialog, i) -> {
                switch (listItem[i]) {
                    case "Hair Tips":
                    case "Skin Tips":
                        activityStart(listItem[i]);
                        dialog.dismiss();
                        break;
                }
            }).setNeutralButton("Cancel", (dialog, which) -> dialog.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();

        });

    }

    private void activityStart(String s) {
        Intent intent = new Intent(MainActivity.this, AddTipsActivity.class);
        intent.putExtra("plan", s);
        startActivity(intent);

    }
}