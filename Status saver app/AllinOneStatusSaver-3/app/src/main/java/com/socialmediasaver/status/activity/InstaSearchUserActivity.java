package com.socialmediasaver.status.activity;

import static com.socialmediasaver.status.util.Utils.Subscription;
import static com.socialmediasaver.status.util.Utils.bannerInit;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.socialmediasaver.status.R;
import com.socialmediasaver.status.adapter.InstaSearchUserAdapter;
import com.socialmediasaver.status.api.CommonClassForAPI;
import com.socialmediasaver.status.model.InstagramSearch.InstagramSearchModel;
import com.socialmediasaver.status.retrofit.Api;
import com.socialmediasaver.status.retrofit.ApiConstant;
import com.socialmediasaver.status.retrofit.ApiInterface;
import com.socialmediasaver.status.util.SharePrefs;
import com.socialmediasaver.status.util.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.startapp.sdk.ads.banner.Banner;

import io.reactivex.observers.DisposableObserver;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InstaSearchUserActivity extends AppCompatActivity {
    EditText searchEditText;
    RecyclerView recyclerView;
    ProgressBar serach_progress;
    String s1;
    Call<InstagramSearchModel> call;
    TextView text;
    CommonClassForAPI commonClassForAPI;
    LinearLayout instalogin, serachLayout;
    TextView tvLogin, tvViewStories;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insta_search_activity);

        Toolbar toolbar = findViewById(R.id.insta_toolbar);
        text = findViewById(R.id.text);
        tvLogin = findViewById(R.id.tvLogin1);
        serachLayout = findViewById(R.id.serachLayout);
        instalogin = findViewById(R.id.instalogin);
        serach_progress = findViewById(R.id.serach_progress);
        recyclerView = findViewById(R.id.recyclerView);
        tvViewStories = findViewById(R.id.tvViewStories);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        searchEditText = findViewById(R.id.searchEditText);
        commonClassForAPI = CommonClassForAPI.getInstance(this);
        checksubscription();

        if (!SharePrefs.getInstance(InstaSearchUserActivity.this).getBoolean(SharePrefs.ISINSTALOGIN)) {
            //showAlertDialog();
            instalogin.setVisibility(View.VISIBLE);
            serachLayout.setVisibility(View.GONE);
            //showAlertDialog();
            Login();
        } else {
            instalogin.setVisibility(View.GONE);
            serachLayout.setVisibility(View.VISIBLE);
            init_TextWatcher();
        }
        // init_TextWatcher();
        //showAlertDialog();


    }

    private void checksubscription() {
        if (Subscription.equals("NO")) {
            Banner banner = (Banner) findViewById(R.id.startAppInstasearch);
            banner.setVisibility(View.VISIBLE);
            banner.showBanner();
        }

    }

    public void Login() {

        tvLogin.setOnClickListener(v -> {
            // Toast.makeText(InstaSearchUserActivity.this, "hi", Toast.LENGTH_SHORT).show();
            showAlertDialog();
        });
    }


    private void showAlertDialog() {
        androidx.appcompat.app.AlertDialog custome_dialog;

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.insta_login_alert_layout, null, false);

        Button allow = view.findViewById(R.id.button4);
        Button deny = view.findViewById(R.id.button5);

        builder.setView(view);
        deny.setVisibility(View.GONE);

        custome_dialog = builder.create();
        custome_dialog.setCanceledOnTouchOutside(false);
        custome_dialog.show();

        allow.setOnClickListener(v -> {
            custome_dialog.dismiss();
            Intent intent = new Intent(InstaSearchUserActivity.this,
                    LoginActivity.class);
            startActivityForResult(intent, 100);
        });
        deny.setOnClickListener(v -> {
            custome_dialog.dismiss();
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == 100 && resultCode == RESULT_OK) {
                String requiredValue = data.getStringExtra("key");
                if (SharePrefs.getInstance(this).getBoolean(SharePrefs.ISINSTALOGIN)) {
                    instalogin.setVisibility(View.GONE);
                    serachLayout.setVisibility(View.VISIBLE);
                    init_TextWatcher();
                } else {
                    instalogin.setVisibility(View.VISIBLE);
                    serachLayout.setVisibility(View.GONE);
                    tvLogin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showAlertDialog();
                        }
                    });

                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void init_TextWatcher() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                text.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                serach_progress.setVisibility(View.VISIBLE);

            }

            @Override
            public void afterTextChanged(Editable s) {
                serach_progress.setVisibility(View.VISIBLE);
                text.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                s1 = searchEditText.getText().toString().trim();
                if (s1.length() > 0) {
                    if (call != null)
                        call.cancel();
                    fetchUsers(s1);

                    callStoriesDetailApi(s1);

                } else {
                    if (call != null)
                        call.cancel();
                }

            }
        });
    }


    private void callStoriesDetailApi(String UserId) {
        try {
            Utils utils = new Utils(this);
            if (utils.isNetworkAvailable()) {
                if (commonClassForAPI != null) {

                    commonClassForAPI.getSearchData(storyDetailObserver, UserId, "ds_user_id=" + SharePrefs.getInstance(this).getString(SharePrefs.USERID)
                            // commonClassForAPI.getStories(storyDetailObserver, "ds_user_id=" + SharePrefs.getInstance(this).getString(SharePrefs.USERID)
                            // commonClassForAPI.getFullDetailFeed_(storyDetailObserver, UserId, "ds_user_id=" + SharePrefs.getInstance(this).getString(SharePrefs.USERID)
                            + "; sessionid=" + SharePrefs.getInstance(this).getString(SharePrefs.SESSIONID));


                }
            } else {
                Utils.setToast(this, this
                        .getResources().getString(R.string.no_net_conn));
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private DisposableObserver<InstagramSearchModel> storyDetailObserver = new DisposableObserver<InstagramSearchModel>() {
        @Override
        public void onNext(InstagramSearchModel response) {

            try {

                recyclerView.setVisibility(View.VISIBLE);
                text.setVisibility(View.GONE);

                serach_progress.setVisibility(View.GONE);
                GridLayoutManager gridLayoutManager;
                gridLayoutManager = new GridLayoutManager(InstaSearchUserActivity.this, 1);
                recyclerView.setLayoutManager(gridLayoutManager);
                recyclerView.addItemDecoration(new DividerItemDecoration(InstaSearchUserActivity.this, GridLayoutManager.VERTICAL));
                InstaSearchUserAdapter adapter = new InstaSearchUserAdapter(InstaSearchUserActivity.this, response.getUsers());
                recyclerView.setAdapter(adapter);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(@NonNull Throwable e) {

        }

        @Override
        public void onComplete() {

        }

    };


    private void fetchUsers(String username) {
        serach_progress.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        ApiInterface apiInterface;
        apiInterface = (new Api().getClient(ApiConstant.BASE_URL).create(ApiInterface.class));
        call = apiInterface.getusers("ds_user_id=" + SharePrefs.getInstance(this).getString(SharePrefs.USERID)
                // commonClassForAPI.getFullDetailFeed_(storyDetailObserver, UserId, "ds_user_id=" + SharePrefs.getInstance(this).getString(SharePrefs.USERID)
                + "; sessionid=" + SharePrefs.getInstance(this).getString(SharePrefs.SESSIONID), username);
        call.enqueue(new Callback<InstagramSearchModel>() {
            @Override
            public void onResponse(Call<InstagramSearchModel> call, Response<InstagramSearchModel> response) {

                InstagramSearchModel root = response.body();
                if (response.code() == 200) {

                    if (response.code() == 200) {
                        if (response.body().getUsers().size() != 0) {


                            recyclerView.setVisibility(View.VISIBLE);
                            text.setVisibility(View.GONE);

                            serach_progress.setVisibility(View.GONE);
                            GridLayoutManager gridLayoutManager;
                            gridLayoutManager = new GridLayoutManager(InstaSearchUserActivity.this, 1);
                            recyclerView.setLayoutManager(gridLayoutManager);
                            recyclerView.addItemDecoration(new DividerItemDecoration(InstaSearchUserActivity.this, GridLayoutManager.VERTICAL));
                            InstaSearchUserAdapter adapter = new InstaSearchUserAdapter(InstaSearchUserActivity.this, response.body().getUsers());
                            recyclerView.setAdapter(adapter);
                        } else {
                            text.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }
                    } else {
                        text.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        //Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
                    }
                } else if (response.code() != 200) {
                    Toast.makeText(InstaSearchUserActivity.this, "Error " + response.code() + " found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<InstagramSearchModel> call, Throwable t) {
                // Log error here since request failed
                serach_progress.setVisibility(View.GONE);
                call.cancel();
            }
        });
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
