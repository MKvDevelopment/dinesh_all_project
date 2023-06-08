package com.socialmediasaver.status.activity;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.socialmediasaver.status.R;
import com.socialmediasaver.status.util.NetworkChangeReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class InstaProfileActivity extends AppCompatActivity {

    private Button search;
    private TextView tvPost,tvFollowers,tvFollowing;
    private ImageView imageView;
    private EditText editText;
    private int post,followers,following;
    private ConstraintLayout constraintLayout;
    String incomingJson;
    private NetworkChangeReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insta_profile);

        //check network connectivity
        broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);

        Toolbar toolbar = findViewById(R.id.insta_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        search = findViewById(R.id.button6);
        editText = findViewById(R.id.ed_search);
        tvFollowers = findViewById(R.id.textView9);
        tvFollowing = findViewById(R.id.textView10);
        tvPost = findViewById(R.id.textView8);
        imageView = findViewById(R.id.imageView1);
        constraintLayout = findViewById(R.id.user_display);

        search.setOnClickListener(v -> {
            String username = editText.getText().toString().trim().toLowerCase();

            if (username.isEmpty()) {
                editText.setError("Enter UserName");
            } else {
                new JsonReader(username).execute();
            }
        });

    }

    private String getjsonData(String link) {
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(link);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.connect();
            int respocode = con.getResponseCode();

            if (respocode == 200) {
                //result ok
                InputStream inputStream = con.getInputStream();
                Scanner scanner = new Scanner(inputStream);
                while (scanner.hasNext()) {
                    sb.append(scanner.nextLine());

                }

            }
        } catch (MalformedURLException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return (sb.toString());

    }

    class JsonReader extends AsyncTask<Void, Void, Void> {

        private String name;

        JsonReader() {

        }

        JsonReader(String name) {

            this.name = name;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String link = "https://www.instagram.com/" + name + "/?__a=10";


            incomingJson = getjsonData(link);
            Log.e("lund",incomingJson);

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            try {
                JSONObject mainobj = new JSONObject(incomingJson);
                JSONObject graphqlObj = mainobj.getJSONObject("graphql");
                JSONObject userObj = graphqlObj.getJSONObject("user");
                JSONObject followersObj = userObj.getJSONObject("edge_followed_by");
                followers=followersObj.getInt("count");

                JSONObject followObj = userObj.getJSONObject("edge_follow");
                following=followObj.getInt("count");

                JSONObject postObj = userObj.getJSONObject("edge_owner_to_timeline_media");
                post=postObj.getInt("count");

                constraintLayout.setVisibility(View.VISIBLE);
                String image = userObj.getString("profile_pic_url_hd");

                if (image.isEmpty()) {
                    Toast.makeText(InstaProfileActivity.this, "null", Toast.LENGTH_SHORT).show();
                } else {
                     Glide.with(getApplicationContext()).load(image).into(imageView);
                }
                tvFollowers.setText(String.valueOf(followers));
                tvFollowing.setText(String.valueOf(following));
                tvPost.setText(String.valueOf(post));

            } catch (JSONException e) {
                Toast.makeText(InstaProfileActivity.this, "Error  " + e.getMessage(), Toast.LENGTH_SHORT).show();
                System.out.println("Error  " + e.getMessage());
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}