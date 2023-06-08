package com.typingwork.jobathome.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.typingwork.jobathome.Adapter.MessageAdapter;
import com.typingwork.jobathome.Model.Message;
import com.typingwork.jobathome.R;
import com.typingwork.jobathome.Utils.NetworkChangeReceiver;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private DatabaseReference userDatabase;
    private DatabaseReference chatDatabase;
    private DatabaseReference currentUserRef;
    private ValueEventListener userListener, chatListener;
    // Will handle old/new messages between users
    private Query messagesDatabase;
    private ChildEventListener messagesListener;
    private MessageAdapter messagesAdapter;
    private final List<Message> messagesList = new ArrayList<>();
    // User data
    private String currentUserId;
    // activity_chat views
    private EditText messageEditText;
    private RecyclerView recyclerView;
    private ImageView sendButton;
    // chat_bar views
    private TextView adminNamee, adminStatuss, adminOflineStatus;
    private CircleImageView adminImgg;
    // Will be used on Notifications to determinate if user has chat window open
    public static String otherUserId, otherUserToken;
    public static boolean running = false;
    private String adminImg, adminName, adminStatus;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        running = true;

        //off screenshot
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);


        //check network connectivity
        NetworkChangeReceiver broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);


        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("loading...");

        messageEditText = findViewById(R.id.chat_message);
        recyclerView = findViewById(R.id.chat_recycler);
        adminImgg = findViewById(R.id.imageView2);
        adminNamee = findViewById(R.id.textView3);
        adminStatuss = findViewById(R.id.textView5);
        adminOflineStatus = findViewById(R.id.textView4);

        currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        otherUserId = getIntent().getStringExtra("id");
        otherUserToken = getIntent().getStringExtra("token");

        DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference().child("admin");
        currentUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

        checkCurrentUserToken();
        adminRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                adminImg = Objects.requireNonNull(snapshot.child("image").getValue()).toString();
                adminStatus = Objects.requireNonNull(snapshot.child("online").getValue()).toString();
                adminName = Objects.requireNonNull(snapshot.child("name").getValue()).toString();

                Picasso.get().load(Uri.parse(adminImg)).placeholder(R.drawable.logo).into(adminImgg);
                adminNamee.setText(adminName+" here");

                if (adminStatus.equals("true")) {
                    adminOflineStatus.setText("Hey Am here, Ask me. Have \nyou any Doubt or Problem?");
                    adminStatuss.setText("(Online)");
                    adminStatuss.setTextColor(getResources().getColor(R.color.green));
                } else {
                    adminStatuss.setText("(Offline)");
                    adminStatuss.setTextColor(getResources().getColor(R.color.red));
                    adminOflineStatus.setText("Sorry! Am far. Leave msg. \nI reply as soon as possible.");
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        messagesAdapter = new MessageAdapter(messagesList);

        recyclerView.setAdapter(messagesAdapter);

        // Action bar related

        // Will handle the send button to send a message

        sendButton = findViewById(R.id.chat_send);
        sendButton.setOnClickListener(view ->
                sendMessage());

        ImageView sendPictureButton = findViewById(R.id.chat_send_picture);
        sendPictureButton.setOnClickListener(view -> {
            Intent galleryIntent = new Intent();
            galleryIntent.setType("image/*");
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(Intent.createChooser(galleryIntent, "Select Image"), 1);
        });


        // Will handle typing feature, 0 means no typing, 1 typing, 2 deleting and 3 thinking (5+ sec delay)

        messageEditText.addTextChangedListener(new TextWatcher() {
            private Timer timer = new Timer();

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (messagesList.size() > 0) {
                    if (charSequence.length() == 0) {
                        FirebaseDatabase.getInstance().getReference().child("Chat").child(currentUserId).child(otherUserId).child("typing").setValue(0);

                        timer.cancel();
                    } else if (i2 > 0) {
                        FirebaseDatabase.getInstance().getReference().child("Chat").child(currentUserId).child(otherUserId).child("typing").setValue(1);

                        timer.cancel();
                        timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                FirebaseDatabase.getInstance().getReference().child("Chat").child(currentUserId).child(otherUserId).child("typing").setValue(3);
                            }
                        }, 5000);
                    } else if (i1 > 0) {
                        FirebaseDatabase.getInstance().getReference().child("Chat").child(currentUserId).child(otherUserId).child("typing").setValue(2);

                        timer.cancel();
                        timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                FirebaseDatabase.getInstance().getReference().child("Chat").child(currentUserId).child(otherUserId).child("typing").setValue(3);
                            }
                        }, 5000);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // Checking if root layout changed to detect soft keyboard

        final RelativeLayout root = findViewById(R.id.chat_root);
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            int previousHeight = root.getRootView().getHeight() - root.getHeight() - recyclerView.getHeight();

            @Override
            public void onGlobalLayout() {
                int height = root.getRootView().getHeight() - root.getHeight() - recyclerView.getHeight();

                if (previousHeight != height) {
                    if (previousHeight > height) {
                        previousHeight = height;
                    } else {
                        recyclerView.scrollToPosition(messagesList.size() - 1);

                        previousHeight = height;
                    }
                }
            }
        });

    }

    private void checkCurrentUserToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
            Map map=new HashMap();
            map.put("token",token);
            currentUserRef.updateChildren(map);
        }).addOnFailureListener(e -> {

        });

    }


    private String getDate(long time_stamp_server) {

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        return formatter.format(time_stamp_server);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // progressDialog.show();
        running = true;
        Map<String,Object> map = new HashMap<>();
        map.put("status", "online");
        currentUserRef.updateChildren(map);
        loadMessages();
        initDatabases();
    }

    @Override
    protected void onPause() {
        super.onPause();

        running = false;
        Map<String,Object> map = new HashMap<>();
        map.put("status", String.valueOf(new Date().getTime()));
        currentUserRef.updateChildren(map);
        if (messagesList.size() > 0 && messageEditText.getText().length() > 0) {
            FirebaseDatabase.getInstance().getReference().child("Chat").child(currentUserId).child(otherUserId).child("typing").setValue(0);
        }

        removeListeners();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri url = data.getData();

            DatabaseReference messageRef = FirebaseDatabase.getInstance().getReference().child("Messages").child(currentUserId).child(otherUserId).push();
            final String messageId = messageRef.getKey();

            DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications").child(otherUserId).push();
            final String notificationId = notificationRef.getKey();

            StorageReference file = FirebaseStorage.getInstance().getReference().child("message_images").child(messageId + ".jpg");

            file.putFile(url).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    //String imageUrl = task.getResult().getMetadata().getReference().getDownloadUrl().toString();

                    file.getDownloadUrl().addOnSuccessListener(uri -> {

                        Map<String,Object> messageMap = new HashMap<>();
                        messageMap.put("message", String.valueOf(uri));
                        messageMap.put("type", "image");
                        messageMap.put("from", currentUserId);
                        messageMap.put("to", otherUserId);
                        messageMap.put("timestamp", ServerValue.TIMESTAMP);

                        HashMap<String, String> notificationData = new HashMap<>();
                        notificationData.put("from", currentUserId);
                        notificationData.put("type", "message");
                        notificationData.put("to", otherUserId);
                        notificationData.put("token", otherUserToken);

                        Map<String,Object> userMap = new HashMap<>();
                        userMap.put("Messages/" + currentUserId + "/" + otherUserId + "/" + messageId, messageMap);
                        userMap.put("Messages/" + otherUserId + "/" + currentUserId + "/" + messageId, messageMap);

                        userMap.put("Chat/" + currentUserId + "/" + otherUserId + "/message", "You have sent a picture.");
                        userMap.put("Chat/" + currentUserId + "/" + otherUserId + "/timestamp", ServerValue.TIMESTAMP);
                        userMap.put("Chat/" + currentUserId + "/" + otherUserId + "/seen", ServerValue.TIMESTAMP);

                        userMap.put("Chat/" + otherUserId + "/" + currentUserId + "/message", "Has send you a picture.");
                        userMap.put("Chat/" + otherUserId + "/" + currentUserId + "/timestamp", ServerValue.TIMESTAMP);
                        userMap.put("Chat/" + otherUserId + "/" + currentUserId + "/seen", 0);

                        userMap.put("Notifications/" + otherUserId + "/" + notificationId, notificationData);


                        FirebaseDatabase.getInstance().getReference().updateChildren(userMap, (databaseError, databaseReference) -> {
                            sendButton.setEnabled(true);

                            //   Toast.makeText(ChatActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    });
                }
            });
        }
    }

    private void initDatabases() {
        // Initialize/Update realtime other user data such as name and online status

        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(otherUserId);
        userListener = new ValueEventListener() {
            Timer timer;

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    final String online = Objects.requireNonNull(dataSnapshot.child("online").getValue()).toString();

                    if (online.equals("true")) {
                        if (timer != null) {
                            timer.cancel();
                            timer = null;
                        }

                    } else {
                        timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                ChatActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                    }
                                });
                            }
                        }, 2000);
                    }

                } catch (Exception e) {
                    //  Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ChatActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        userDatabase.addValueEventListener(userListener);

        //Check if last message is unseen and mark it as seen with current timestamp

        chatDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(currentUserId).child(otherUserId);
        chatListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.hasChild("seen")) {
                        long seen = (long) dataSnapshot.child("seen").getValue();

                        if (seen == 0) {
                            chatDatabase.child("seen").setValue(ServerValue.TIMESTAMP);
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ChatActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        chatDatabase.addValueEventListener(chatListener);
    }

    private void loadMessages() {

        messagesList.clear();

        // Load/Update all messages between current and other user
        //  Toast.makeText(this, otherUserId, Toast.LENGTH_SHORT).show();
        messagesDatabase = FirebaseDatabase.getInstance().getReference()
                .child("Messages")
                .child(currentUserId)
                .child(otherUserId);
        messagesListener = new ChildEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildAdded(@NotNull DataSnapshot dataSnapshot, String s) {
                progressDialog.show();
                if (dataSnapshot.exists()) {
                    try {
                        Message message = dataSnapshot.getValue(Message.class);
                        messagesList.add(message);
                        messagesAdapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(messagesList.size() - 1);
                        progressDialog.dismiss();
                    } catch (Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(ChatActivity.this, "No message found", Toast.LENGTH_SHORT).show();
                }
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                messagesAdapter.notifyDataSetChanged();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                messagesAdapter.notifyDataSetChanged();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
                messagesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(ChatActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        messagesDatabase.addChildEventListener(messagesListener);
    }

    private void removeListeners() {
        try {
            chatDatabase.removeEventListener(chatListener);
            chatListener = null;

            userDatabase.removeEventListener(userListener);
            userListener = null;

            messagesDatabase.removeEventListener(messagesListener);
            messagesListener = null;
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        sendButton.setEnabled(false);

        String message = messageEditText.getText().toString();

        if (message.length() == 0) {
            Toast.makeText(getApplicationContext(), "Message cannot be empty", Toast.LENGTH_SHORT).show();

            sendButton.setEnabled(true);
        } else {
            messageEditText.setText("");

            // Pushing message/notification so we can get keyIds

            DatabaseReference userMessage = FirebaseDatabase.getInstance().getReference().child("Messages").child(currentUserId).child(otherUserId).push();
            String pushId = userMessage.getKey();

            DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications").child(otherUserId).push();
            String notificationId = notificationRef.getKey();

            // "Packing" message

            Map<String,Object> messageMap = new HashMap<>();
            messageMap.put("message", message);
            messageMap.put("type", "text");
            messageMap.put("from", currentUserId);
            messageMap.put("to", otherUserId);
            messageMap.put("msg_key", pushId);
            messageMap.put("timestamp", ServerValue.TIMESTAMP);

            HashMap<String, String> notificationData = new HashMap<>();
            notificationData.put("from", currentUserId);
            notificationData.put("type", "message");
            notificationData.put("to", otherUserId);
            notificationData.put("token", otherUserToken);

            Map<String,Object> userMap = new HashMap<>();
            userMap.put("Messages/" + currentUserId + "/" + otherUserId + "/" + pushId, messageMap);
            userMap.put("Messages/" + otherUserId + "/" + currentUserId + "/" + pushId, messageMap);

            userMap.put("Chat/" + currentUserId + "/" + otherUserId + "/message", message);
            userMap.put("Chat/" + currentUserId + "/" + otherUserId + "/timestamp", ServerValue.TIMESTAMP);
            userMap.put("Chat/" + currentUserId + "/" + otherUserId + "/seen", ServerValue.TIMESTAMP);

            userMap.put("Chat/" + otherUserId + "/" + currentUserId + "/message", message);
            userMap.put("Chat/" + otherUserId + "/" + currentUserId + "/timestamp", ServerValue.TIMESTAMP);
            userMap.put("Chat/" + otherUserId + "/" + currentUserId + "/seen", 0);

            userMap.put("Users/" + currentUserId + "/last_msg", message);
            userMap.put("Users/" + currentUserId + "/seen", "no");
            userMap.put("Users/" + currentUserId + "/date", String.valueOf(new Date().getTime()));

            userMap.put("Notifications/" + otherUserId + "/" + notificationId, notificationData);

            // Updating database with the new data including message, chat and notification

            FirebaseDatabase.getInstance().getReference().updateChildren(userMap, (databaseError, databaseReference) -> {
                sendButton.setEnabled(true);

                if (databaseError != null) {
                    Toast.makeText(ChatActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private String getTimeAgo(long time) {
        final long diff = System.currentTimeMillis() - time;

        if (diff < 1) {
            return " just now";
        }
        if (diff < 60 * 1000) {
            if (diff / 1000 < 2) {
                return diff / 1000 + " second ago";
            } else {
                return diff / 1000 + " seconds ago";
            }
        } else if (diff < 60 * (60 * 1000)) {
            if (diff / (60 * 1000) < 2) {
                return diff / (60 * 1000) + " minute ago";
            } else {
                return diff / (60 * 1000) + " minutes ago";
            }
        } else if (diff < 24 * (60 * (60 * 1000))) {
            if (diff / (60 * (60 * 1000)) < 2) {
                return diff / (60 * (60 * 1000)) + " hour ago";
            } else {
                return diff / (60 * (60 * 1000)) + " hours ago";
            }
        } else {
            if (diff / (24 * (60 * (60 * 1000))) < 2) {
                return diff / (24 * (60 * (60 * 1000))) + " day ago";
            } else {
                return diff / (24 * (60 * (60 * 1000))) + " days ago";
            }
        }
    }



}