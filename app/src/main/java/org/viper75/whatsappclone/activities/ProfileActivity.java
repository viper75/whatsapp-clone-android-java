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
    private DatabaseReference mContactsDBRef;
    private FirebaseUser mCurrentUser;
    private CircleImageView mProfileImage;
    private TextView mUsername;
    private TextView mStatus;
    private Button mRequestBtn;
    private Button mCancelRequestBtn;
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
        mContactsDBRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        initializeViews();

        retrieveUserInfo();
    }

    private void initializeViews() {
        mProfileImage = mProfileActivityLayoutBinding.profileImageCiv;
        mUsername = mProfileActivityLayoutBinding.profileUsernameTv;
        mStatus = mProfileActivityLayoutBinding.profileStatusTv;
        mRequestBtn = mProfileActivityLayoutBinding.profileSendMessageBtn;
        mCancelRequestBtn = mProfileActivityLayoutBinding.declineMessageRequestBtn;

        if (mCurrentUser.getUid().equals(uid)) {
            mRequestBtn.setVisibility(View.INVISIBLE);
        }

        mCancelRequestBtn.setOnClickListener(v -> cancelFriendRequest());

        mRequestBtn.setOnClickListener(v -> {
            switch (mRequestStatus) {
                case NEW:
                    sendFriendRequest();
                    break;
                case REQUEST_SENT:
                    cancelFriendRequest();
                    break;
                case REQUEST_RECEIVED:
                    acceptFriendRequest();
                    break;
                case FRIENDS:
                    deleteContact();
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
                       mCancelRequestBtn.setVisibility(View.INVISIBLE);
                   }
                });
            }
        });
    }

    private void acceptFriendRequest() {
        mContactsDBRef.child(mCurrentUser.getUid()).child(uid).child("Contacts").setValue("saved").addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mContactsDBRef.child(uid).child(mCurrentUser.getUid()).child("Contacts").setValue("saved").addOnCompleteListener(task1 -> {
                   if (task1.isSuccessful()) {
                       mFriendRequestsDBRef.child(mCurrentUser.getUid()).child(uid).removeValue().addOnCompleteListener(task2 -> {
                           if (task2.isSuccessful()) {
                               mFriendRequestsDBRef.child(uid).child(mCurrentUser.getUid()).removeValue().addOnCompleteListener(task3 -> {
                                   if (task3.isSuccessful()) {
                                       mRequestBtn.setText("Delete this Contact");
                                       mRequestStatus = RequestStatus.FRIENDS;
                                       mCancelRequestBtn.setVisibility(View.INVISIBLE);
                                   }
                               });
                           }
                       });
                   }
                });
            }
        });
    }

    private void deleteContact() {
        mContactsDBRef.child(mCurrentUser.getUid()).child(uid).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mContactsDBRef.child(uid).child(mCurrentUser.getUid()).removeValue().addOnCompleteListener(task1 -> {
                   if (task1.isSuccessful()) {
                       mRequestBtn.setText("Send friend request");
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

        mFriendRequestsDBRef.child(mCurrentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(uid).exists()) {
                    String requestStatusStr = (String) snapshot.child(uid).child("request_status").getValue();
                    RequestStatus requestStatus = Enum.valueOf(RequestStatus.class, requestStatusStr);

                    switch (requestStatus) {
                        case REQUEST_SENT:
                            mRequestBtn.setText("Cancel friend request");
                            mRequestStatus = RequestStatus.REQUEST_SENT;
                            break;
                        case REQUEST_RECEIVED:
                            mRequestBtn.setText("Accept Friend Request");
                            mRequestStatus = RequestStatus.REQUEST_RECEIVED;
                            mCancelRequestBtn.setVisibility(View.VISIBLE);
                            break;
                        case NEW:
                            mRequestBtn.setText("Send friend request");
                            mRequestStatus = RequestStatus.NEW;
                            break;
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mContactsDBRef.child(mCurrentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(uid).exists()) {
                    mRequestBtn.setText("Delete this Contact");
                    mRequestStatus = RequestStatus.FRIENDS;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}