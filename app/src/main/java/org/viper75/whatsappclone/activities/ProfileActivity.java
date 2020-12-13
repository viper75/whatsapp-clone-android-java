package org.viper75.whatsappclone.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.viper75.whatsappclone.databinding.ProfileActivityLayoutBinding;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    public final static String EXTRA_PROFILE_UID = "profileUid";

    private ProfileActivityLayoutBinding mProfileActivityLayoutBinding;
    private DatabaseReference mUserDBRef;
    private FirebaseUser mCurrentUser;
    private CircleImageView mProfileImage;
    private TextView mUsername;
    private TextView mStatus;
    private Button mRequestBtn;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProfileActivityLayoutBinding = ProfileActivityLayoutBinding.inflate(getLayoutInflater());
        setContentView(mProfileActivityLayoutBinding.getRoot());

        uid = Objects.requireNonNull(getIntent().getExtras()).getString(EXTRA_PROFILE_UID);
        assert uid != null;
        mUserDBRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        initializeViews();

        retrieveUserInfo();
    }

    private void initializeViews() {
        mProfileImage = mProfileActivityLayoutBinding.profileImageCiv;
        mUsername = mProfileActivityLayoutBinding.profileUsernameTv;
        mStatus = mProfileActivityLayoutBinding.profileStatusTv;
        mRequestBtn = mProfileActivityLayoutBinding.profileSendMessageBtn;

        if (mCurrentUser.getUid().equals(uid)) {
            mRequestBtn.setVisibility(View.INVISIBLE);
        }
    }

    private void retrieveUserInfo() {
        mUserDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = (String) snapshot.child("username").getValue();
                String status = (String) snapshot.child("status").getValue();

                mUsername.setText(username);
                mStatus.setText(status);

                if (snapshot.child("image").exists()) {
                    String imageUrl = (String) snapshot.child("image").getValue();
                    Picasso.get().load(imageUrl).into(mProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}