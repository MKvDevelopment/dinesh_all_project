package com.earnmoney.adminearnmoney.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.earnmoney.adminearnmoney.Adapter.MessageAdapter;
import com.earnmoney.adminearnmoney.Model.Message;
import com.earnmoney.adminearnmoney.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class    ChatActivity extends AppCompatActivity implements MessageAdapter.messageAdapterListner {

    // Code Recieve
    private Switch btnChatBlock;
    private DatabaseReference userDatabase, chatDatabase, userRef;
    private ValueEventListener userListener, chatListener;
    // Will handle old/new messages between users
    private Query messagesDatabase;
    private ChildEventListener messagesListener;
    private MessageAdapter messagesAdapter;
    private final List<Message> messagesList = new ArrayList<>();
    // User data
    private String currentUserId, seen, user_img;
    private AlertDialog customDialog;
    // activity_chat views
    private EditText messageEditText;
    private TextView delete1, delete2;
    private RecyclerView recyclerView;
    private Button sendButton;
    private DocumentReference chatRef;
    private ImageView sendPictureButton, userImg;
    // chat_bar views
    private TextView  userName, userStatus;
    // Will be used on Notifications to detairminate if user has chat window open
    public static String otherUserId, otherUserToken;
    public static boolean running = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        running = true;

        messageEditText = findViewById(R.id.chat_message);
        recyclerView = findViewById(R.id.chat_recycler) ;
        userImg = findViewById(R.id.imageView2);
        userName = findViewById(R.id.textView3);
        userStatus = findViewById(R.id.textView5);
        btnChatBlock = findViewById(R.id.btnBlock);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        otherUserId = getIntent().getStringExtra("userid");
        otherUserToken = getIntent().getStringExtra("token");

        messagesDatabase = FirebaseDatabase.getInstance().getReference().child("Messages").child(currentUserId).child(otherUserId);

        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(otherUserId);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                user_img = snapshot.child("img").getValue().toString();
                String user_status = snapshot.child("status").getValue().toString();
                seen = snapshot.child("seen").getValue().toString();
                String email = snapshot.child("email").getValue().toString();

                userName.setText(snapshot.child("name").getValue().toString());
                userStatus.setText(user_status);
                Picasso.get().load(Uri.parse(user_img)).placeholder(R.mipmap.ic_avtar).into(userImg);
                if (user_status.equals("online")) {
                    userStatus.setVisibility(View.VISIBLE);
                    userStatus.setText(user_status);
                } else {
                    userStatus.setVisibility(View.VISIBLE);
                    userStatus.setText(getTimeAgo(Long.parseLong(user_status)));
                }
                if (email.isEmpty()) {
                    Toast.makeText(ChatActivity.this, "ffff", Toast.LENGTH_SHORT).show();
                } else {
                    chatRef = FirebaseFirestore.getInstance().collection("users").document(email);
                    chatRef.addSnapshotListener((value, error) -> {
                        String chat_status = value.getString("chat");

                        if (chat_status.equals("block")) {
                            btnChatBlock.setChecked(true);
                        }

                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnChatBlock.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                chatRef.update("chat", "block");
            } else {
                chatRef.update("chat", "unblock");
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        messagesAdapter = new MessageAdapter(messagesList);
        messagesAdapter.setMessageAdapterListner(this);
        recyclerView.setAdapter(messagesAdapter);
        // Will handle the send button to send a message

        sendButton = findViewById(R.id.chat_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        sendPictureButton = findViewById(R.id.chat_send_picture);
        sendPictureButton.setOnClickListener(view -> {
            Intent galleryIntent = new Intent();
            galleryIntent.setType("image/*");
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(Intent.createChooser(galleryIntent, "Select Image"), 1);
        });


        userImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FullScreenActivity.class);
                intent.putExtra("imageUrl", user_img);
                startActivity(intent);
            }
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
                    } else if (previousHeight < height) {
                        recyclerView.scrollToPosition(messagesList.size() - 1);

                        previousHeight = height;
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        running = true;

        Map map = new HashMap();
        map.put("online", "true");
        FirebaseDatabase.getInstance().getReference().child("admin").updateChildren(map);

        loadMessages();
        initDatabases();
    }

    @Override
    protected void onPause() {
        super.onPause();

        running = false;

      /*  Map map = new HashMap();
        map.put("online", ServerValue.TIMESTAMP);
        FirebaseDatabase.getInstance().getReference().child("admin").updateChildren(map);*/

        if (messagesList.size() > 0 && messageEditText.getText().length() > 0) {
            FirebaseDatabase.getInstance().getReference().child("Chat").child(currentUserId).child(otherUserId).child("typing").setValue(0);
        }

        removeListeners();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (seen.equals("no")) {
            Map map = new HashMap();
            map.put("seen", "yes");
            userRef.updateChildren(map);
        }

        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // NavUtils.navigateUpFromSameTask(this);
                finish();
                break;
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

                        Map messageMap = new HashMap();
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

                        Map userMap = new HashMap();
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

                            if (databaseError != null) {
                                //   Toast.makeText(ChatActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
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
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ChatActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        userDatabase.addValueEventListener(userListener);

        //Check if last message is unseen and mark it as seen with current timestamp

        chatDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(currentUserId).child(otherUserId);
        chatListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
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
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ChatActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        chatDatabase.addValueEventListener(chatListener);
    }

    private void loadMessages() {
        messagesList.clear();

        // Load/Update all messages between current and other user

        messagesListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {
                    Message message = dataSnapshot.getValue(Message.class);

                    messagesList.add(message);
                    messagesAdapter.notifyDataSetChanged();

                    recyclerView.scrollToPosition(messagesList.size() - 1);
                } catch (Exception e) {
                    Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                messagesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                messagesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                messagesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
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

            Map messageMap = new HashMap();
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

            Map userMap = new HashMap();
            userMap.put("Messages/" + currentUserId + "/" + otherUserId + "/" + pushId, messageMap);
            userMap.put("Messages/" + otherUserId + "/" + currentUserId + "/" + pushId, messageMap);

            userMap.put("Chat/" + currentUserId + "/" + otherUserId + "/message", message);
            userMap.put("Chat/" + currentUserId + "/" + otherUserId + "/timestamp", ServerValue.TIMESTAMP);
            userMap.put("Chat/" + currentUserId + "/" + otherUserId + "/seen", ServerValue.TIMESTAMP);

            userMap.put("Chat/" + otherUserId + "/" + currentUserId + "/message", message);
            userMap.put("Chat/" + otherUserId + "/" + currentUserId + "/timestamp", ServerValue.TIMESTAMP);
            userMap.put("Chat/" + otherUserId + "/" + currentUserId + "/seen", 0);

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

    @Override
    public void onclickListner(int postion) {
        askForDelete(postion);
    }

    private void askForDelete(int position) {

        View view = LayoutInflater.from(ChatActivity.this).inflate(R.layout.chat_user_detail, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setTitle("Choose one");
        delete1 = view.findViewById(R.id.textView6);
        delete2 = view.findViewById(R.id.textView7);

        customDialog = builder.create();
        customDialog.setCanceledOnTouchOutside(false);
        customDialog.show();

        Message message = messagesList.get(position);

        delete1.setOnClickListener(v -> {
            customDialog.dismiss();
            DatabaseReference messagesDatabase = FirebaseDatabase.getInstance().getReference().child("Messages").child(currentUserId).child(otherUserId);
            messagesDatabase.child(message.getMsg_key()).removeValue();
        });
        delete2.setOnClickListener(v -> {
            customDialog.dismiss();
            DatabaseReference messagesDatabase = FirebaseDatabase.getInstance().getReference().child("Messages").child(currentUserId).child(otherUserId);
            messagesDatabase.child(message.getMsg_key()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    DatabaseReference messagesDatabase = FirebaseDatabase.getInstance().getReference().child("Messages").child(otherUserId).child(currentUserId);
                    messagesDatabase.child(message.getMsg_key()).removeValue().addOnCompleteListener(task -> {
                        Toast.makeText(ChatActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });


    }


}