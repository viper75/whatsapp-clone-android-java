package org.viper75.whatsappclone.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.viper75.whatsappclone.databinding.SettingsActivityLayoutBinding;

import java.util.HashMap;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    private SettingsActivityLayoutBinding mSettingsActivityLayoutBinding;
    private TextInputEditText mUsernameInput;
    private TextInputEditText mStatusInput;
    private DatabaseReference mDatabaseReference;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSettingsActivityLayoutBinding = SettingsActivityLayoutBinding.inflate(getLayoutInflater());
        setContentView(mSettingsActivityLayoutBinding.getRoot());

        uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        initializeViews();

        retrieveUserInfo();
    }

    private void initializeViews() {
        mUsernameInput = mSettingsActivityLayoutBinding.usernameInput;
        mStatusInput = mSettingsActivityLayoutBinding.statusInput;

        Button updateBtn = mSettingsActivityLayoutBinding.saveProfileBtn;
        updateBtn.setOnClickListener(this::updateProfile);
    }

    private void retrieveUserInfo() {
        mDatabaseReference.child("Users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChild("username") && snapshot.hasChild("image")) {
                    String username = (String) snapshot.child("username").getValue();
                    String status = (String) snapshot.child("status").getValue();

                    mUsernameInput.setText(username);
                    mStatusInput.setText(status);
                } else if (snapshot.exists() && snapshot.hasChild("username")) {
                    String username = (String) snapshot.child("username").getValue();
                    String status = (String) snapshot.child("status").getValue();

                    mUsernameInput.setText(username);
                    mStatusInput.setText(status);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateProfile(View view) {
        String username = Objects.requireNonNull(mUsernameInput.getText()).toString();
        String status = Objects.requireNonNull(mStatusInput.getText()).toString();

        if (TextUtils.isEmpty(username)) {
            mUsernameInput.setError("Username is required.");
            return;
        }

        if (TextUtils.isEmpty(status)) {
            mStatusInput.setError("Status is required.");
            return;
        }

        HashMap<String, String> profileMap = new HashMap<>();
        profileMap.put("uid", uid);
        profileMap.put("username", username);
        profileMap.put("status", status);

        mDatabaseReference.child("Users").child(uid).setValue(profileMap).addOnCompleteListener(task -> {
           if (task.isSuccessful()) {
                sendUserToMainActivity();
                Toast.makeText(this, "Profile updated successfully.", Toast.LENGTH_SHORT).show();
           } else {
               String message = Objects.requireNonNull(task.getException()).getMessage();
               assert message != null;
               Snackbar.make(mSettingsActivityLayoutBinding.getRoot(), message, BaseTransientBottomBar.LENGTH_LONG);
           }
        });
    }

    private void sendUserToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}