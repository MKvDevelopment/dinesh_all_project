package com.typingwork.admintypingwork.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.typingwork.admintypingwork.Adapter.AlertListAdapter;
import com.typingwork.admintypingwork.Adapter.SearchListAdapter;
import com.typingwork.admintypingwork.Model.SearchListModel;
import com.typingwork.admintypingwork.R;
import com.typingwork.admintypingwork.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, SearchListAdapter.searchListAdapterListner {

    ActivityMainBinding binding;
    private DocumentReference adminReference, userRef;
    private SearchListAdapter adapter;
    public static String USER_IMAGE, SUBSCRIPTION, TOKEN;
    private ArrayList<SearchListModel> list=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        final String uidd = FirebaseAuth.getInstance().getUid();

        adminReference = FirebaseFirestore.getInstance().collection("App_Utils").document("App_Utils");
        userRef = FirebaseFirestore.getInstance().collection("Users_list").document(uidd);
        userRef.addSnapshotListener((value, error) -> {
            assert value != null;
            USER_IMAGE = value.getString("image");
            SUBSCRIPTION = value.getString("subscritpion");
            TOKEN = value.getString("device_id");

            FirebaseMessaging.getInstance().getToken().addOnSuccessListener(newToken -> {
                Map<String, Object> map = new HashMap<>();
                if (!TOKEN.contains(newToken)) {
                    // userRef.update("device_id", newToken);
                    map.put("device_id", newToken);
                    userRef.update(map);

                    Map<String, Object> mapp = new HashMap<>();
                    mapp.put("online", "true");
                    mapp.put("token", newToken);
                    FirebaseDatabase.getInstance().getReference().child("admin").updateChildren(mapp);
                }
            });
        });

        adminReference.addSnapshotListener((value, error) -> {
            String chat_status = value.getString("chat");
            String Premium_Chat = value.getString("Premium_Chat");
            if (chat_status.equals("On")) {
                binding.btnChatOnOff.setChecked(true);
            } else {
                binding.btnChatOnOff.setChecked(false);
            }
            if (Premium_Chat.equals("On")) {
                binding.btnPremiumChatOnOff.setChecked(true);
            } else {
                binding.btnPremiumChatOnOff.setChecked(false);
            }



        });
        binding.floatingActionButton2.setOnClickListener(view -> {
            Intent userProfileIntent = new Intent(MainActivity.this, ChatActivity.class);
            startActivity(userProfileIntent);
        });
        binding.btnWithdraw.setOnClickListener(view -> {
            Intent userProfileIntent = new Intent(MainActivity.this, WithdrawActivity.class);
            startActivity(userProfileIntent);
        });
        binding.btnWalletAlert.setOnClickListener(view -> {
            Intent userProfileIntent = new Intent(MainActivity.this, WalletAlertActivity.class);
            startActivity(userProfileIntent);
        });
        binding.btnChatOnOff.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                adminReference.update("chat", "On");
            } else {
                adminReference.update("chat", "Off");
            }
        });

        binding.btnPremiumChatOnOff.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                adminReference.update("Premium_Chat", "On");
            } else {
                adminReference.update("Premium_Chat", "Off");
            }
        });


        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

        //Creating the ArrayAdapter instance having the filter list
        ArrayAdapter<String> aa = new ArrayAdapter<>(
                this,
                R.layout.custom_spinner,
                getResources().getStringArray(R.array.orderFilterList));
        aa.setDropDownViewResource(R.layout.custom_spinner_dropdown);

        spinner.setAdapter(aa);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                binding.edittext.setVisibility(View.GONE);
                break;

            case 1:
                //by Uid
                setUserByFilter("uid");
                // setupRecycleView(collectionReference);
                onStart();
                break;

            case 2:
                //by email
                setUserByFilter("email");
                //  setupRecycleView(collectionReference.whereEqualTo("status", "Pending"));
                onStart();
                break;

            case 3:
                //by phone number
                setUserByFilter("phone");
                //setupRecycleView(collectionReference.whereEqualTo("status", "Completed"));
                onStart();
                break;
        }
    }

    private void setUserByFilter(String key) {
        binding.edittext.setVisibility(View.VISIBLE);
        binding.btnSearch.setVisibility(View.VISIBLE);


        binding.btnSearch.setOnClickListener(view -> {
            String data = binding.edittext.getText().toString().trim();
            findUser(key, data);
        });


    }

    private void findUser(String key, String data) {
        CollectionReference previusReference = FirebaseFirestore.getInstance().collection("Users_list");
        previusReference
                .whereEqualTo(key, data)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    list.clear();
                    if (!queryDocumentSnapshots.isEmpty()){

                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            SearchListModel model = new SearchListModel();
                            model.setWallet(documentSnapshot.getString("wallet"));
                            model.setName(documentSnapshot.getString("name"));
                            model.setWithdraw(documentSnapshot.getString("withdraw"));
                            model.setActivation(documentSnapshot.getString("activation"));
                            model.setBlock(documentSnapshot.getString("block"));
                            model.setWrong_entry(documentSnapshot.getString("wrong_entry"));
                            model.setRight_entry(documentSnapshot.getString("right_entry"));
                            model.setPhoto(documentSnapshot.getString("photo"));
                            model.setUid(documentSnapshot.getString("uid"));
                            list.add(model);
                        }
                        adapter = new SearchListAdapter(list);
                        binding.recyclerview.setAdapter(adapter);
                        adapter.setListAdapterListner(this);
                    }else{
                        Toast.makeText(this, "No Data Found", Toast.LENGTH_SHORT).show();
                    }

                }).addOnFailureListener(e -> {

                });



    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClickListner( int position) {
        Intent intent=new Intent(MainActivity.this,UserDetailActivity.class);
        intent.putExtra("uid",list.get(position).getUid());
        startActivity(intent);
    }

    public void changeAppDataPage(View view) {

        startActivity(new Intent(MainActivity.this,AdminDataChangeActivity.class));

    }
}