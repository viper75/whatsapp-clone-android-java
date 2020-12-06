package org.viper75.whatsappclone.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.viper75.whatsappclone.R;
import org.viper75.whatsappclone.databinding.ContactItemLayoutBinding;
import org.viper75.whatsappclone.databinding.FindFriendsActivityLayoutBinding;
import org.viper75.whatsappclone.models.Contact;
import org.viper75.whatsappclone.viewholders.ContactItemViewHolder;

import java.util.Objects;

public class FindFriendsActivity extends AppCompatActivity {

    private FindFriendsActivityLayoutBinding mFindFriendsActivityLayoutBinding;
    private DatabaseReference mUsersDBRef;
    private RecyclerView mContactsRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFindFriendsActivityLayoutBinding = FindFriendsActivityLayoutBinding.inflate(getLayoutInflater());
        setContentView(mFindFriendsActivityLayoutBinding.getRoot());

        mUsersDBRef = FirebaseDatabase.getInstance().getReference().child("Users");

        initializeViews();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contact> options = new FirebaseRecyclerOptions.Builder<Contact>()
                .setQuery(mUsersDBRef, Contact.class)
                .build();

        FirebaseRecyclerAdapter<Contact, ContactItemViewHolder> adapter = new FirebaseRecyclerAdapter<Contact, ContactItemViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ContactItemViewHolder holder, int position, @NonNull Contact contact) {
                holder.getMUsername().setText(contact.getUsername());
                holder.getMStatus().setText(contact.getStatus());

                Picasso.get().load(contact.getImage()).placeholder(R.drawable.default_profile_image)
                        .into(holder.getMProfileImage());

                holder.getMContactItem().setOnClickListener(v -> {
                    String friend_uid = getRef(position).getKey();
                    Intent intent = new Intent(FindFriendsActivity.this, ProfileActivity.class);
                    intent.putExtra(ProfileActivity.EXTRA_PROFILE_UID, friend_uid);
                    startActivity(intent);
                });
            }

            @NonNull
            @Override
            public ContactItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                ContactItemLayoutBinding contactItemLayoutBinding = ContactItemLayoutBinding
                        .inflate(getLayoutInflater(), parent, false);
                return new ContactItemViewHolder(contactItemLayoutBinding);
            }
        };

        mContactsRV.setAdapter(adapter);

        adapter.startListening();
    }

    private void initializeViews() {
        Toolbar toolbar = mFindFriendsActivityLayoutBinding.findFriendsAppBar.mainAppBar;
        toolbar.setTitle(R.string.find_friends);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        mContactsRV = mFindFriendsActivityLayoutBinding.contactsRv;
        mContactsRV.setLayoutManager(new LinearLayoutManager(this));
    }
}