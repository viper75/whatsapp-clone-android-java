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
import org.viper75.whatsappclone.models.RequestStatus;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    public final static String EXTRA_PROFILE_UID = "profileUid";

    private ProfileActivityLayoutBinding mProfileActivityLayoutBinding;
    private DatabaseReference mUserDBRef;
    private DatabaseReference mFriendRequestsDBRef;
    private FirebaseUser mCurrentUser;
    private CircleImageView mProfileImage;
    private TextView mUsername;
    private TextView mStatus;
    private Button mRequestBtn;
    private RequestStatus mRequestStatus = RequestStatus.NEW;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProfileActivityLayoutBinding = ProfileActivityLayoutBinding.inflate(getLayoutInflater());
        setContentView(mProfileActivityLayoutBinding.getRoot());

        uid = Objects.requireNonNull(getIntent().getExtras()).getString(EXTRA_PROFILE_UID);
        assert uid != null;
        mUserDBRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        mFriendRequestsDBRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
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

        mRequestBtn.setOnClickListener(v -> {
            switch (mRequestStatus) {
                case NEW:
                    sendFriendRequest();
                    break;
                case REQUEST_SENT:
                    cancelFriendRequest();
                    break;
            }
        });
    }

    private void sendFriendRequest() {
        mFriendRequestsDBRef.child(mCurrentUser.getUid()).child(uid).child("request_status").setValue(RequestStatus.REQUEST_SENT.toString())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mFriendRequestsDBRef.child(uid).child(mCurrentUser.getUid()).child("request_status").setValue(RequestStatus.REQUEST_RECEIVED.toString())
                                .addOnCompleteListener(task1 -> {
                                   if (task1.isSuccessful()) {
                                       mRequestBtn.setText("Cancel friend request");
                                       mRequestStatus = RequestStatus.REQUEST_SENT;
                                   }
                                });
                    }
                });
    }

    private void cancelFriendRequest() {
        mFriendRequestsDBRef.child(mCurrentUser.getUid()).child(uid).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mFriendRequestsDBRef.child(uid).child(mCurrentUser.getUid()).removeValue().addOnCompleteListener(task1 -> {
                   if (task1.isSuccessful()) {
                       mRequestBtn.setText("Send Friend Request");
                       mRequestStatus = RequestStatus.NEW;
                   }
                });
            }
        });
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