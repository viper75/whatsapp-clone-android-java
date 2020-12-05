package org.viper75.whatsappclone.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.viper75.whatsappclone.R;
import org.viper75.whatsappclone.databinding.SettingsActivityLayoutBinding;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private static final int PROFILE_IMAGE_REQUEST_CODE = 1;

    private SettingsActivityLayoutBinding mSettingsActivityLayoutBinding;
    private TextInputEditText mUsernameInput;
    private TextInputEditText mStatusInput;
    private CircleImageView mProfileImage;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSettingsActivityLayoutBinding = SettingsActivityLayoutBinding.inflate(getLayoutInflater());
        setContentView(mSettingsActivityLayoutBinding.getRoot());

        uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference().child("Profile Images");

        initializeViews();

        retrieveUserInfo();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PROFILE_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            assert result != null;
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                uploadProfileImage(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Snackbar.make(mSettingsActivityLayoutBinding.getRoot(), Objects.requireNonNull(error.getMessage()),
                        BaseTransientBottomBar.LENGTH_LONG);
            }
        }
    }

    private void initializeViews() {
        mUsernameInput = mSettingsActivityLayoutBinding.usernameInput;
        mStatusInput = mSettingsActivityLayoutBinding.statusInput;

        Button updateBtn = mSettingsActivityLayoutBinding.saveProfileBtn;
        updateBtn.setOnClickListener(this::updateProfile);

        mProfileImage = mSettingsActivityLayoutBinding.profileImage;
        mProfileImage.setOnClickListener(this::uploadProfilePic);
    }

    private void uploadProfilePic(View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PROFILE_IMAGE_REQUEST_CODE);
    }

    private void retrieveUserInfo() {
        mDatabaseReference.child("Users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChild("username") && snapshot.hasChild("image")) {
                    String username = (String) snapshot.child("username").getValue();
                    String status = (String) snapshot.child("status").getValue();
                    String imageUrl = (String) snapshot.child("image").getValue();

                    mUsernameInput.setText(username);
                    mStatusInput.setText(status);

                    Picasso.get().load(imageUrl).placeholder(R.drawable.default_profile_image).into(mProfileImage);
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

    private void uploadProfileImage(Uri imageUri) {
        mStorageReference.child(uid + ".jpg").putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    Task<Uri> downloadUrlTask = Objects.requireNonNull(Objects.requireNonNull(taskSnapshot.getMetadata())
                            .getReference()).getDownloadUrl();

                    downloadUrlTask.addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();

                        mDatabaseReference.child("Users").child(uid).child("image").setValue(downloadUrl)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(this, "Profile picture uploaded successfully.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        String message = Objects.requireNonNull(task.getException()).getMessage();
                                        Snackbar.make(mSettingsActivityLayoutBinding.getRoot(), message, BaseTransientBottomBar.LENGTH_LONG);
                                    }
                                });
                    });})
                .addOnFailureListener(exception -> {
                    String message = exception.getMessage();
                    Snackbar.make(mSettingsActivityLayoutBinding.getRoot(), message, BaseTransientBottomBar.LENGTH_LONG);
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