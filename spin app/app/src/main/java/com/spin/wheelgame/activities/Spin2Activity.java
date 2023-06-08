package com.spin.wheelgame.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.UpdateAppearance;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.spin.wheelgame.R;
import com.spin.wheelgame.utils.NetworkChangeReceiver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rubikstudio.library.LuckyWheelView;
import rubikstudio.library.model.LuckyItem;

public class Spin2Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static String RATE_US_URL, APP_VERSION;

    private MediaPlayer spinSound;
    private MediaPlayer coinSound;
    private int indexx;
    private Button spinButton;
    private TextView titleTextView;
    private LuckyWheelView wheelView;
    private EditText ed_mob;
    private AlertDialog custom_dialog;
    private NetworkChangeReceiver broadcastReceiver = new NetworkChangeReceiver();
    private String user_name, email, image, depositbal, winBal, previousIndex, rate_count,row;
    private DocumentReference documentReference, adminRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spin2);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        documentReference = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        adminRef = FirebaseFirestore.getInstance().collection("App_utlil").document("Admin_detail");

        findViewById(R.id.btn_add_money).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Spin2Activity.this, AddMoneyActivity.class);
                startActivity(intent);
            }
        });
        //broadcast receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, filter);
        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Spin to Win");
        setSupportActionBar(toolbar);

        adminRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                APP_VERSION = value.getString("app_version");
                RATE_US_URL = value.getString("rate_link");
                String reason = value.getString("app_of_reason");
                String status = value.getString("app_on");

                if (status.equals("OFF")) {
                    androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(Spin2Activity.this);
                    alertDialog.setTitle("Alert!");
                    alertDialog.setMessage(reason);
                    alertDialog.setCancelable(false);
                    alertDialog.setPositiveButton("Exit Now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    }).show();

                }
                checkForUpdate();
            }
        });


        //drawer layout
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);


        findViewById(R.id.btn_withdraw).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Spin2Activity.this, WithdrawActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });


        titleTextView = findViewById(R.id.title);
        wheelView = findViewById(R.id.wheel);
        spinSound = MediaPlayer.create(this, R.raw.spinsoundeffect);
        coinSound = MediaPlayer.create(this, R.raw.coin);

        int[] items = {35, 5, 30, 80, -10, 12, 60, 100, 15, 20, 10, 40, 25, 70, 45, 50};

        int[] colors = {
                Color.parseColor("#33ccff"),
                Color.parseColor("#44c8fb"),
                Color.parseColor("#55c4f7"),
                Color.parseColor("#66bff2"),
                Color.parseColor("#77bbee"),
                Color.parseColor("#88b7ea"),
                Color.parseColor("#A9C0ED"),
                Color.parseColor("#adc2eb"),
                Color.parseColor("#bdc0e8"),
                Color.parseColor("#aaaee1"),
                Color.parseColor("#bbaadd"),
                Color.parseColor("#d6b8e1"),
                Color.parseColor("#cca6d9"),
                Color.parseColor("#dda1d4"),
                Color.parseColor("#ee9dd0"),
                Color.parseColor("#FA9AD6")};


        int[] targetIndex1 = {13,14,12,3,6,9,11,0,2,5,10,15,4,8,1,4,5,1,4,10,1,4,4,4,1,1,1,1,4,5,1,1,};
        int[] targetIndex2 = {3,12,6,14,9,13,2,11,0,10,4,5,15,1,8,4,1,5,4,1,1,1,4,4,4,1,4,5,8,1,4,9,};

        List<LuckyItem> data = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            LuckyItem luckyItem = new LuckyItem();
            luckyItem.topText = "\u20B9 " + items[i];
            luckyItem.color = colors[i % 16];
            data.add(luckyItem);
        }
        wheelView.setData(data);
        wheelView.setTouchEnabled(false);

        wheelView.setLuckyRoundItemSelectedListener(new LuckyWheelView.LuckyRoundItemSelectedListener() {
            @Override
            public void LuckyRoundItemSelected(int index) {
                // indexx = index;
                spinButton.setEnabled(true);
                spinSound.stop();
                //addWinning(items[index]);
                final ProgressDialog progress = new ProgressDialog(Spin2Activity.this);
                progress.setMessage("Updating Balance");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setIndeterminate(true);
                progress.setCancelable(false);
                progress.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progress.dismiss();
                        if (index == 4) {
                            showLosingDialog(items[index]);
                        } else {
                            showWinningDialog(items[index]);
                        }
                    }
                }, 4000);

            }

        });

        spinButton = findViewById(R.id.spin_button);
        spinButton.setOnClickListener(v -> {
            int indexx = Integer.parseInt(previousIndex);
            int bal = Integer.parseInt(depositbal);
            if (bal < 10) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Spin2Activity.this);
                alertDialogBuilder.setTitle("Add Money");
                alertDialogBuilder
                        .setMessage("Minimum 10 \u20B9 required in deposits to spin")
                        .setCancelable(false)
                        .setPositiveButton("Add Money", (dialog, id) -> startActivity(new Intent(Spin2Activity.this, AddMoneyActivity.class)))
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            } else if (Integer.parseInt(winBal) >= 500) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Spin2Activity.this);
                alertDialogBuilder.setTitle("Congratulation's!");
                alertDialogBuilder
                        .setMessage("Now you are eligible to withdraw money.Click Continue to withdraw money.")
                        .setCancelable(false)
                        .setPositiveButton("Continue", (dialog, which) -> {
                            Intent intent = new Intent(Spin2Activity.this, WithdrawActivity.class);
                            intent.putExtra("email", email);
                            startActivity(intent);
                        })
                        .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            } else {

                switch (Integer.parseInt(row))
                {
                    case 1:
                        wheelView.startLuckyWheelWithTargetIndex(targetIndex1[indexx]);
                    case 2:
                        wheelView.startLuckyWheelWithTargetIndex(targetIndex2[indexx]);

                }

                //incrIndex();
                int i = ++indexx;
                documentReference.update("index", String.valueOf(i));
                spinButton.setEnabled(false);
                spinSound = MediaPlayer.create(Spin2Activity.this, R.raw.spinsoundeffect);
                spinSound.start();


            }

        });

        String text = titleTextView.getText().toString();
        SpannableString spannableString = new SpannableString(text);
        final int start = 0;
        final int end = text.length();

        spannableString.setSpan(new RainbowSpan(this), start, end, 0);
        titleTextView.setText(spannableString);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.btn_refer_earn:
                referDialog();
                break;

            case R.id.btn_earn_more:
                earnMoreMoneyDialog();
                break;

            case R.id.btn_whatsapp:
                whatspDialog();
                break;

            case R.id.privacy_policy:
                Intent intent2 = new Intent(Spin2Activity.this, ContentActivity.class);
                intent2.putExtra("name", "Privacy Policy");
                intent2.putExtra("file_name", "privacy");
                startActivity(intent2);
                break;

            case R.id.terms:
                Intent intent1 = new Intent(Spin2Activity.this, ContentActivity.class);
                intent1.putExtra("name", "Terms and Conditions");
                intent1.putExtra("file_name", "terms");
                startActivity(intent1);
                break;

            case R.id.exit:
                exitDialog();
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void earnMoreMoneyDialog() {
        boolean cunt = Integer.parseInt(rate_count) >= 1;
        if (Integer.parseInt(winBal) <= 200) {
            Toast.makeText(this, "Winning balance must be upto 200 \u20B9.", Toast.LENGTH_LONG).show();
        } else if (cunt) {
            Toast.makeText(this, "You had already completed this task.", Toast.LENGTH_SHORT).show();
        } else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Alert!!");
            alertDialog.setMessage("To get \u20B9 5. You have to give 5 Star rating and write positive review");
            alertDialog.setCancelable(true);
            alertDialog.setPositiveButton("Rate Now", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //go to the playstore link
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(RATE_US_URL));
                    startActivity(intent);
                    //update balance

                    int total_bal = Integer.parseInt(depositbal) + 5;
                    int total_count = Integer.parseInt(rate_count) + 1;
                    Map map = new HashMap();
                    map.put("deposit_balence", String.valueOf(total_bal));
                    map.put("user_rate_count", String.valueOf(total_count));

                    documentReference.update(map);
                    dialog.dismiss();
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
        }
    }

    private void referDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Spin2Activity.this);
        alertDialogBuilder.setTitle("Refer and Earn");
        alertDialogBuilder
                .setMessage("Refer your friend and get \u20B9 30 into your wallet once your friend makes his First withdrawal.")
                .setCancelable(false)
                .setPositiveButton("Refer a Friend",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType("text/plain");
                                intent.putExtra(Intent.EXTRA_TEXT, "Hey!! friends - Today I find this amazing application Spin to Win. Earn a lot of money from this app at home by smartphone. Spin the wheel and Stand a chance to win upto 100 \u20B9. " + RATE_US_URL);
                                intent.putExtra(Intent.EXTRA_SUBJECT, RATE_US_URL);
                                startActivity(Intent.createChooser(intent, "Share via"));
                            }
                        })

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();


    }

    private void exitDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Spin2Activity.this);
        alertDialogBuilder.setTitle("Exit Confirmation");
        alertDialogBuilder
                .setMessage("Click yes to Exit!")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                moveTaskToBack(true);
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(1);
                            }
                        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    private void whatspDialog() {

        if (Integer.parseInt(winBal) <= 200) {
            Toast.makeText(this, "Winning balance must be upto 200 \u20B9.", Toast.LENGTH_LONG).show();
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Spin2Activity.this);
            alertDialogBuilder.setTitle("Contact with us");
            alertDialogBuilder
                    .setMessage("To directly contact with our representative, you have to pay 200 \u20B9 as convenience fee.")
                    .setCancelable(false)
                    .setPositiveButton("Pay Now",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (Integer.parseInt(depositbal) >= 200) {
                                        getMobFromUser();
                                        dialog.cancel();
                                    } else {
                                        minBalDialog();
                                        dialog.cancel();
                                    }
                                }
                            })

                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        }
    }

    private void getMobFromUser() {

        AlertDialog.Builder alert = new AlertDialog.Builder(Spin2Activity.this);
        View view = getLayoutInflater().inflate(R.layout.add_product_dialog, null);

        ed_mob = view.findViewById(R.id.toast_mob);

        Button btn_cancel = (Button) view.findViewById(R.id.toast_cancel);
        Button btn_submit = (Button) view.findViewById(R.id.toast_submit);
        alert.setView(view);

        custom_dialog = alert.create();
        custom_dialog.setCanceledOnTouchOutside(false);
        custom_dialog.show();

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                custom_dialog.dismiss();
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ed_mob.getText().toString().isEmpty()) {
                    Toast.makeText(Spin2Activity.this, "Enter your Email Address", Toast.LENGTH_SHORT).show();
                } else {
                    custom_dialog.dismiss();
                    ProgressDialog progressDialog = new ProgressDialog(Spin2Activity.this);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("Uploading your Request...");
                    progressDialog.show();

                    new Handler().postDelayed(() -> {

                        int remain = Integer.parseInt(depositbal) - 100;
                        documentReference.update("deposit_balence", String.valueOf(remain)).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressDialog.dismiss();

                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(Spin2Activity.this);
                                alertDialog.setTitle("Thanks Dear!");
                                alertDialog.setMessage("Our representative will contact you as soon as possible. Please wait our response.");
                                alertDialog.setCancelable(false);
                                alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                                Toast.makeText(Spin2Activity.this, "Request sent successfully.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }, 3000);
                }


            }
        });


    }

    private void minBalDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Spin2Activity.this);
        alertDialogBuilder.setTitle("Insufficient Money!");
        alertDialogBuilder
                .setMessage("You have insufficient money in your deposit wallet. Please add more money in deposit wallet!")
                .setCancelable(false)
                .setPositiveButton("Add Now",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivity(new Intent(Spin2Activity.this, AddMoneyActivity.class));
                            }
                        })
                .setNegativeButton("Add Later", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();


    }

    private static class RainbowSpan extends CharacterStyle implements UpdateAppearance {
        private final int[] colors;

        public RainbowSpan(Context context) {
            colors = context.getResources().getIntArray(R.array.rainbow);
        }

        public void updateDrawState(TextPaint paint) {
            paint.setStyle(Paint.Style.FILL);
            Shader shader = new LinearGradient(0, 0, 0, paint.getTextSize() * colors.length, colors,
                    null, Shader.TileMode.MIRROR);
            Matrix matrix = new Matrix();
            matrix.setRotate(90);
            shader.setLocalMatrix(matrix);
            paint.setShader(shader);
        }
    }

    private void showWinningDialog(Object selectedItem) {

        final ProgressDialog progress = new ProgressDialog(Spin2Activity.this);
        progress.setMessage("Updating Balance");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false);

        FancyGifDialog build = new FancyGifDialog.Builder(this)
                .setTitle("Congrats!!  \uD83E\uDD70, You have won \u20B9" + selectedItem + ".")
                .setMessage("Amount will be added to your winnings.")
                .setGifResource(R.drawable.trophy)
                .OnPositiveClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        updateUserBal(selectedItem, progress);
                    }
                })
                .setPositiveBtnText("Spin More")
                .isCancellable(false)
                .setNegativeBtnText("Cancel").OnNegativeClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        updateUserBal(selectedItem, progress);
                    }
                }).build();
        coinSound.start();
    }

    private void updateUserBal(Object selectedItem, ProgressDialog progressDialog) {

        int remain = Integer.parseInt(depositbal) - 10;
        long hh = Long.parseLong(winBal);
        int winbale = (int) (hh + Long.parseLong(String.valueOf(selectedItem)));

        Map map = new HashMap();
        map.put("deposit_balence", String.valueOf(remain));
        map.put("winning_balence", String.valueOf(winbale));

        documentReference.update(map).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                progressDialog.dismiss();
            }
        });
    }

    private void showLosingDialog(Object selectedItem) {

        final ProgressDialog progress = new ProgressDialog(Spin2Activity.this);
        progress.setMessage("Updating Balance");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false);


        FancyGifDialog build = new FancyGifDialog.Builder(this)
                .setTitle("Bad luck!!, You have lost \u20B9" + (int) selectedItem + ".")
                .setMessage("Amount will be deducted from your winnings.")
                .setGifResource(R.drawable.depressed)
                .OnPositiveClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        progress.show();
                        updateUserBal(selectedItem, progress);
                    }
                })
                .setPositiveBtnText("Spin More")
                .isCancellable(false)
                .setNegativeBtnText("Cancel").OnNegativeClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        updateUserBal(selectedItem, progress);
                    }
                }).build();
        coinSound.start();
    }

    public void refreshWalletUI() {
        ((TextView) findViewById(R.id.wallet_balance_deposits)).setText("\u20B9 " + depositbal);
        ((TextView) findViewById(R.id.wallet_balance_winnings)).setText("\u20B9 " + winBal);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshWalletUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Fetch admin personal data
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                user_name = value.getString("name");
                email = value.getString("email");
                image = value.getString("image");
                row=value.getString("row");
                rate_count = value.getString("user_rate_count");
                depositbal = value.getString("deposit_balence");
                winBal = value.getString("winning_balence");
                previousIndex = value.getString("index");

                setheaderData(email, image, user_name);
                refreshWalletUI();

            }
        });


    }

    private void setheaderData(String email, String image, String user_name) {
        ImageView headerImage;
        TextView headerEmail, headerName;

        NavigationView navigationView = findViewById(R.id.nav_view);
        View view = navigationView.getHeaderView(0);

        headerName = view.findViewById(R.id.nav_username);
        headerEmail = view.findViewById(R.id.nav_useremail);
        headerImage = view.findViewById(R.id.nav_userimage);

        headerName.setText("Hi " + user_name + "!");
        headerEmail.setText(email);
        Picasso.get().load(image).placeholder(R.mipmap.ic_launcher).into(headerImage);
    }
    private void checkForUpdate() {

        String app_version = String.valueOf(getCurrentAppVersionCode());

        if (!app_version.contains(APP_VERSION)) {

            new AlertDialog.Builder(this).setPositiveButton("Update Now", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    //go to the playstore link
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(RATE_US_URL));
                    startActivity(intent);
                    finish();
                }
            }).setTitle("Update Available!")
                    .setMessage("New Version is Available. Update now otherwise you can not access our Application")
                    .setCancelable(false).show();
        }

    }

    private int getCurrentAppVersionCode() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

}
