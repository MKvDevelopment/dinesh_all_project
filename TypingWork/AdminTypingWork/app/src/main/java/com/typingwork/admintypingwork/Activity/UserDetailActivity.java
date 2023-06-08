package com.typingwork.admintypingwork.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.typingwork.admintypingwork.R;
import com.typingwork.admintypingwork.databinding.ActivityUserDetailBinding;

public class UserDetailActivity extends AppCompatActivity {

    private ActivityUserDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityUserDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String id=getIntent().getStringExtra("uid");
        DocumentReference reference = FirebaseFirestore.getInstance().collection("Users_list").document(id);
        reference.addSnapshotListener((value, error) -> {
            binding.tvName.setText("Name:- "+value.getString("name"));
            binding.tvWallet.setText("Wallet:- "+value.getString("wallet"));
            binding.tvActivate.setText("Activate:- "+value.getString("activation"));
            binding.tvWithdraw.setText("Withdraw:- "+value.getString("withdraw"));
            binding.tvWrite.setText("Right Entry:- "+value.getString("right_entry"));
            binding.tvWrong.setText("Wrong Entry:- "+value.getString("wrong_entry"));

            String status=value.getString("block");
            if (status.equals("Block")){
                binding.btnBlock.setChecked(true);
            }

        });


        binding.btnBlock.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                reference.update("block", "Block");
            } else {
                reference.update("block", "UnBlock");
            }
        });

    }
}