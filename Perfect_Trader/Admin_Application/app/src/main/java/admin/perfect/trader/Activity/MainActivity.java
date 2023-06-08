package admin.perfect.trader.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import admin.perfect.trader.Adatper.SliderAdapter;
import admin.perfect.trader.Constant.App_Utils;
import admin.perfect.trader.Model.LinkModel;
import admin.perfect.trader.R;


public class MainActivity extends AppCompatActivity {

    private CollectionReference adminRef;
    private DocumentReference userRef;
    private SliderView sliderView;
    private SliderAdapter sliderAdapter;
    private ProgressDialog progressDialog;
    public static String USER_IMAGE, SUBSCRIPTION, TOKEN;
    private CardView btnIntraday, btnGlobal, btnPositonal, btnUserCommunity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_home);

        initViews();

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle("Please Wait!");
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        final String uidd = FirebaseAuth.getInstance().getUid();

        adminRef = FirebaseFirestore.getInstance().collection("Main_slider_banner");
        assert uidd != null;
        userRef = FirebaseFirestore.getInstance().collection("User_List").document(uidd);
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


        adminRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            ArrayList<LinkModel> model_list = new ArrayList<>();

            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {

                LinkModel model = documentSnapshot.toObject(LinkModel.class);
                model_list.add(model);

            }

            sliderView = findViewById(R.id.imageSlider);
            sliderAdapter = new SliderAdapter(model_list);
            sliderView.setSliderAdapter(sliderAdapter);
            sliderView.setIndicatorAnimation(IndicatorAnimationType.DROP);
            sliderView.setAutoCycle(true);
            sliderAdapter.notifyDataSetChanged();
            progressDialog.dismiss();
        });

        // Initializing Users database
        DatabaseReference usersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
       // usersDatabase.keepSynced(true); // For offline use


        btnUserCommunity.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, UserCommunity.class));
        });
        btnPositonal.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, PositionalActivity.class));
        });
        btnIntraday.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, IntradayCallActivity.class));
        });
        btnGlobal.setOnClickListener(view -> {
            Toast.makeText(this, "Comming Soon", Toast.LENGTH_SHORT).show();
        });

    }
    private void initViews() {
        btnGlobal = findViewById(R.id.btn_global);
        btnIntraday = findViewById(R.id.btn_intraday);
        btnPositonal = findViewById(R.id.btn_positional);
        btnUserCommunity = findViewById(R.id.btn_user_comm);
    }


}