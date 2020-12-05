package org.viper75.whatsappclone.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.viper75.whatsappclone.databinding.GroupChatsActivityLayoutBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class GroupChatsActivity extends AppCompatActivity {

    public static final String EXTRA_GROUP_NAME = "groupName";

    private GroupChatsActivityLayoutBinding mGroupChatsActivityLayoutBinding;
    private EditText mGroupMessageInput;
    private TextView mGroupMessages;
    private ScrollView mScrollView;
    private String mGroupName;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mGroupsDatabaseReference;
    private String mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGroupChatsActivityLayoutBinding = GroupChatsActivityLayoutBinding.inflate(getLayoutInflater());
        setContentView(mGroupChatsActivityLayoutBinding.getRoot());

        mGroupName = Objects.requireNonNull(getIntent().getExtras()).getString(EXTRA_GROUP_NAME);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mGroupsDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Groups").child(mGroupName);
        initializeViews();

        getCurrentUserInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();


        mGroupsDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    displayMessages(snapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    displayMessages(snapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void displayMessages(DataSnapshot snapshot) {
        Iterator<DataSnapshot> iterator = snapshot.getChildren().iterator();

        while (iterator.hasNext()) {
            String date = Objects.requireNonNull(iterator.next().getValue()).toString();
            String message = Objects.requireNonNull(iterator.next().getValue()).toString();
            String time = Objects.requireNonNull(iterator.next().getValue()).toString();
            String username = Objects.requireNonNull(iterator.next().getValue()).toString();

            mGroupMessages.append(username + "\n" + message + "\n" + time + "\t" + date + "\n\n\n");
        }
        mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
    }

    private void initializeViews() {
        Toolbar toolbar = mGroupChatsActivityLayoutBinding.groupsAppBar.mainAppBar;
        toolbar.setTitle(mGroupName);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        mGroupMessageInput = mGroupChatsActivityLayoutBinding.messageInput;
        mGroupMessages = mGroupChatsActivityLayoutBinding.groupMessages;
        mScrollView = mGroupChatsActivityLayoutBinding.scrollView;

        ImageButton sendMsgBtn = mGroupChatsActivityLayoutBinding.sendMessageBtn;
        sendMsgBtn.setOnClickListener(this::sendGroupMessage);
    }

    private void getCurrentUserInfo() {
        mDatabaseReference.child("Users").child(mCurrentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.child("username").exists()) {
                    mUsername = (String) snapshot.child("username").getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendGroupMessage(View view) {
        String message = mGroupMessageInput.getText().toString();

        if (TextUtils.isEmpty(message)) {
            Toast.makeText(this, "Cannot send a blank message!", Toast.LENGTH_SHORT).show();
            return;
        }

        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        String messageKey = mGroupsDatabaseReference.push().getKey();

        Map<String, Object> groupMessageKey = new HashMap<>();
        mGroupsDatabaseReference.updateChildren(groupMessageKey);

        Map<String, Object> messageObject = new HashMap<>();
        messageObject.put("message", message);
        messageObject.put("date", dateFormat.format(calendar.getTime()));
        messageObject.put("time", timeFormat.format(calendar.getTime()));
        messageObject.put("username", mUsername);

        assert messageKey != null;
        mGroupsDatabaseReference.child(messageKey).updateChildren(messageObject)
                .addOnCompleteListener(task -> {
                   if (task.isSuccessful()) {
                        mGroupMessageInput.setText("");
                        mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                   } else {
                       Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show();
                   }
                });
    }
}